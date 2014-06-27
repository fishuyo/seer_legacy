
package com.fishuyo.seer
package allosphere
package test

import allosphere.actor._

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory


object ClusterSystem {
  val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.config))
  // val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.config10g))
}
import ClusterSystem._


object Main extends App {

	var sim = false
	val buf = new Array[Byte](1024)
	var frame = 0
	var bytes = 0

	var pub:ActorRef = _
	var sub:ActorRef = _
	Hostname() match {
		case "gr01" => sim = true; pub = system.actorOf(Props( new Pub()), name = "pub")
		case _ => sub = system.actorOf(Props( new Sub()), name = "sub")
	}

	while(true){
		if(sim){
			pub ! buf
		}
		Thread.sleep(33)
		frame += 1

		if(frame % 30 == 0) printStats()	
	}

	def printStats(){
		println(s"""Frame: $frame
			Data received: $bytes
			""")
	}
}

class Pub extends Actor {
  import DistributedPubSubMediator.Publish

  // activate the extension
  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case state: Array[Byte] =>
      mediator ! Publish("state", state)
  }
}

class Sub extends Actor {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(system).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("state", self)
 
  def receive = {
    case SubscribeAck(Subscribe("state", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case state: Array[Byte] =>
      Main.bytes += state.length
  }
}




