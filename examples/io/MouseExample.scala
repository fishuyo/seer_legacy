

package com.fishuyo.seer
package examples.io

import graphics._
import spatial._
import io._

import rx._
import rx.async._
import rx.async.Platform._

import scala.concurrent.duration._

object MouseExample extends SeerApp { 

  val model = Sphere().scale(0.1f)

  val o1 = Mouse.status.trigger{ println("status: " + Mouse.status.now) }
  val o2 = Mouse.xy.trigger{ println("xy: " + Mouse.xy.now) }
  val o3 = Mouse.vel.trigger{ println("vel: " + Mouse.vel.now) }

  // move model when m key held down
  val m = Rx{ Keyboard.down() == 'm' }
  val pos = Mouse.xy.map( _*2 - Vec2(1,1) )
  val o4 = pos.trigger{ if( m.now ) model.pose.pos = Vec3(pos.now) }


  val clicks = Var(0)
  var timeout = Timer(500 millis)
  val o5 = Mouse.status.trigger{ 
    if(Mouse.status.now == "down"){
      timeout.kill()
      clicks() = clicks.now + 1
      timeout = Timer(500 millis)
      timeout.triggerLater { clicks() = 0; timeout.kill() }
    }
  }
  val o6 = clicks.trigger{println("clicks: " + clicks.now)}

  override def draw(){
    model.draw
  }

}