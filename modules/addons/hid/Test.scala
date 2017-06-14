
package com.fishuyo.seer
package hid

import io._
import actor._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

object Test extends App {

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  val osc = new OSCSend()
  osc.connect("localhost", 9000)

  DeviceManager.services
  // val joy = new PS3Controller(0)
  // val m = new Device("Razer Orochi",0)
  val joy = new JoyconR(0)
  // val m2 = new Device("Apple Optical USB Mouse",0)
  joy.connect
  val sources = joy.getSources
  sources.foreach { case (n,s) =>
    s.runForeach( (v) => osc.send(s"/joycon/$n", v))
  }

  sources("X").runForeach( (v) => println("X " + v))
  // joy.sources("L1").runForeach( (v) => println("L1 " + v))
  // joy.sources("leftX").runForeach( (v) => println("leftX " + v))
  // joy.sources("accX").runForeach( (v) => println("accX " + v))
  // joy.source("triangle").value
  // joy.onButton {
  //   case 
  //   case buttonstate => ()
  // }
  // Mapping(joy.source("triangle") -> app.sink("callAgents") )

  // val msg = Array[Byte](1,0)
  // m.device.get.write(msg, msg.length, 0)
  while(true){
    Thread.sleep(100);
    // m.device.foreach{_.write(msg, msg.length, 0)}
  }
  DeviceManager.shutdown()
}