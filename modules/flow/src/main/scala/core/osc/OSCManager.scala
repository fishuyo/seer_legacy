package seer
package flow

import akka.actor._

import de.sciss.osc.Message

import collection.mutable.HashMap

import seer.actor._

object OSCManager {
  // val actorRef = system.actorOf(OSCManagerActor.props, "OSCManager")
  val actorRef = System().actorOf(OSCManagerActor.props, "OSCManager")
  def apply() = actorRef
}

// OSCManagerActor manages open receive ports and sharing them with OSCActors
// it must keep track of open oscRecv objects and disconnect when all actors stop
object OSCManagerActor {
  case class Bind(port:Int, handler:OSC.OSCHandler)
  case class Unbind(port:Int, handler:OSC.OSCHandler)

  def props = Props[OSCManagerActor]
}

class OSCManagerActor extends Actor with ActorLogging {
  import OSCManagerActor._

  var receivers = HashMap[Int,OSCRecv]()

  def receive = {
    case Bind(port,handler) => 
      var oscRecv = receivers.getOrElseUpdate(port, {
        val r = new OSCRecv
        r.listen(port)
        println(s"Listening on port ${port}")
        r
      })
      oscRecv.bind(handler)

    case Unbind(port,handler) => receivers.get(port).foreach { case r =>
      r.unbind(handler)
      if(r.handlers.length == 0){
        println(s"disconnecting socket at port $port")
        r.disconnect
        receivers -= port
      }
    }
  }
  
  override def postStop() = {
    // on stop disconnect any receivers, ignore exceptions
    try { 
      receivers.values.foreach( _.disconnect ) 
    } catch { case e:Exception => println(e) }
    super.postStop()
  }
}


