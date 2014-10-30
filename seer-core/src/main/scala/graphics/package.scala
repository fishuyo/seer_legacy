
package com.fishuyo.seer

package object graphics {

	val Points = 0
	val Lines = 1
	val LineLoop = 2
  val LineStrip = 3
  val Triangles = 4
  val TriangleStrip = 5
  val TriangleFan = 6

	implicit def RGBA2Float(c:RGBA) = c.toGray
	implicit def RGB2RGBA(c:RGB) = RGBA(c.r,c.g,c.b,1)
	implicit def RGBA2RGB(c:RGBA) = RGB(c.r,c.g,c.b)

	// port of Allosystem color conversion code al_Color.cpp
	implicit def HSV2RGB(c:HSV):RGB = {
	
		if(c.s == 0f) return RGB(c.v)

		val h = c.h*6f
											
		val i = h.toInt 	// integer part of h
		val f = h - i				// fractional part of h
		val p = c.v * (1f - c.s);

		// depends on hue section being even or odd
		val q = c.v * (1f - c.s*( if((i&1)==1) f else (1f - f) ))

		var (r,g,b)=(0f,0f,0f)
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
	implicit def RGB2HSV(c:RGB):HSV = {
		val cmax = List(c.r,c.g,c.b).max
		val cmin = List(c.r,c.g,c.b).min
		val del = cmax - cmin

		val hsv = HSV(0,0,cmax)

		if(del != 0f && cmax != 0f){		// chromatic data...
			hsv.s = del / cmax							// set saturation
		
			var hl = 0f
			if (c.r == cmax) hl = (c.g - c.b)/del;	// between yellow & magenta
			else if(c.g == cmax)	hl = 2f + (c.b - c.r)/del;	// between cyan & yellow
			else hl = 4f + (c.r - c.g)/del;	// between magenta & cyan

			if(hl < 0f) hl += 6f;

			hsv.h = hl * (1f/6f);
		} //else this is a gray, no chroma...
	
		hsv
	}
	implicit def HSV2RGBA(c:HSV) = RGB2RGBA(HSV2RGB(c))
	implicit def RGBA2HSV(c:RGBA) = RGB2HSV(RGBA2RGB(c))
	
}
