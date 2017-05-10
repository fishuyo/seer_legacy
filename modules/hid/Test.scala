
package com.fishuyo.seer
package hid

import actor._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

object Test extends App {

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  DeviceManager.services
  val joy = new PS3Controller(0)
  val m = new Device("Razer Orochi",0)
  val m2 = new Device("Apple Optical USB Mouse",0)
  joy.connect

  // joy.sources("L1").runForeach( (v) => println("L1 " + v))
  // joy.sources("leftX").runForeach( (v) => println("leftX " + v))
  joy.sources("accX").runForeach( (v) => println("accX " + v))
  // joy.source("triangle").value
  // joy.onButton {
  //   case 
  //   case buttonstate => ()
  // }
  // Mapping(joy.source("triangle") -> app.sink("callAgents") )
  while(true){Thread.sleep(100)}
  DeviceManager.shutdown()
}