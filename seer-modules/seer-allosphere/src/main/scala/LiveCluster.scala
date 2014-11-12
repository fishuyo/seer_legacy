
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
import akka.pattern.ask
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory

import concurrent.Await
import concurrent.duration._

import ClusterSystem.{ system, system10g }
// import ClusterSystem.{ test2 => system, test2_10g => system10g }

object Node extends OmniApp {

	var sim = false
  var render = true
	val loader = ScriptLoader()

  var scriptListener:ActorRef = _
	var clusterPublisher:ActorRef = _

	Hostname() match {
		case "gr01" => 
      sim = true
      render = false
      scriptListener = system.actorOf(Props( new SimLoader() ), name = "simLoader")
      clusterPublisher = system.actorOf(Props( new NodePublisher() ), name = "nodePublisher")
		case _ =>
      scriptListener = system10g.actorOf(Props( new NodeLoader()), name = "loader")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")

  loader ! ScriptLoader.RunCode("""
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

  implicit val timeout = akka.util.Timeout(4.seconds)
  var script = Await.result(loader ? "script", 10 seconds).asInstanceOf[Option[SeerScript]]


  override def onDrawOmni(){
    if(!render) return
    Shader("omni").begin
    omni.uniforms(omniShader);

    script.foreach( _.draw() )
    
    Shader("omni").end
  }

  override def animate(dt:Float){
    script.foreach( _.animate(dt) )
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
      println("Loading script..")
      Node.loader ! ScriptLoader.Reload(script)
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
      println("Loading script..")
      Node.loader ! ScriptLoader.Reload(script)
  }
}




