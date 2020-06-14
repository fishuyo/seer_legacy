
package com.fishuyo.seer
package examples.ui

import graphics._
import spatial._
import io._
import ui._

object PickableTest extends SeerApp {

  val p = new BoundingBoxPickable(Vec3(-0.5),Vec3(0.5))
  val m = Cube()
  p.pose.set(m.pose)
  p.scale.set(m.scale)

  override def animate(dt:Float) = {
    implicit def f2i(f:Float):Int = f.toInt
    val ray = Camera.ray(Mouse.x.now*Window.width, (1f - Mouse.y.now)*Window.height)
    val e = Mouse.status.now match {
      case "move" => Point
      case "down" => Pick
      case "drag" => Drag
      case "up" => Unpick
    }
    p.event(PickEvent(e,ray))

    if(p.selected) m.material.color = RGB(1,0,1)
    else if(p.hover) m.material.color = RGB(0,1,1)
    else m.material.color = RGB(1,1,1)

    m.pose.set(p.pose)
  }

  override def draw() = {
    m.draw()
  }

}