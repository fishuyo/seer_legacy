
package com.fishuyo.seer
package actor

import akka.actor._

import better.files._, FileWatcher._
import java.nio.file.{Path, StandardWatchEventKinds => EventType, WatchEvent}


object Monitor {

  implicit val system:ActorSystem = System()

  def apply(path:String, rec:Boolean=false)(f:(File)=>Unit): Unit = {
    
    val watch = path.toFile.newWatcher(recursive = rec)
    
    watch ! when(events = EventType.ENTRY_CREATE, EventType.ENTRY_MODIFY) {
      case (_, file) => f(file)
    }
  }

  def stop(path:String, rec:Boolean=false): Unit ={
  }

  def kill(): Unit ={
  }
}

// import com.beachape.filemanagement.MonitorActor
// import com.beachape.filemanagement.RegistryTypes._
// import com.beachape.filemanagement.Messages._

// import java.nio.file._
// import java.nio.file.StandardWatchEventKinds._
// import com.sun.nio.file._

// object Monitor {

//   val monitorActor = System().actorOf(MonitorActor(concurrency = 2))

//   def apply(path:String, rec:Boolean=false)(f:Callback): Unit ={
//     //This will receive callbacks for just the one file
//     monitorActor ! RegisterCallback(
//       event = ENTRY_MODIFY,
//       modifier = Some(SensitivityWatchEventModifier.HIGH), //None,
//       path = Paths.get(path),
//       callback = f,
//       recursive = rec
//     )
//   }

//   def stop(path:String, rec:Boolean=false): Unit ={
//     monitorActor ! UnRegisterCallback(
//       ENTRY_MODIFY,
//       recursive = rec,
//       path = Paths.get(path)
//       )
//   }

//   def kill(): Unit ={
//     monitorActor ! Kill
//   }
// }