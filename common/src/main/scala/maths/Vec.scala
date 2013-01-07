
package com.fishuyo
package maths

object Vec3 {

  def apply( v: Float) = new Vec3( v, v, v)
  def apply( vv: Double) = { val v=vv.toFloat; new Vec3( v, v, v) }
  def apply( x: Float, y: Float, z: Float) = new Vec3(x,y,z)
  def apply( x: Double, y: Double, z: Double) =  new Vec3(x.toFloat,y.toFloat,z.toFloat) 

  def unapply( v: Vec3): Some[(Float,Float,Float)] = Some((v.x,v.y,v.z))
}

class Vec3( var x: Float, var y: Float, var z: Float ){
  //def ==(v:Vec3) = {x==v.x && y==v.y && z==v.z}
  def set(v:Vec3) = { x=v.x; y=v.y; z=v.z }
  def set(a:Float,b:Float,c:Float) = { x=a; y=b; z=c; }
  def +(v: Vec3) = Vec3( x+v.x, y+v.y, z+v.z )
  def +=(v: Vec3) = { x+=v.x; y+=v.y; z+=v.z }
  def -(v: Vec3) = Vec3( x-v.x, y-v.y, z-v.z )
  def -=(v: Vec3) = { x-=v.x; y-=v.y; z-=v.z }
  def unary_- = Vec3( -x, -y, -z ) 
  //def *(ss: Double) = { val s = ss.toFloat; Vec3(s*x, s*y, s*z) }
  def *(s: Float ) = Vec3(s*x, s*y, s*z)
  def *(v: Vec3 ) = Vec3(v.x*x, v.y*y, v.z*z)
  //def *=(ss: Double) = { val s = ss.toFloat; x*=s; y*=s; z*=s } 
  def *=(s: Float) = { x*=s; y*=s; z*=s }
  def /(s: Float ) = Vec3(x/s, y/s, z/s)
  
  def dot(v: Vec3) : Float = x*v.x + y*v.y + z*v.z
  def cross( v: Vec3) = Vec3( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = this * (1.0f / mag() )

  def zero() = {x=0;y=0;z=0}
  override def toString() = "[" + x + " " + y + " " + z + "]"

  def lerp( v:Vec3, d:Float ) = this + (v-this)*d
  
}


