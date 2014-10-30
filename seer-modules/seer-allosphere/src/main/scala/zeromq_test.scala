
// package com.fishuyo.seer
// package allosphere
// package zeromq

// import allosphere.actor.ClusterConfig

// // import akka.cluster.Cluster
// // import akka.cluster.ClusterEvent._
// import akka.actor._
// // import akka.contrib.pattern.DistributedPubSubExtension
// // import akka.contrib.pattern.DistributedPubSubMediator
// import com.typesafe.config.ConfigFactory

// import akka.zeromq._
// import akka.util.ByteString


// object ClusterSystem {
//   val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.zmq_test))
//   // val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.config10g))
// }
// import ClusterSystem._


 
// class Listener extends Actor {
//   def receive: Receive = {
//     case Connecting    => println("connecting")
//     case m: ZMQMessage => print(".")
//     case _             => ()
//   }
// }
 

// object Test extends App {

// 	var sim = true
// 	val buf = new Array[Byte](1024)
// 	var frame = 0
// 	var lframe = 0
// 	var bytes = 0

// 	// var pub:ActorRef = _
// 	// var sub:ActorRef = _
// 	// Hostname() match {
// 	// 	case "gr01" => sim = true; pub = system.actorOf(Props( new Pub()), name = "pub")
// 	// 	case _ => sub = system.actorOf(Props( new Sub()), name = "sub")
// 	// }

// 	val pubSocket = ZeroMQExtension(system).newSocket(SocketType.Pub,
// 	  Bind("tcp://127.0.0.1:21231"))

// 	val listener = system.actorOf(Props(new Listener()), name = "sub")
// 	val subSocket = ZeroMQExtension(system).newSocket(SocketType.Sub,
//   Listener(listener), Connect("tcp://127.0.0.1:21231"), SubscribeAll)

// 	while(true){
// 		if(sim){
// 			buf(0) = frame.toByte
// 			pubSocket ! ZMQMessage(ByteString(buf))
// 			frame += 1
// 		} 
// 		Thread.sleep(33)
// 		lframe += 1

// 		if(lframe % 30 == 0) printStats()	
// 	}

// 	def printStats(){
// 		println(s"""Frame: $frame
// 			Data received: $bytes
// 			""")
// 	}
// }





