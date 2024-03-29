

package com.fishuyo.seer
package examples.graphics

import graphics._
import ui._
import spatial._
import io._

object WidgetExample extends SeerApp { 

  val s = new Slider(Vec2(0,0), Vec2(1,1))

  override def animate(dt:Float){
    implicit def f2i(f:Float) = f.toInt
    val ray = Camera.ray(Mouse.x.now*Window.width, (1f - Mouse.y.now)*Window.height)
    val e = Mouse.status.now match {
      case "move" => Point
      case "down" => Pick
      case "drag" => Drag
      case "up" => Unpick
    }
    val event = PickEvent(e,ray)
    s.pickEvent(event)
    // s.children.foreach(_.pickEvent(event))


  }
  override def draw(){
    WidgetRenderer.draw(s)
  }
}