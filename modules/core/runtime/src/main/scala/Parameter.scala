
package seer
// package core //switch to core package?
package actor


import akka.actor._
import akka.util._
import scala.reflect.ClassTag
import collection.mutable.ListBuffer
import collection.mutable.HashSet
import concurrent.Await
import scala.util.Success
import concurrent.duration._
import akka.pattern.ask


object Parameter {
  def apply[T: ClassTag](v:T, path:String) = {
    new Parameter(v,path)
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
  
}



object ParameterActor {
  case class Save(name:String)

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

  var value:T = initialValue
  
  val instances = HashSet[Parameter[T]]()
  var changeCallbacks:ListBuffer[T=>Unit] = ListBuffer()

  def receive = {
    case "get" => sender() ! value
    case newValue:T => 
      value = newValue
      instances.foreach(_.setCachedValue(value))
      changeCallbacks.foreach(_(value))

    case p:Parameter[T] => instances += p

    case f:(T=>Unit) => if(!changeCallbacks.contains(f)) changeCallbacks += f
    case "clearCallbacks" => changeCallbacks.clear

    case Save(name) => 
  }

}