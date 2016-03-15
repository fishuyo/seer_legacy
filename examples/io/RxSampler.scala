

package com.fishuyo.seer
package examples.io

import graphics._
import spatial._
import io._

import rx._
import rx.async._
import rx.async.Platform._

import scala.concurrent.duration._

object RxSampler extends SeerApp { 

  val model = Sphere().scale(0.1f)

  val pos = Mouse.xy.map( _*2 - Vec2(1,1) )
  val o = pos.trigger{ model.pose.pos = Vec3(pos.now) }

  val sampler = new Sampler(pos)

  val o1 = Keyboard.down.trigger {
    Keyboard.down.now match {
      case 'r' => sampler.record()
      case 't' => sampler.stop()
      case 'p' => sampler.play()
      case _ => ()
    }
  }

  val o2 = sampler.out.trigger { model.pose.pos = Vec3(sampler.out.now)}


  override def draw(){
    model.draw
  }

}