
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import allosphere._
import allosphere.actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

import de.sciss.osc.Message

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator


object Script extends SeerScript {

	var frame = 0
	var sim = true

	var publisher:ActorRef = _
	var subscriber:ActorRef = _
	ClusterConfig.hostname match {
		case "gr01" => publisher = system.actorOf(Props( new Simulator), name = "simulator")
		case "Thunder.local" =>
			publisher = system.actorOf(Props(new Simulator), name = "simulator")
			subscriber = system.actorOf(Props( new Renderer), name = "renderer")
		case _ => sim = false; subscriber = system.actorOf(Props( new Renderer), name = "renderer")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")

	// val actor = system.actorOf(Props(new Node), name = "node")

	override def preUnload(){
	}

	override def draw(){
	}

	override def animate(dt:Float){
		frame += 1
		if(sim) publisher ! s"frame: $frame"
	}


}

class Renderer extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(system).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("state", self)
 
  def receive = {
    case SubscribeAck(Subscribe("state", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case s: String =>
      log.info("Got {}", s)
  }
}

class Simulator extends Actor {
  import DistributedPubSubMediator.Publish
  // activate the extension
  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case in: String ⇒
      val out = in.toUpperCase
      mediator ! Publish("state", out)
  }
}

class Node extends Actor with ActorLogging {
 
  val cluster = Cluster(system)
 
  // subscribe to cluster changes, re-subscribe when restart 
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }
  override def postStop(): Unit = cluster.unsubscribe(self)
 
  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}


Script
