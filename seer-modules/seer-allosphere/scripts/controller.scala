

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

implicit def f2i(f:Float) = f.toInt

object ControllerScript extends SeerScript{

	val publisher = system.actorOf(Props( new ControllerStatePublisher()), name = "controllerstate")

	override def preUnload(){
		publisher ! PoisonPill
	}
	override def draw(){
		Sphere().draw
	}

	var vel = Vec2()
	var lpos = Vec2()
	var ray = Camera.ray(0,0)

	override def animate(dt:Float){

		if( Mouse.status() == "drag"){
			vel = (Mouse.xy() - lpos)/dt
			ray = Camera.ray(Mouse.x()*Window.width, (1.f-Mouse.y()) * Window.height)
			publisher ! (vel,ray)
		}
		lpos = Mouse.xy()

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
  	case (v:Vec2,r:Ray) => mediator ! Publish("controllerState", Array(v.x,v.y,r.o.x,r.o.y,r.o.z,r.d.x,r.d.y,r.d.z))
  }
}


ControllerScript