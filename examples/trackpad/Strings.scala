
package com.fishuyo.seer
package examples.trackpad

import graphics._
import spatial._
import particle._
import io._

object Strings extends SeerApp {


  val strings = for( i <- 0 until 4) yield new SpringString(Vec3(), 10)

  val mesh = Mesh()
  mesh.primitive = Lines
  // strings.foreach { case s => s.particles}


  (0 until 4).foreach{ case i =>
    val rx = Trackpad.xys(i).map( (v) => Vec3(v * 2) - Vec3(1) )
    rx.trigger { strings(i).pos.set(Vec3(rx.now)) }
  }

  override def animate(dt:Float){
    strings.foreach( _.animate(dt))
  }
  override def draw(){
    strings.foreach{ case s =>
      s.particles.foreach { case p =>
        Sphere().translate(p.position).scale(0.02).draw
      }
    }
  }

}