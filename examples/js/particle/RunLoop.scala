
package com.fishuyo.seer.js

import scala.scalajs.js
import scala.scalajs.js.annotation._

import org.scalajs.dom
// import dom.document
import org.scalajs.jquery.jQuery


trait RunLoop {
  var request = 0
  // var x = 0f
  // var y = 0f

  @JSExport
  def start():Unit = {
    request = dom.window.requestAnimationFrame(frame _)
  }
  @JSExport
  def stop():Unit = {
    dom.window.cancelAnimationFrame(request)
  }

  def frame(time:Double):Unit = {
    // println(s"update: $dt ${dom.window.performance.now()}")

    update(time)
    draw()
    request = dom.window.requestAnimationFrame(frame _)
  }

  def update(dt:Double):Unit //{
    // println("update")
    // x += 0.1f
    // y += 0.1f
    // jQuery("#thing").css("transform", s"translate(${x}%,${y}%)")
  //}

  def draw():Unit //{
    // println("draw")
  //}
}


