
package com.fishuyo.seer
package examples.vrpn

import io._
import graphics._

object VRPNtest extends SeerApp {

  val cube = Cube()

  VRPN.ip = "192.168.0.100" //46"
  // VRPN.bind("Tracker0",(p)=>{
  VRPN.bind("rigid1",(p)=>{
    println(p)
    cube.pose.set(p)
  })

  override def draw() = cube.draw

}