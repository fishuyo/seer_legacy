// package com.fishuyo.seer
// package dynamic

// import actor._

// import graphics.Animatable
// import graphics.Scene
// import graphics.RenderGraph
// import audio.AudioSource
// import audio.Audio

// import java.io.File
// import scala.io.Source

// import scala.language.dynamics

// import reflect.runtime.universe._
// import reflect.runtime.currentMirror
// import tools.reflect.ToolBox

// import akka.actor._
// import akka.event.Logging
// import akka.pattern.ask
// import akka.util.Timeout

// import concurrent.Await
// import concurrent.duration._

// /**
//  * Object for generating scala script loaders which are compiled
//  * and added to the Scene on file modification
//  */
// object SeerScriptLoader {
//   case class RunFile(path:String, reloadOnChange:Boolean)
//   case class RunCode(code:String)
//   case class Reload(code:String)

//   val toolbox = currentMirror.mkToolBox() 
//   val manager = System().actorOf( Props[ScriptManager], name="ScriptManager" )
  
//   implicit val timeout = Timeout(4 seconds)

//   def imports() = {
//     """
//       import com.fishuyo.seer._
//       import com.fishuyo.seer.graphics._
//       import com.fishuyo.seer.dynamic._
//       import com.fishuyo.seer.spatial._
//       import com.fishuyo.seer.io._
//       import com.fishuyo.seer.util._
//     """
//   }

//   def apply() = Await.result(manager ? "create", 3 seconds).asInstanceOf[ActorRef]

//   def apply(path:String, reloadOnChange:Boolean=true) = {
//     val f = manager ? "create"
//     val actor = Await.result(f, 3 seconds).asInstanceOf[ActorRef]
//     actor ! RunFile(path,reloadOnChange)
//     actor
//   }
// }
// import SeerScriptLoader._


// /**
//  * ScriptManager Actor, create script loaders in manager context to handle errors
//  */
// class ScriptManager extends Actor with ActorLogging {
//   import akka.actor.SupervisorStrategy._
//   import scala.concurrent.duration._

//   val scripts = HashMap[String,ScriptLoaderActor]()

//   override val supervisorStrategy =
//     OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled=false) {
//       case _:scala.tools.reflect.ToolBoxError => logToolboxErrorLocation(); Resume
//       case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
//     }

//   def receive = {
//     case "create" => 
//       val nam = "script"+scripts.size
//       val loader = context.actorOf( Props[ScriptLoaderActor], name)
//       scripts(name) = loader
//       sender ! loader

//     case x => log.warning("Received unknown message: {}", x)
//   }

//   def logToolboxErrorLocation(){
//     if(toolbox.frontEnd.hasErrors){
//       toolbox.frontEnd.infos.foreach{ case info =>
//         val line = info.pos.line - imports().split("\n").length + 1
//         val msg = s"""
//           ${info.msg}
//           at line ${line}:${info.pos.column}
//           ${info.pos.lineContent}
//           ${info.pos.lineCaret} 
//         """
//         log.error(msg)
//       }
//     }
//   }
// }

// /**
//  * ScriptLoader trait 
//  */
// trait ScriptLoader {
//   def setPath(path:String)
//   def setCode(code:String)
//   def load()
//   def reload()
//   def unload()
//   def compile()
//   def run()
// }

// /**
//  *
//  */
// trait ScriptLoaderToolbox extends ScriptLoader {
//   def load()
// }

// /**
//  * ScriptLoaderActor, each responsible for compiling and running
//  * a script file or chunk of code
//  */
// class ScriptLoaderActor(val loader:ScriptLoader) extends Actor with ActorLogging {

//   def receive = {
//     case Path(path, reloadOnChange) =>
//       if(reloadOnChange) Monitor(path){ (p) => self ! Reload }
//       script = Some(runFile(path))
//       load()

//     case Code(code) => script = Some(runCode(code)); load()
//     case Reload(code) => reload(code)

//     case x => log.warning("Received unknown message: {}", x)
//   }
// }


