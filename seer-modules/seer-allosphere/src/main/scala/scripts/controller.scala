

import com.fishuyo.seer._
import allosphere._
import allosphere.actor._
import dynamic._
import graphics._
import io._
import spatial._
import spatial._


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory

object Script extends SeerScript{

	// val actor = livecluster.Controller.systemm.actorOf(Props( new Publisher()), name = "controller_script")
	val actor = system.actorOf(Props( new Publisher()), name = "controller_script")

	override def preUnload(){
		actor ! Kill
	}
	override def draw(){
		Sphere().draw
	}

	override def animate(dt:Float){
		actor ! Mouse.y().toFloat
		actor ! Camera.nav.pos
	}

	Mouse.clear
	Mouse.use
}


// class Loader extends Actor with ActorLogging {
//   import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
//   val mediator = DistributedPubSubExtension(Node.systemm).mediator
//   // subscribe to the topic named "state"
//   mediator ! Subscribe("script", self)
 
//   def receive = {
//     case SubscribeAck(Subscribe("script", None, `self`)) ⇒
//       context become ready
//   }
 
//   def ready: Actor.Receive = {
//     case s: String =>
//       Node.loader.load(s)
//   }
// }

class Publisher extends Actor {
  import DistributedPubSubMediator.Publish
  // activate the extension
  // val mediator = DistributedPubSubExtension(livecluster.Controller.systemm).mediator
  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case f:Float => mediator ! Publish("io", f)
    case pos:Vec3 => mediator ! Publish("io", Array(pos.x,pos.y,pos.z) )
  }
}




Script