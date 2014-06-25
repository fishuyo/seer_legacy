
package com.fishuyo.seer
package allosphere
package liveclusterloader 

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

import de.sciss.osc.Message

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator


object LiveClusterLoader extends SeerApp {

	var sim = false
	val loader = new SeerScriptTextLoader

	var publisher:ActorRef = _
	var subscriber:ActorRef = _
	ClusterConfig.hostname match {
		// case "gr01" => publisher = system.actorOf(Props( new Simulator), name = "sLoader")
		case _ => subscriber = system.actorOf(Props( new Loader()), name = "loader")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")
}

class Loader extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(system).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("script", self)
 
  def receive = {
    case SubscribeAck(Subscribe("script", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case s: String =>
      LiveClusterLoader.loader.load(s)
  }
}

class Simulator extends Actor {
  import DistributedPubSubMediator.Publish
  // activate the extension
  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case in: String =>
      val out = in.toUpperCase
      mediator ! Publish("script", out)
  }
}


