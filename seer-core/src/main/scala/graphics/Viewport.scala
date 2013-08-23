
package com.fishuyo.seer.graphics

object Viewport {
	def apply( w:Float=800, h:Float=600 ) = new Viewport(0,0,w,h)
	def apply( l:Float, b:Float, w:Float, h:Float) = new Viewport(l,b,w,h)
	def apply( v:Viewport ) = new Viewport(v.l,v.b,v.w,v.h)
}
class Viewport(var l:Float, var b:Float, var w:Float, var h:Float) {
	def aspect() = { if (h!=0 && w!=0) w/h else 1 }
	def set(ll:Float, bb:Float, ww:Float, hh:Float){ l=ll; b=bb; w=ww; h=hh; }
}