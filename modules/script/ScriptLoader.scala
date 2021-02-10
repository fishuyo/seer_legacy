package seer
package script

import actor._

import java.io.File
import scala.io.Source

import reflect.runtime.universe._
import reflect.runtime.currentMirror
import tools.reflect.ToolBox

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import concurrent.Await
import concurrent.duration._

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
  case object Status

  def props = propsToolbox
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
      if(reloadOnChange) Monitor(path){ (f) => self ! Reload }
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
    case Status => 
      // if(loader.errors.isEmpty) loader.checkErrors()
      // sender ! loader.errors

    // case _ => log.warning("Received unknown message: {}", x)
  }
}

/**
  * ScriptLoader trait 
  */
trait ScriptLoader {

  var code=""
  var path:Option[String] = None
  var result:AnyRef = null              // result of evaluating code
  var errors:Seq[(Int,String)] = Seq()  // seq of lineNumber -> message tuples

  def setPath(s:String) = path = Some(s)
  def setCode(s:String) = code = s

  def getCode() = {
    if(path.isDefined) code = Source.fromFile(new File(path.get)).mkString
    val importString = ScriptManager.imports.flatMap( (i) => s"import $i\n").mkString
    importString + code
  }

  // compile / recompile and evaluate script
  def reload(){
    try{
      // notify running script about to reload
      result match {
        case s:SeerScript => s.preUnload()
        case a:ActorRef => a ! "preunload" //akka.actor.PoisonPill
        case _ => ()
      }

      errors = Seq()
      unload()
      val ret = eval[AnyRef]()
      ret match{
        case s:Script =>
          result = ret
          s.load()
        case s:SeerScript =>
          result = ret
          s.load()
        case a:ActorRef =>
          result = ret
          a ! "load"
        case l:List[ActorRef] =>
          result = ret
          l.foreach{ case a => a ! "load" }
        case c:Class[_] if c.getSuperclass == classOf[SeerActor] =>
          val r = ".*\\$(.*)\\$.".r
          val r(simple) = c.getName
          val id = s"live.$simple.${util.Random.int()}"
          val a = System().actorOf( SeerActor.props(c), id )
          result = a
          a ! SeerActor.Name(id)
          a ! "load"
        case x => println(s"Unrecognized return value from script: $x")
      }

    } catch { case e:Exception => 
      println("Exception in script: " + e.getMessage)
      val frame = e.getStackTrace.find{ e => e.getMethodName.contains("load") }.get
      errors = Seq((frame.getLineNumber, "RuntimeError: " + e.toString))
    }
  }

  def unload(){
    result match {
      case s:Script => 
        s.unload()
      case s:SeerScript =>
        s.unload()
      case a:ActorRef =>
        a ! "unload"
        a ! akka.actor.PoisonPill
      case l:List[ActorRef] =>
        l.foreach{ case a =>
          a ! "unload"
          a ! akka.actor.PoisonPill
        }
      case _ => ()
    }
  }

  def checkErrors(){}
  def eval[T]():T
}

/**
  * Toolbox implementation of a ScriptLoader
  */
class ToolboxScriptLoader extends ScriptLoader {
  val toolbox = ScriptManager.toolbox //currentMirror.mkToolBox() 

  def eval[T]() : T = {
    val source = getCode()
    val tree = toolbox.parse(source)
    toolbox.eval(tree).asInstanceOf[T]
  }
  
  override def checkErrors() = {
    if(toolbox.frontEnd.hasErrors){
      val errs = toolbox.frontEnd.infos.map { case info =>
        val line = info.pos.line
        val msg = s"""
          ${info.msg}
          ${info.pos.lineContent}
          ${info.pos.lineCaret} 
        """
        (line,msg)
      }.toSeq
      errors = errs
    } else errors = Seq()
  }

}


// import javax.script.ScriptEngineManager

// class DummyClass

// object Evaluator {
//   val engine = new ScriptEngineManager().getEngineByName("scala")
//   val settings = engine.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
//   settings.embeddedDefaults[DummyClass]
//   engine.eval("val x: Int = 5")
//   val thing = engine.eval("x + 9").asInstanceOf[Int]
// }



