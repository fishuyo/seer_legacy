
package seer.actor

import akka.actor._

import better.files._
import io.methvin.better.files._
// import FileWatcher._

// import java.nio.file._
// import java.nio.file.StandardWatchEventKinds._
// import com.sun.nio.file._

// class RMonitor(file:File) extends RecursiveFileMonitor(file) {
//   override def close() = { this.getListener() }
// }

object Monitor {

  val watchers = collection.mutable.HashMap[String,RecursiveFileMonitor]()

  implicit val sys = System()
  // val monitorActor = System().actorOf(MonitorActor(concurrency = 2))

  def apply(path:String, rec:Boolean=false)(f:(File)=>Unit){
    val myDir = File(path)
    val watcher = new RecursiveFileMonitor(myDir) {
      override def onCreate(file: File, count: Int) = f(file) //println(s"$file got created")
      override def onModify(file: File, count: Int) = f(file) //println(s"$file got modified $count times")
      override def onDelete(file: File, count: Int) = println(s"$file got deleted")
    }
    watchers(path) = watcher
    import scala.concurrent.ExecutionContext.Implicits.global
    watcher.start()
    // //This will receive callbacks for just the one file
    // monitorActor ! RegisterCallback(
    //   event = ENTRY_MODIFY,
    //   modifier = Some(SensitivityWatchEventModifier.HIGH), //None,
    //   path = Paths.get(path),
    //   callback = f,
    //   recursive = rec
    // )
  }

  def stop(path:String, rec:Boolean=false){
    try {
      if(watchers.contains(path)){
        val w = watchers(path)
        w.close() // calls watchservice.close
        // w.stop() // same as close
        watchers.remove(path)
      }
    } catch { case e:Exception => println(e)}
    // monitorActor ! UnRegisterCallback(
    //   ENTRY_MODIFY,
    //   recursive = rec,
    //   path = Paths.get(path)
    //   )
  }

  def kill(){
    // monitorActor ! Kill
  }
}

// val watcher: ActorRef = (home/"Downloads").newWatcher(recursive = true)

// // register partial function for an event
// watcher ! on(EventType.ENTRY_DELETE) {
//  case file if file.isDirectory => println(s"$file got deleted")
// }

// // watch for multiple events
// watcher ! when(events = EventType.ENTRY_CREATE, EventType.ENTRY_MODIFY) {
//  case (EventType.ENTRY_CREATE, file, count) => println(s"$file got created")
//  case (EventType.ENTRY_MODIFY, file, count) => println(s"$file got modified $count times")
// }