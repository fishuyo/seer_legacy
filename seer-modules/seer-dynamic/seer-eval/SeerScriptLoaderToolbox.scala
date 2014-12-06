package com.fishuyo.seer
package dynamic

import actor._

import graphics.Animatable
import graphics.Scene
import audio.AudioSource
import audio.Audio

import java.io.File
import scala.io.Source

import scala.language.dynamics

import reflect.runtime.universe._
import reflect.runtime.currentMirror
import tools.reflect.ToolBox

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import concurrent.Await
import concurrent.duration._

/**
 * Object for generating scala script loaders which are compiled
 * and added to the Scene on file modification
 */
object ScriptLoader {
  case class RunFile(path:String, reloadOnChange:Boolean)
  case class RunCode(code:String)
  case class Reload(code:String)

  val toolbox = currentMirror.mkToolBox() 
  val manager = System().actorOf( Props[ScriptManager], name="ScriptManager" )
  
  implicit val timeout = Timeout(4 seconds)

  def imports() = {
    """
      import com.fishuyo.seer._
      import com.fishuyo.seer.graphics._
      import com.fishuyo.seer.dynamic._
      import com.fishuyo.seer.spatial._
      import com.fishuyo.seer.io._
      import com.fishuyo.seer.util._
    """
  }

  def apply() = Await.result(manager ? "create", 3 seconds).asInstanceOf[ActorRef]

  def apply(path:String, reloadOnChange:Boolean=true) = {
    val f = manager ? "create"
    val actor = Await.result(f, 3 seconds).asInstanceOf[ActorRef]
    actor ! RunFile(path,reloadOnChange)
    actor
  }
}
import ScriptLoader._


/**
 * ScriptManager Actor
 */
class ScriptManager extends Actor with ActorLogging {
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled=false) {
      case _:scala.tools.reflect.ToolBoxError => logToolboxErrorLocation(); Resume
      case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  def receive = {
    case "create" => 
      val loader = context.actorOf( Props[ScriptLoader] )
      sender ! loader

    case x => log.warning("Received unknown message: {}", x)
  }

  def logToolboxErrorLocation(){
    if(toolbox.frontEnd.hasErrors){
      toolbox.frontEnd.infos.foreach{ case info =>
        val line = info.pos.line - imports().split("\n").length + 1
        val msg = s"""
          ${info.msg}
          at line ${line}:${info.pos.column}
          ${info.pos.lineContent}
          ${info.pos.lineCaret} 
        """
        log.error(msg)
      }
    }
  }
}

/**
 * ScriptLoader Actor, each responsible for compiling and running
 * a script file or chunk of code
 */
class ScriptLoader extends Actor with ActorLogging {

  var loaded = false
  var path:Option[String] = None
  var script:Option[SeerScript] = None

  def receive = {
    case RunFile(path, reloadOnChange) =>
      if(reloadOnChange) Monitor(path){ (p) => self ! Reload("") }
      script = Some(runFile(path))
      load()

    case RunCode(code) => script = Some(runCode(code)); load()
    case Reload(code) => reload(code)
    case "script" => sender ! script

    case x => log.warning("Received unknown message: {}", x)
  }
     
  def runFile(filepath:String):SeerScript = {
    path = Some(filepath)
    val source = Source.fromFile(new File(filepath))
    val code = imports() + source.mkString
    val tree = toolbox.parse(code)
    val compiled = toolbox.compile(tree)
    val result = compiled()
    val script = result.asInstanceOf[SeerScript]
    return script
  }

  def runCode(source:String):SeerScript = {
    path = None
    val code = imports() + source
    val tree = toolbox.parse(code)
    val compiled = toolbox.compile(tree)
    val result = compiled()
    val script = result.asInstanceOf[SeerScript]
    return script
  } 

  def load(){
    if(script.isEmpty) return
    val s = script.get
    Scene.push(s)
    Audio().push(s)
    s.onLoad()
    loaded = true
  }
 
  def reload(code:String=""){
    if(script.isDefined) script.get.preUnload()
    var newscript:SeerScript = null
    if(path.isDefined){
      newscript = runFile(path.get)
    } else{
      newscript = runCode(code)
    }
    unload()
    script = Some(newscript)
    load()
  }

  def unload(){
    if(script.isEmpty) return
    val s = script.get
    Scene.remove(s)
    Audio().sources -= s
    s.onUnload()
    script = None
    loaded = false
  }

  def getScript() = Await.result(self ? "script", 3 seconds).asInstanceOf[Option[SeerScript]]

  override def preStart() = {
    log.debug("ScriptLoader Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "SL Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

}


class SeerScriptLoader(val scriptPath:String) {

  val toolbox = currentMirror.mkToolBox() 

  var loaded = false

  val file = new File(scriptPath)

  var script:SeerScript = null

  load()
      
  Monitor(scriptPath){ (p) =>
    reload
  }

  def imports() = {
    """
      import com.fishuyo.seer._
      import com.fishuyo.seer.graphics._
      import com.fishuyo.seer.dynamic._
      import com.fishuyo.seer.spatial._
      import com.fishuyo.seer.io._
      import com.fishuyo.seer.util._
    """
  }
  private def compile() = {
    val source = Source.fromFile(file)
    val tree = toolbox.parse(imports() + source.mkString)
    val script = toolbox.eval(tree).asInstanceOf[SeerScript]
    script
  }
  private def load() = {
    try{
      script = compile()
      loaded = true
      Scene.push(script)
      Audio().push(script)
      script.onLoad()
    } catch { 
      case e:Exception => loaded = false; println(e.getMessage)
    } 
  }
  
  def reload() = {
    try{
      if(script != null) script.preUnload()
      val newscript = compile()
      // newscript.copy(script)
      unload
      script = newscript
      loaded = true
      Scene.push(script)
      Audio().push(script)
    } catch { 
      case e:Exception => loaded = false; println(e.getMessage)
    }
  }

  def unload(){
    if(script != null){
      Scene.remove(script)
      Audio().sources -= script
      script.onUnload()
      script = null
      loaded = false
    }
  }
                       
}

// class SeerScriptTextLoader {

//   var loaded = false
//   var script:SeerScript = null

//   def load(code:String) = {
//     try{
//       script = Eval[SeerScript](code)
//       loaded = true
//       // Scene.push(script)
//       // Audio().push(script)
//       script.onLoad()
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     } 
//   }
  
//   def reload(code:String) = {
//     try{
//       if(script != null) script.preUnload()
//       val newscript = Eval[SeerScript](code)
//       unload
//       script = newscript
//       loaded = true
//       // Scene.push(script)
//       // Audio().push(script)
//     } catch { 
//       case e:Exception => loaded = false; println(e)
//     }
//   }

//   def unload(){
//     if(script != null){
//       // Scene.remove(script)
//       // Audio().sources -= script
//       script.onUnload()
//       script = null
//       loaded = false
//     }
//   }
                       
// }



