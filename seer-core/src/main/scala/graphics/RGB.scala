
package com.fishuyo.seer
package graphics

import maths.Vec3


object RGB {
  def apply( i: Float) = new RGB(i,i,i)
  def apply( i: Double) = new RGB(i.toFloat,i.toFloat,i.toFloat)
  def apply( r: Float, g: Float, b: Float) = new RGB(r,g,b)
  def apply( r:Double, g: Double, b: Double) = new RGB(r.toFloat,g.toFloat,b.toFloat)
  val black = RGB(0)	
	val white = RGB(1)	
  val red = RGB(1,0,0)
  val green = RGB(0,1,0)
  val blue = RGB(0,0,1)
}

class RGB(var r:Float, var g:Float, var b:Float){
	def rgb() : Int = new java.awt.Color(Math.min(1,r),Math.min(1,g),Math.min(1,b)).getRGB()

  def set(r1:Float,g1:Float,b1:Float) = { r=r1; g=g1; b=b1 }
  def set(c:RGB) = { r=c.r; g=c.g; b=c.b }
	def +(c: RGB) = new RGB(r+c.r, g+c.g, b+c.b)
	def *(s:Float) = new RGB(r*s, g*s, b*s)
	def *(c:RGB) = new RGB(r*c.r, g*c.g, b*c.b)
	def /(s: Float) = this * (1/s)



  def toGray() = (.3f*r+.59f*g+.11f*b)

  override def toString() = "( " + r + " " + g + " " + b + " )"
}

object RGBA{
  def apply( r:Float, g:Float, b:Float, a:Float=1.f) = new RGBA(r,g,b,a)
  def apply( v:Vec3, a:Float) = new RGBA(v.x,v.y,v.z,a)

}
class RGBA( rr:Float, gg:Float, bb:Float, var a:Float ) extends RGB(rr,gg,bb){
  def value = toGray()

  def set(r1:Float,g1:Float,b1:Float,a1:Float=1.f) = { r=r1; g=g1; b=b1; a=a1 }
  def set(c:RGBA) = { r=c.r; g=c.g; b=c.b; a=c.a }
	def +(c: RGBA) = new RGBA(r+c.r, g+c.g, b+c.b, a+c.a)
	override def *(s:Float) = new RGBA(r*s, g*s, b*s, a)
	def *(c:RGBA) = new RGBA(r*c.r, g*c.g, b*c.b, a*c.a)

  override def toString() = "( " + r + " " + g + " " + b + " " + a + " )"
}
