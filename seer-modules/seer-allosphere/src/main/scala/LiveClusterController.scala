
package com.fishuyo.seer
package allosphere
package livecluster

import graphics._
import dynamic._
import spatial._
import io._
import util._

import allosphere.actor._


import java.io._
import scala.io.Source


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

import monido._

import ClusterSystem.system
// import ClusterSystem.{ test3 => system }

object Controller extends SeerApp {

	val loader = new SeerScriptLoader("scripts/controller.scala")

  val monitor1 = FileMonido("scripts/simulator.scala"){
    case ModifiedOrCreated(f) => 
      val code = Source.fromFile(f).getLines.reduceLeft[String](_ + '\n' + _)
      publisher ! ("simulator", code)
    case _ => None
  }
  val monitor2 = FileMonido("scripts/renderer.scala"){
    case ModifiedOrCreated(f) => 
      val code = Source.fromFile(f).getLines.reduceLeft[String](_ + '\n' + _)
      publisher ! ("renderer", code)
    case _ => None
  }

	var publisher = system.actorOf(Props( new SimPublisher()), name = "simpublisher")

}

class SimPublisher extends Actor with ActorLogging {
  import DistributedPubSubMediator.Publish

  // activate the extension
  val mediator = DistributedPubSubExtension(system).mediator
 
  def receive = {
    case (name:String, script:String) =>
      println(s"Publishing script to $name..")
      mediator ! Publish(name, script)
    case _ => ()
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


