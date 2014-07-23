
import com.fishuyo.seer._

import allosphere._
import allosphere.actor._

import graphics._
import dynamic._
import spatial._
import io._

import allosphere.livecluster.Node

import collection.mutable.ArrayBuffer

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

object SimulatorScript extends SeerScript{
	
  var t = 0.f

	val controllerStateListener:ActorRef = system.actorOf(Props( new ControllerListener()), name = "controllerlistener")

	var statePublisher:ActorRef = system10g.actorOf(Props( new StatePublisher()), name = "statepublisher")


	override def preUnload(){
		controllerStateListener ! PoisonPill
		statePublisher ! PoisonPill
	}

  override def animate(dt:Float){
		t += dt
		statePublisher ! Camera.nav
  }
}


class StatePublisher extends Actor {
  import DistributedPubSubMediator.Publish
  val mediator = DistributedPubSubExtension(system10g).mediator
 
  def receive = {
    case f:Float =>
      mediator ! Publish("state", f)
    case n:Nav => 
    	mediator ! Publish("state", Array(n.pos.x,n.pos.y,n.pos.z,n.quat.w,n.quat.x,n.quat.y,n.quat.z) )
  }
}

class ControllerListener extends Actor {

  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(system).mediator

  mediator ! Subscribe("controllerState", self)
 
  def receive = {
    case SubscribeAck(Subscribe("controllerState", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case a:Array[Float] => 
      Camera.nav.pos.set(a(0),a(1),a(2))
      Camera.nav.quat.set(a(3),a(4),a(5),a(6))
  }
}




SimulatorScript
