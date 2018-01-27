package com.fishuyo.seer
package dynamic

import actor._

import java.io.File

import scala.language.dynamics

import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import concurrent.Await
import concurrent.duration._

import collection.mutable.HashMap
import collection.mutable.ListBuffer

/**
 * ScriptDirectoryLoaderActor, each responsible for compiling and running
 * a script file or chunk of code
 */
class ScriptDirectoryLoaderActor extends Actor with ActorLogging {
  import ScriptLoaderActor._

  val scripts = HashMap[String,ActorRef]()

  def receive = {
    case Path(path, reloadOnChange) =>
      val file = new File(path)
      if(!file.isDirectory) log.error("Invalid directory..")

      log.info(s"loading $path")
      // for each scala file in directory
      file.listFiles.filter(_.getPath.endsWith(".scala")).foreach { case f =>
        val name = f.getName
        val loader = context.actorOf( ScriptLoaderActor.props, name)
        scripts(name) = loader
        loader ! Path(f.getPath,false)
        loader ! Load
      }

      if(reloadOnChange) Monitor(path){ (p) => 
        log.info(s"FileChanged: $p")
        val name = p.toFile.getName
        if(scripts.contains(name))
          scripts(name) ! Reload
        else{
          val loader = context.actorOf(ScriptLoaderActor.props, name)
          scripts(name) = loader
          loader ! Path(p.toFile.getPath,false)
          loader ! Load
        }
      }
      
    // case Load => loader.load()
    // case Reload => loader.reload()
    case Unload | "unload" => scripts.values.foreach { case a => a ! Unload }

    case x => log.warning("Received unknown message: {}", x)
  }
}


