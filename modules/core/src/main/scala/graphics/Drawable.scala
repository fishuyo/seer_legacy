
package com.fishuyo.seer
package graphics

trait Drawable {
  var initialized = false
	def init(){}
  def draw(){}
  def step(dt: Float){}
  
  @deprecated("use step instead", "")
  def animate(dt: Float){}
}

@deprecated("use Drawable", "")
trait Animatable extends Drawable {
}
