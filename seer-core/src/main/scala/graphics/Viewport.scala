
package com.fishuyo.seer.graphics

object Viewport {
	def apply( w:Int=800, h:Int=600 ) = new Viewport(0,0,w,h)
	def apply( l:Int, b:Int, w:Int, h:Int) = new Viewport(l,b,w,h)
	def apply( v:Viewport ) = new Viewport(v.l,v.b,v.w,v.h)
}
class Viewport(var l:Int, var b:Int, var w:Int, var h:Int) {
	def aspect() = { if (h!=0 && w!=0) w*1.f/h else 1.f }
	def set(ll:Int, bb:Int, ww:Int, hh:Int){ l=ll; b=bb; w=ww; h=hh; }
}