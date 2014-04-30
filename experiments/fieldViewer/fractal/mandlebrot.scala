
package com.fishuyo.seer
package examples.fieldViewer.fractal

import dynamic._
import graphics._
import maths._
import io._

object Main extends App with Animatable {
  DesktopApp.loadLibs()
  Scene.push(this)

  var field = new MField(100,100)

  val live = new Ruby("src/main/scala/examples/fieldViewer/fractal/mandlebrot.rb")

  DesktopApp()

  override def draw() = {
    field.draw()
  }

  override def step(dt:Float) = {
    live.step(dt)
  }

}

class MField(ww:Int,hh:Int) extends Field2D(ww,hh) {
  
  var zoom = Vec3(1.f,0,0)
  var pos = Vec3(0)

  override def step(dt: Float) = {

    for( y <- (0 until h); x <- (0 until w)){
      var p = Vec3( pos.x + x * 4.f*zoom.x / w - 2.f*zoom.x, pos.y + y * 4.f*zoom.x / h - 2.f*zoom.x, 0);
      var orig = Vec3(0) + p
      
      var t = 0;

      while( p.mag < 2.f && t < 100 ){
        var xx = p.x*p.x - p.y*p.y
        var yy = 2*p.x*p.y
        p = Vec3( xx, yy, 0)
        //p = Vec3( p.x*p.x, p.y*p.y, 0)
        p += orig
        t += 1
      }

      set(y,x,t)
    }

    normalize()
  }
}
