
package com.fishuyo.seer
package examples.io

import graphics._
import spatial._
import io._

import rx._
import rx.async._
import rx.async.Platform._
import scala.concurrent.duration._

object AppleTrackpad extends SeerApp { 

  // val a = Var(0)

  // val o = Trackpad.status(0).trigger{ println("status: " + Trackpad.status(0).now) }
  // val o = Trackpad.xys(0).trigger{ println("pos0: " + Trackpad.xys(0).now) }
  // val o1 = Trackpad.sizes(0).trigger{ println("size: " + Trackpad.sizes(0).now) }
  val o2 = Trackpad.angles(0).trigger{ println("angle: " + Trackpad.angles(0).now) }

  // val pos0 = Rx { Trackpad.fingers() filter(_.length > 0)
  // val pos0 = fil.map( _.head.pos )


  // val countSlow = Var(0)
  // var timeout = Timer(25 millis)
  // val o5 = Trackpad.count.trigger{     
  //     timeout.kill()
  //     timeout = Timer(25 millis)
  //     timeout.triggerLater { countSlow() = Trackpad.count.now; timeout.kill() }
  // }
  
  // val o3 = Trackpad.countSlow.trigger{ println("count: " + Trackpad.countSlow.now)}



  override def animate(dt:Float){
  }

}