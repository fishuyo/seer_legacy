
package com.fishuyo.seer
package allosphere

import allosphere.actor._

// import akka.cluster.Cluster
// import akka.cluster.ClusterEvent._
import akka.actor._
// import akka.contrib.pattern.DistributedPubSubExtension
// import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory

import akka.util.ByteString

import akka.io._

import java.net.InetSocketAddress



class Simulator extends Actor with ActorLogging {
 
 	val renderers = ClusterConfig.renderers(UdpAllTest.system)

  override def preStart() = {
    log.debug("Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case msg => renderers.foreach( _ ! msg)
  }
 
}

class Renderer extends Actor with ActorLogging {
 
  override def preStart() = {
    log.debug("Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case bytes:Array[Int] => 
    	UdpAllTest.bytes += bytes.length*4
      UdpAllTest.frame = bytes(0)
    case _ => println("no match.")
  }
 
}


object UdpAllTest extends App {

  implicit val system = ActorSystem("state", ConfigFactory.load(ClusterConfig.udp10g))

	var sim = false
	val buf = new Array[Int](1024*1024)
	var frame = 0
	var lframe = 0
	var bytes = 0

	var publisher:ActorRef = _
	var subscriber:ActorRef = _

	Hostname() match {
		case "gr01" => sim = true; publisher = system.actorOf(Props( new Simulator()), name = "simulator")
		case _ => subscriber = system.actorOf(Props( new Renderer()), name = "renderer")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")


	while(true){
		if(sim){
			buf(0) = frame
			publisher ! buf 
			frame += 1
		} 
		Thread.sleep(33)
		lframe += 1

		if(lframe % 30 == 0) printStats()	
	}

	def printStats(){
		val rate = bytes / 1024
		println(s"""Frame: $frame
			Data rate: $rate KB/s
			""")
	}
}
