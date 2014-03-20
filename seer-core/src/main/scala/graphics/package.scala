
package com.fishuyo.seer
package object graphics {


	import com.badlogic.gdx.graphics.GL10

	val Points = GL10.GL_POINTS
	val Lines = GL10.GL_LINES
	val LineLoop = GL10.GL_LINE_LOOP
  val LineStrip = GL10.GL_LINE_STRIP
  val Triangles = GL10.GL_TRIANGLES
  val TriangleStrip = GL10.GL_TRIANGLE_STRIP
  val TriangleFan = GL10.GL_TRIANGLE_FAN

	implicit def RGBA2Float(c:RGBA) = c.toGray
	implicit def RGB2RGBA(c:RGB) = RGBA(c.r,c.g,c.b,1)

	implicit def HSV2RGB(c:HSV):RGB = {
	
		if(c.s == 0.f) return RGB(c.v)

		val h = c.h*6.f
											
		val i = h.toInt 	// integer part of h
		val f = h - i				// fractional part of h
		val p = c.v * (1.f - c.s);

		// depends on hue section being even or odd
		val q = c.v * (1.f - c.s*( if((i&1)==1) f else (1.f - f) ))

		var (r,g,b)=(0.f,0.f,0.f)
		i match {
			case 0 => r=c.v; g=q; b=p
			case 1 =>	r=q; g=c.v; b=p
			case 2 =>	r=p; g=c.v; b=q
			case 3 =>	r=p; g=q; b=c.v
			case 4 => r=q; g=p; b=c.v
			case _ =>r=c.v; g=p; b=q
		}	
		RGB(r,g,b)
	}
	implicit def HSV2RGBA(c:HSV) = RGB2RGBA(HSV2RGB(c))
	
}
