
package com.fishuyo.seer
package allosphere
package livecluster

import graphics._
import dynamic._
import spatial._
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
import com.typesafe.config.ConfigFactory


object Node extends OmniApp {

	var sim = false
  var render = true
	val loader = new SeerScriptTextLoader

  var scriptListener:ActorRef = _
	var clusterPublisher:ActorRef = _

	Hostname() match {
		case "gr01" => 
      sim = true
      render = false
      scriptListener = system.actorOf(Props( new SimLoader() ), name = "simLoader")
      clusterPublisher = system.actorOf(Props( new NodePublisher() ), name = "nodePublisher")
		case _ => scriptListener = system10g.actorOf(Props( new NodeLoader()), name = "loader")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")

  loader.load("""
    import com.fishuyo.seer._
    import graphics._
    import dynamic._
    import allosphere.livecluster.Node
    
    object ClusterScript extends SeerScript{
      Node.mode = "warp"
      val c = Cube()
      override def draw(){
        c.draw
      }
    }
    ClusterScript
  """)

  override def onDrawOmni(){
    if(!render) return
    Shader("omni").begin
    omni.uniforms(omniShader);

    if(loader.script != null) loader.script.draw()
    
    Shader("omni").end
  }

  override def animate(dt:Float){
    if(loader.script != null) loader.script.animate(dt)
  }
}



class SimLoader extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  
  val mediator = DistributedPubSubExtension(system).mediator
  mediator ! Subscribe("simulator", self)

  def receive = {
    case SubscribeAck(Subscribe("simulator", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case script: String =>
      Node.loader.reload(script)
  }
}

class NodePublisher extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Publish, Subscribe, SubscribeAck }
  
  val mediator = DistributedPubSubExtension(system).mediator
  mediator ! Subscribe("renderer", self)

  val mediator10g = DistributedPubSubExtension(system10g).mediator
 
  def receive = {
    case SubscribeAck(Subscribe("renderer", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case script: String =>
      println("Publishing script to cluster..")
      mediator10g ! Publish("renderer", script)
  }
}


class NodeLoader extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }

  val mediator = DistributedPubSubExtension(system10g).mediator
  mediator ! Subscribe("renderer", self)
 
  def receive = {
    case SubscribeAck(Subscribe("renderer", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case script: String =>
      Node.loader.reload(script)
  }
}




