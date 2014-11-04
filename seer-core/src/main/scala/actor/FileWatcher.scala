
package com.fishuyo.seer
package actor

// import akka.actor.ActorSystem
import com.beachape.filemanagement.MonitorActor
import com.beachape.filemanagement.RegistryTypes._
import com.beachape.filemanagement.Messages._

import java.nio.file._
import java.nio.file.StandardWatchEventKinds._
import com.sun.nio.file._

object Monitor {

  val monitorActor = system.actorOf(MonitorActor(concurrency = 2))

  def apply(path:String)(f:Callback){

    //This will receive callbacks for just the one file
    monitorActor ! RegisterCallback(
      ENTRY_MODIFY,
      Some(SensitivityWatchEventModifier.HIGH), //None,
      recursive = false,
      path = Paths get path,
      f)
  }
}