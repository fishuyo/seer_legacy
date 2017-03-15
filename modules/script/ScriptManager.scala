package com.fishuyo.seer
package dynamic

import actor._

import java.io.File
import scala.io.Source

import scala.language.dynamics

// import reflect.runtime.universe._
import reflect.runtime.currentMirror
import tools.reflect.ToolBox

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import concurrent._
import concurrent.duration._
import scala.language.postfixOps

import collection.mutable.HashMap
import collection.mutable.ListBuffer

/**
  * Object for generating scala script loaders which are compiled
  * and added to the Scene on file modification
  */
object ScriptManager {
  import ScriptLoaderActor._

  case class Create(name:String="")

  val toolbox = currentMirror.mkToolBox() 
  val manager = System().actorOf( Props[ScriptManagerActor], name="ScriptManager" )
  
  implicit val timeout = Timeout(4 seconds)

  val imports = ListBuffer[String]()
  imports += "com.fishuyo.seer._"
  imports += "com.fishuyo.seer.dynamic._"
  imports += "com.fishuyo.seer.actor._"
  imports += "com.fishuyo.seer.spatial._"
  imports += "com.fishuyo.seer.graphics._"
  imports += "com.fishuyo.seer.audio._"
  imports += "com.fishuyo.seer.io._"
  imports += "com.fishuyo.seer.util._"
  imports += "scala.concurrent.duration._"
  imports += "collection.mutable.ListBuffer"
  imports += "collection.mutable.ArrayBuffer"
  imports += "collection.mutable.HashMap"

  def apply() = Await.result(manager ? Create(), 3 seconds).asInstanceOf[ActorRef]

  def load(path:String, reloadOnChange:Boolean=true) = {
    val f = manager ? Path(path,reloadOnChange)
    val actor = Await.result(f, 3 seconds).asInstanceOf[ActorRef]
    actor ! Load
    actor
  }
  def loadCode(code:String) = {
    val f = manager ? Create()
    val actor = Await.result(f, 3 seconds).asInstanceOf[ActorRef]
    actor ! Code(code)
    actor ! Load
    actor
  }

  def remote(address:Address)(path:String, reloadOnChange:Boolean=true){
    val remoteManager = System().actorSelection(address + "/user/ScriptManager")

    val file = new File(path)
    if(file.isDirectory){

      file.listFiles.filter(_.getPath.endsWith(".scala")).foreach { case f =>
        val fu = remoteManager ? Create(f.getName)
        val actor = Await.result(fu, 10 seconds).asInstanceOf[ActorRef]

        var code = Source.fromFile(f).mkString
        actor ! Code(code)
        actor ! Load
        if(reloadOnChange) Monitor(f.getPath){ (p) => 
          var code = Source.fromFile(f).mkString
          actor ! Code(code)
          actor ! Reload
        }
      }

    }else if(file.isFile){
      val f = remoteManager ? Create(file.getName)
      val actor = Await.result(f, 10 seconds).asInstanceOf[ActorRef]

      var code = Source.fromFile(file).mkString
      actor ! Code(code)
      actor ! Load
      if(reloadOnChange) Monitor(path){ (p) => 
        var code = Source.fromFile(file).mkString
        actor ! Code(code)
        actor ! Reload
      }
    } else {
      println("Invalid path..")
    }  

  }
}

/**
  * ScriptManager Actor, create script loaders in manager context to handle errors
  */
class ScriptManagerActor extends Actor with ActorLogging {
  import ScriptManager._
  import ScriptLoaderActor._
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  val scripts = HashMap[String,ActorRef]()
  val dirs = HashMap[String,ActorRef]()

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled=false) {
      case _:scala.tools.reflect.ToolBoxError => logToolboxErrorLocation(); Resume
      case t => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  def receive = {
    case Create(n) => 
      var name = n
      if(n.isEmpty) name += "script"+scripts.size
      if(scripts.isDefinedAt(name)) sender ! scripts(name)
      else {
        val loader = context.actorOf( ScriptLoaderActor.props, name)
        scripts(name) = loader
        sender ! loader
      }
    case Path(path,reload) =>
      val file = new File(path)
      val name = file.getName
      if(file.isDirectory){
        // log.info(s"create ScriptDirectoryLoaderActor for $path")
        if(dirs.isDefinedAt(name)) sender ! scripts(name)
        else {
          val loader = context.actorOf( Props[ScriptDirectoryLoaderActor], name)
          dirs(name) = loader
          loader ! Path(path,reload)
          sender ! loader
        }
      }else if(file.isFile){
        if(scripts.isDefinedAt(name)) sender ! scripts(name)
        else{
          val loader = context.actorOf( ScriptLoaderActor.props, name)
          scripts(name) = loader
          loader ! Path(path,reload)
          // loader ! Load
          sender ! loader
        }
      } else {
        log.error("Invalid path..")
      }  
    case x => () //log.warning("Received unknown message: {}", x)
  }

  def logToolboxErrorLocation(){
    if(toolbox.frontEnd.hasErrors){
      toolbox.frontEnd.infos.foreach{ case info =>
        val line = info.pos.line - imports.length //+ 1
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



