
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props

import allosphere._
import allosphere.actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

import de.sciss.osc.Message


object Script extends SeerScript {

	val sim = false

	val actor = system.actorOf(Props(new Node), name = "node")

	override def preUnload(){
	}

	override def draw(){
	}

	override def animate(dt:Float){
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
