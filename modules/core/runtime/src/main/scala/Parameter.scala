
package seer
package actor

import akka.actor._
import akka.util._
import scala.reflect.ClassTag
import collection.mutable.ListBuffer
import collection.mutable.ArrayBuffer
import collection.mutable.HashSet
import concurrent.Await
import scala.util.Success
import concurrent.duration._
import akka.pattern.ask

import com.twitter.chill.KryoInjection


// XXX careful with mutable objects..
object Var {
  def apply[T: ClassTag](path:String, v:T) = {
    new Parameter(v,path)
  }
}


object Parameter {
  def apply[T: ClassTag](v:T, path:String) = {
    new Parameter(v,path)
  }

  def save(name:String, path:String) = {
    System().actorSelection(s"/user/p.$path") ! ParameterActor.Save(name)
  }

  def load(name:String, path:String) = {
    System().actorSelection(s"/user/p.$path") ! ParameterActor.Load(name)
  }
}

class Parameter[T: ClassTag](initialValue:T, path:String){
  val actorRef = ParameterActor(initialValue, path)
  private var cachedValue:T = initialValue
  
  updateCachedValue()
  actorRef ! this
  
  // def callback
  def apply() = cachedValue
  def update(v:T) = { 
    cachedValue = v
    actorRef ! v
  }
  def get() = apply()
  def set(v:T) = update(v)
  def :=(v:T) = update(v)

  def onChange(f:T=>Unit)={
    actorRef ! f 
  }

  def setCachedValue(v:T) = { cachedValue = v }
  def updateCachedValue() = {    
    implicit val t = Timeout(1.second)
    val f = actorRef ? "get" 
    Await.ready(f, 1.second).value.get match {
      case Success(value:T) => cachedValue = value
      case _ => 
    }
  }
  
  def save(name:String) = actorRef ! ParameterActor.Save(name)
  def load(name:String) = actorRef ! ParameterActor.Load(name)
}



object ParameterActor {
  case class Save(name:String)
  case class Load(name:String)

  // def props[T](v:T) = Props[Parameter[T]](v)
  def props[T: ClassTag](v:T) = Props(new ParameterActor[T](v))

  def apply[T: ClassTag](v:T, path:String):ActorRef = {
    var name = path.replaceAllLiterally("/",".")
    if(name.startsWith(".")) name = name.substring(1)
    if(name.isEmpty) name = s"${v.getClass.getSimpleName}"
    
    val f = System().actorSelection(s"/user/p.$name").resolveOne(1.second)
    Await.ready(f, 1.second).value.get match {
      case Success(a:ActorRef) => a
      case _ => System().actorOf(props(v), s"p.$name")
    }
    
  }

}

class ParameterActor[T: ClassTag](initialValue:T) extends Actor {
  import ParameterActor._

  println(s"new param actor: ${self.path.name}")
  
  var value:T = initialValue
  
  val instances = HashSet[Parameter[T]]()
  var changeCallbacks:ArrayBuffer[T=>Unit] = ArrayBuffer()

  def receive = {
    case "get" => sender() ! value
    case newValue:T => 
      value = newValue
      instances.foreach(_.setCachedValue(value))
      changeCallbacks.foreach(_(value))

    case p:Parameter[T] => instances += p

    case f:(T=>Unit) => if(!changeCallbacks.contains(f)) changeCallbacks += f
    case ("remove", f:(T=>Unit)) => changeCallbacks -= f
    case "clearCallbacks" => changeCallbacks.clear

    case Save(name) => Preset.save(name, self.path.name, value)
    case Load(name) => Preset.load[T](name, self.path.name).foreach(self ! _)

  }

}


object Preset {
  val pwd = os.pwd / "_presets"

  def save[T: ClassTag](name:String, path:String, value:T) = {
    try{
      val wd = pwd / name
      os.makeDir.all(wd)
      val bytes = KryoInjection(value)
      os.write.over(wd / path, bytes)
    } catch {
      case e:Exception => println(s"Error saving preset for: $name $path")
    }
  }

  def load[T: ClassTag](name:String, path:String):Option[T] = {
    try{
      val wd = pwd / name
      val bytes = os.read.bytes(wd / path)
      val decode = KryoInjection.invert(bytes)
      decode match {
        case Success(v:T) => return Some(v)
        case m => println(s"Preset load invert failed: $name $path $m  ${m.getClass.getSimpleName}"); return None
      }
    } catch {
      case e:Exception => println(s"Error loading preset for: $name $path")
    }
    None
  }

}