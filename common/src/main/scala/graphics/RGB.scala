
package com.fishuyo
package graphics


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

class RGB(val r:Float, val g:Float, val b:Float){
	def rgb() : Int = new java.awt.Color(Math.min(1,r),Math.min(1,g),Math.min(1,b)).getRGB()
	def +(c: RGB) = new RGB(r+c.r, g+c.g, b+c.b)
	def *(s:Float) = new RGB(r*s, g*s, b*s)
	def *(c:RGB) = new RGB(r*c.r, g*c.g, b*c.b)
	def /(s: Float) = this * (1/s)

  override def toString() = "( " + r + " " + g + " " + b + " )"
}

class RGBA( r:Float, g:Float, b:Float, val a:Float ) extends RGB(r,g,b){
	def +(c: RGBA) = new RGBA(r+c.r, g+c.g, b+c.b, a+c.a)
	override def *(s:Float) = new RGBA(r*s, g*s, b*s, a)
	def *(c:RGBA) = new RGBA(r*c.r, g*c.g, b*c.b, a*c.a)

  override def toString() = "( " + r + " " + g + " " + b + " " + a + " )"
}
