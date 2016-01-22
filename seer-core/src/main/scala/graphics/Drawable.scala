
package com.fishuyo.seer
package graphics

trait Drawable {
  var initialized = false
	def init(){}
	def draw(){}
  def animate(dt: Float){}
}

trait Animatable extends Drawable {
}
