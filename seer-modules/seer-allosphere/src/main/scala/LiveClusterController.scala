
package com.fishuyo.seer
package allosphere
package livecluster

import graphics._
import dynamic._
import maths._
import io._
import util._

import allosphere.actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map
import scala.io.Source

import de.sciss.osc.Message

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory


import monido._

object Controller extends SeerApp {

	val loader = new SeerScriptLoader("src/main/scala/scripts/controller.scala")

	val monitor = FileMonido("src/main/scala/scripts/cluster_node.scala"){
    case ModifiedOrCreated(f) => 
      val code = Source.fromFile(f).getLines.reduceLeft[String](_ + '\n' + _)
      publisher ! code
    case _ => None
  }

  // val systemm = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.test_config1))

	var publisher:ActorRef = _
	var subscriber:ActorRef = _
	ClusterConfig.hostname match {
		case _ => publisher = system.actorOf(Props( new Publisher()), name = "publisher")
		// case _ => subscriber = system.actorOf(Props( new Loader()), name = "loader")
	}

}

class Publisher extends Actor with ActorLogging {
  import DistributedPubSubMediator.Publish

  // val cluster = Cluster(system)

  // activate the extension
  val mediator = DistributedPubSubExtension(system).mediator
 
  // subscribe to cluster changes, re-subscribe when restart 
  // override def preStart(): Unit = {
  //   cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
  //     classOf[MemberEvent], classOf[UnreachableMember])
  // }
  // override def postStop(): Unit = cluster.unsubscribe(self)
  
  def receive = {
    case in: String =>
      println("Publishing script..")
      mediator ! Publish("script", in)

    // case MemberUp(member) =>
    //   log.info("Member is Up: {}", member.address)
    // case UnreachableMember(member) =>
    //   log.info("Member detected as unreachable: {}", member)
    // case MemberRemoved(member, previousStatus) =>
    //   log.info("Member is Removed: {} after {}",
    //     member.address, previousStatus)
    // case _: MemberEvent => // ignore
  }
}


