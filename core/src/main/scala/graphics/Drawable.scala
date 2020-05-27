
package com.fishuyo.seer
package graphics

trait Drawable {
  var initialized = false
  def init(): Unit ={}
  def draw(): Unit ={}
  def step(dt: Float): Unit ={}
  
  @deprecated("use step instead", "")
  def animate(dt: Float): Unit ={}
}

@deprecated("use Drawable", "")
trait Animatable extends Drawable {
}
