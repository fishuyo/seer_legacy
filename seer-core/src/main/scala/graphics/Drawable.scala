
package com.fishuyo.seer
package graphics

trait Drawable {
	def init(){}
	def draw(){}
}

trait Animatable extends Drawable {
	def animate(dt: Float){}
}
