package com.fishuyo.seer
package dynamic

import actor._

import graphics.Animatable
import graphics.Scene
import graphics.RenderGraph
import audio.AudioSource
import audio.Audio

import java.io.File
import scala.io.Source

import scala.language.dynamics

import reflect.runtime.universe._
import reflect.runtime.currentMirror
import tools.reflect.ToolBox

import com.twitter.util.Eval

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import concurrent.Await
import concurrent.duration._

import collection.mutable.HashMap
import collection.mutable.ListBuffer

/**
  * ScriptLoaderActor companion object
  */
object ScriptLoaderActor {
  case class Path(path:String, reloadOnChange:Boolean)
  case class Code(code:String)
  case object Load
  case object Reload
  case object Unload

  // def props = propsEval
  def props = propsToolbox
  def propsEval = Props(new ScriptLoaderActor(new EvalScriptLoader()))
  def propsToolbox = Props(new ScriptLoaderActor(new ToolboxScriptLoader()))
}
/**
 * ScriptLoaderActor, each responsible for compiling and running
 * a script file or chunk of code
 */
class ScriptLoaderActor(val loader:ScriptLoader) extends Actor with ActorLogging {
  import ScriptLoaderActor._
  def receive = {
    case Path(path, reloadOnChange) =>
      log.info(s"path $path")
      if(reloadOnChange) Monitor(path){ (p) => self ! Reload }
      loader.setPath(path)
    case Code(code) => 
      loader.setCode(code)
    case Load | "load" =>
      log.info("loading..");
      loader.reload()
    case Reload | "reload" => 
      log.info("reloading..");
      loader.reload()
    case Unload | "unload" => loader.unload()

    case x => log.warning("Received unknown message: {}", x)
  }
}

/**
  * ScriptLoader trait 
  */
trait ScriptLoader {

  var loaded = false
  var code=""
  var path:Option[String] = None
  var file:Option[File] = None
  var obj:AnyRef = null

  def setPath(s:String) = path = Some(s)
  def setCode(s:String) = code = s
  def getCode() = {
    if(path.isDefined) code = Source.fromFile(new File(path.get)).mkString
    val importString = ScriptManager.imports.flatMap( (i) => s"import $i\n").mkString
    importString + code
  }

  // def load(){ //just using reload only
  //   try{
  //     obj = compile()
  //     // println(s"load $obj")
  //     obj match {
  //       case s:SeerScript =>
  //         s.load()
  //         loaded = true
  //       case a:ActorRef =>
  //         a ! "load"
  //         loaded = true
  //       case l:List[ActorRef] =>
  //         l.foreach{ case a => a ! "load" }
  //         loaded = true
  //       case c:Class[_]  =>
  //         val name = c.getSimpleName
  //         println( s"got class: $name")
  //       case obj => println(s"Unrecognized return value from script: $obj")
  //     }
  //   } catch { case e:Exception => loaded = false; println(e.getMessage) } 
  // }
  def reload(){
    try{
      // obj match {
      //   case s:SeerScript => s.preUnload()
      //   case a:ActorRef => a ! "preunload" //akka.actor.PoisonPill
      //   case _ => ()
      // }
      val ret = compile()
      ret match{
        case s:Script =>
          unload
          obj = ret
          s.load()
          loaded = true
        case s:SeerScript =>
          unload
          obj = ret
          s.load()
          loaded = true
        case a:ActorRef =>
          unload
          obj = ret
          a ! "load"
          loaded = true
        case l:List[ActorRef] =>
          unload
          obj = ret
          l.foreach{ case a => a ! "load" }
          loaded = true
        case c:Class[_] if c.getSuperclass == classOf[SeerActor] =>
          val r = ".*\\$(.*)\\$.".r
          val r(simple) = c.getName
          val id = s"live.$simple.${util.Random.int()}"
          // println( s"got class: $simple")
          val a = System().actorOf( SeerActor.props(c), id )
          unload
          obj = a
          a ! SeerActor.Name(id)
          a ! "load"
          loaded = true
        case obj => println(s"Unrecognized return value from script: $obj")

      }
    } catch { case e:Exception => loaded = false; println("Exception in script: " +e.getMessage) }
  }
  def unload(){
    obj match {
      case s:Script => 
        s.unload()
        obj = null
        loaded = false
      case s:SeerScript =>
        s.unload()
        obj = null
        loaded = false
      case a:ActorRef =>
        a ! "unload"
        a ! akka.actor.PoisonPill
        obj = null
        loaded = false
      case l:List[ActorRef] =>
        l.foreach{ case a =>
          a ! "unload"
          a ! akka.actor.PoisonPill
        }
        loaded = false
      case _ => ()
    }
  }

  def compile():AnyRef
}

/**
  * Toolbox implementation of a ScriptLoader
  */
class ToolboxScriptLoader extends ScriptLoader {
  val toolbox = ScriptManager.toolbox //currentMirror.mkToolBox() 

  def compile() = {
    val source = getCode()
    val tree = toolbox.parse(source)
    val script = toolbox.eval(tree).asInstanceOf[AnyRef]
    script
  } 
}

/**
  * Twitter Eval implementation of a ScriptLoader
  */
object EvalScriptLoader {
  val eval = new Eval()
  def apply() = eval
}
class EvalScriptLoader extends ScriptLoader {
  def compile() = {
    val source = getCode()
    val script = EvalScriptLoader.eval[AnyRef](source)  //.inPlace[AnyRef](source)
    // val script = Eval[AnyRef](source,false)
    script
  }
}




