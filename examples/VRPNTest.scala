package seer
package examples.vrpn

import spatial._
import graphics._
import io._

object VRPNTest extends SeerApp {

  var ps = List[Vec3]() 
  VRPN.analogListen("PoseNet0@localhost", (data) => { 
    ps = data.grouped(3).map { case a => Vec3(a(0),a(1),a(2)) }.toList
    println(ps)
  })
  
  // while(true){ Thread.sleep(1000) }

  override def draw() = {
    ps.foreach { case p =>
      val v = Vec3(p.x,p.y,-p.z)
      Sphere().scale(0.03f).translate(v*0.1f).draw()
    }
  }


}