
package com.fishuyo.seer
package allosphere

import allosphere.actor.ClusterConfig

// import akka.cluster.Cluster
// import akka.cluster.ClusterEvent._
import akka.actor._
// import akka.contrib.pattern.DistributedPubSubExtension
// import akka.contrib.pattern.DistributedPubSubMediator
import com.typesafe.config.ConfigFactory

// import akka.zeromq._
import akka.util.ByteString

import akka.io._

import java.net.InetSocketAddress

object UdpSystem {
  implicit val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.udp_test))
  // val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.config10g))
}
import UdpSystem._
 

class SimpleSender(remote: InetSocketAddress) extends Actor {
  IO(Udp) ! Udp.SimpleSender(collection.immutable.Traversable(Udp.SO.Broadcast(true)))
 
  def receive = {
    case Udp.SimpleSenderReady =>
      context.become(ready(sender()))
  }
 
  def ready(send: ActorRef): Receive = {
    case msg:String =>
      send ! Udp.Send(ByteString(UdpTest.buf), remote)
  }
}

object UdpTest extends App {

	var sim = true
	val buf = new Array[Byte](1400)
	var frame = 0
	var lframe = 0
	var bytes = 0


	val sender = system.actorOf(Props(new SimpleSender(new InetSocketAddress("192.168.0.255",9000))), name = "send")

	while(true){
		if(sim){
			buf(0) = frame.toByte
			sender ! "s" //"/ye\0s\0\0\0aaaa"
			frame += 1
		} 
		Thread.sleep(33)
		lframe += 1

		if(lframe % 30 == 0) printStats()	
	}

	def printStats(){
		println(s"""Frame: $frame
			Data received: $bytes
			""")
	}
}
