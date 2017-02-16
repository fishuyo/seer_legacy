
package com.fishuyo.seer
package js

import spatial.Vec3
import particle.Particle

import scala.scalajs.js
import scala.scalajs.js.annotation._ //JSExport

import org.scalajs.dom
// import dom.document
import org.scalajs.jquery.jQuery

import collection.mutable.ListBuffer

object TestApp extends js.JSApp with RunLoop {

  // @JSExport
  // val runloop = new RunLoop()

  var ctx:dom.CanvasRenderingContext2D = _
  val gravity = Vec3(0f,0.2f,0f)
  val particles = (0 until 10) map { i => Particle(Vec3(100f+10*i,100f,0f), Vec3(i*3.1f,i.toFloat,0f)) }

  def main(): Unit = {

    // jQuery(start _)
    // start()
  }

  @JSExport
  def setCanvas(canvas:dom.html.Canvas){
    // val canvas = jQuery("#canvas").asInstanceOf[dom.html.Canvas]

    ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    ctx.scale(2,2)
  }

  def clear() = {
    ctx.fillStyle = "white"
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)
  }

  override def update(dt:Double) = {
    particles.foreach { case p =>
      p.velocity += gravity
      p.setVelocity(p.velocity)
      p.step()
      if(p.position.y > ctx.canvas.height - 10){
        p.position.y = ctx.canvas.height - 10
        p.velocity.y *= -0.59f
        p.velocity.x *= 0.99f
      }
      if(p.position.x > ctx.canvas.width - 5){
        p.position.x = ctx.canvas.width - 5
        p.velocity.x *= -0.9f
        p.velocity.y *= 0.99f
      }
      if(p.position.x < 0){
        p.position.x = 0
        p.velocity.x *= -0.9f
        p.velocity.y *= 0.99f
      }
      p.setVelocity(p.velocity)
    }
  }

  override def draw() = {
    ctx.canvas.width  = (dom.window.innerWidth*2).toInt;
    ctx.canvas.height = (dom.window.innerHeight*2).toInt;
    clear()
    ctx.fillStyle = "black"
    particles.foreach { case p =>
      ctx.beginPath()
      ctx.arc(p.position.x, p.position.y, 20, 0, math.Pi*2, true);
      ctx.stroke()
      // ctx.fillRect(p.pos.x,p.pos.y,4,4)
    }
  }

}


