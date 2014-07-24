

import com.fishuyo.seer._
import allosphere._
import allosphere.actor._
import dynamic._
import graphics._
import io._
import spatial._


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory

import ClusterSystem.system
// import ClusterSystem.{ test3 => system }

object ControllerScript extends SeerScript{

	val publisher = system.actorOf(Props( new ControllerStatePublisher()), name = "controllerstate")

	override def preUnload(){
		publisher ! PoisonPill
	}
	override def draw(){
		Sphere().draw
	}

	override def animate(dt:Float){
		// publisher ! Mouse.y().toFloat
		publisher ! Camera.nav
	}

	Mouse.clear
	Mouse.use
}


class ControllerStatePublisher extends Actor {
  import DistributedPubSubMediator.Publish

  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case f:Float => mediator ! Publish("controllerState", f)
    case n:Nav => mediator ! Publish("controllerState", Array(n.pos.x,n.pos.y,n.pos.z,n.quat.w,n.quat.x,n.quat.y,n.quat.z) )
  }
}


ControllerScript