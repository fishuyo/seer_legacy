
package com.fishuyo.seer
package spatial

import spire.algebra._

import scala.language.implicitConversions

object Vec3 {

  def apply() = new Vec3(0,0,0)
  def apply(v:Vec3) = new Vec3(v.x,v.y,v.z)
  def apply( v:Float) = new Vec3(v,v,v)
  def apply( vv:Double) = { val v=vv.toFloat; new Vec3(v,v,v) }
  def apply( x:Float, y:Float, z:Float) = new Vec3(x,y,z)
  def apply( x:Double, y:Double, z:Double) =  new Vec3(x.toFloat,y.toFloat,z.toFloat) 

  def apply(v:Vec2,z:Float=0) = new Vec3(v.x,v.y,z)
  def apply(x:Float,v:Vec2) = new Vec3(x,v.x,v.y)

  def unapply( v: Vec3): Some[(Float,Float,Float)] = Some((v.x,v.y,v.z))

  // implicit object Vec3IsField extends spire.algebra.Field[Vec3] {
  //      // Members declared in spire.algebra.AdditiveGroup
  //   def negate(x:Vec3):Vec3 = -x

  //   // Members declared in spire.algebra.AdditiveMonoid
  //   def zero:Vec3 = Vec3(0)

  //   // Members declared in spire.algebra.AdditiveSemigroup
  //   def plus(x:Vec3,y:Vec3):Vec3 = x + y

  //   // Members declared in spire.algebra.EuclideanRing
  //   def gcd(a:Vec3,b:Vec3):Vec3 = a*b
  //   def mod(a:Vec3,b:Vec3):Vec3 = a/b
  //   def quot(a:Vec3,b:Vec3):Vec3 = a/b

  //   // Members declared in spire.algebra.MultiplicativeGroup
  //   def div(x:Vec3,y:Vec3):Vec3 = x/y

  //   // Members declared in spire.algebra.MultiplicativeMonoid
  //   def one:Vec3 = Vec3(1)

  //   // Members declared in spire.algebra.MultiplicativeSemigroup
  //   def times(x:Vec3,y:Vec3):Vec3 = x*y
  // }

  // implicit object Vec3IsNumeric extends spire.math.Numeric[Vec3] {
  //      // Members declared in spire.algebra.AdditiveGroup
  //   def negate(x:Vec3):Vec3 = -x

  //   // Members declared in spire.algebra.AdditiveMonoid
  //   def zero:Vec3 = Vec3(0)

  //   // Members declared in spire.algebra.AdditiveSemigroup
  //   def plus(x:Vec3,y:Vec3):Vec3 = x + y

  //   // Members declared in spire.algebra.EuclideanRing
  //   def gcd(a:Vec3,b:Vec3):Vec3 = a*b
  //   def mod(a:Vec3,b:Vec3):Vec3 = a/b
  //   def quot(a:Vec3,b:Vec3):Vec3 = a/b

  //   // Members declared in spire.algebra.MultiplicativeGroup
  //   def div(x:Vec3,y:Vec3):Vec3 = x/y

  //   // Members declared in spire.algebra.MultiplicativeMonoid
  //   def one:Vec3 = Vec3(1)

  //   // Members declared in spire.algebra.MultiplicativeSemigroup
  //   def times(x:Vec3,y:Vec3):Vec3 = x*y
  // }

  implicit object Vec3Numeric extends scala.math.Numeric[Vec3] {
    def plus(x: Vec3, y: Vec3) = x+y
    def minus(x: Vec3, y: Vec3) = x-y
    def times(x: Vec3, y: Vec3) = x*y
    def negate(x: Vec3): Vec3 = -x
    def fromInt(x: Int) = Vec3(x)
    def toInt(x: Vec3) = x.mag().toInt
    def toLong(x: Vec3) = x.mag().toLong
    def toFloat(x: Vec3) = x.mag()
    def toDouble(x: Vec3) = x.mag().toDouble
    def compare(x:Vec3,y:Vec3) = (x.mag() - y.mag()).toInt
    def parseString(str:String): Option[Vec3] = None
  }
}

class Vec3( var x: Float, var y: Float, var z: Float ) extends Serializable {
  implicit def toF( d: Double ):Float = d.toFloat

  override def equals(other:Any) = other match {
    case v:Vec3 => this == v
    case _ => false
  }
  override def hashCode = 41*(41+x.hashCode) + 43*y.hashCode + z.hashCode

  def ==(v:Vec3) = {x==v.x && y==v.y && z==v.z}
  def apply(i:Int) = i match { case 0 => x; case 1 => y; case 2 => z;}
  def update(i:Int,v:Float) = i match { case 0 => x=v; case 1 => y=v; case 2 => z=v;}
  def set(v:Vec3) = { x=v.x; y=v.y; z=v.z }
  def set(v:Float) = { x=v; y=v; z=v }
  // def set(v:Double) = { x=v; y=v; z=v }
  def set(a:Float,b:Float,c:Float) = { x=a; y=b; z=c; }
  // def set(a:Double,b:Double,c:Double) = { x=a; y=b; z=c; }
  def set(v:(Float,Float,Float)) = { x=v._1; y=v._2; z=v._3 }
  def +(v: Vec3) = Vec3( x+v.x, y+v.y, z+v.z )
  def +=(v: Vec3) = { x+=v.x; y+=v.y; z+=v.z; this}
  def -(v: Vec3) = Vec3( x-v.x, y-v.y, z-v.z )
  def -=(v: Vec3) = { x-=v.x; y-=v.y; z-=v.z; this}
  def unary_- = Vec3( -x, -y, -z ) 
  def *(s: Float ) = Vec3(s*x, s*y, s*z)
  // def *(s: Double ) = Vec3(s*x, s*y, s*z)
  def *(v: Vec3 ) = Vec3(v.x*x, v.y*y, v.z*z)
  def *=(s: Float) = { x*=s; y*=s; z*=s; this}
  // def *=(s: Double) = { x*=s; y*=s; z*=s; this}
  def *=(v: Vec3) = { x*=v.x; y*=v.y; z*=v.z; this}
  def /(v: Vec3 ) = Vec3(x/v.x, y/v.y, z/v.z)
  def /(s: Float ) = Vec3(x/s, y/s, z/s)
  def /=(s: Float ) = {x=x/s; y=y/s; z=z/s; this}
  // def /=(s: Double ) = {x=x/s; y=y/s; z=z/s; this}
  
  def dot(v: Vec3) : Float = x*v.x + y*v.y + z*v.z
  def cross(v: Vec3) = Vec3( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = { this *= (1.0f / mag()); this } 
  def normalized() = this * (1.0f / mag() )

  def zero() = { x=0; y=0; z=0; this }
  def isZero() = {x == 0f && y == 0f && z == 0f}

  def lerp( v:Vec3, d:Float ) = this + (v-this)*d
  def lerpTo( v:Vec3, d:Float) = this.set(this.lerp(v,d))

  def wrap(l:Vec3,h:Vec3) = {
    x = util.wrap(x,l.x,h.x)
    y = util.wrap(y,l.y,h.y)
    z = util.wrap(z,l.z,h.z)
  }
  def min(v:Vec3) = {
    var min = Vec3(this)
    if(v.x < x) min.x = v.x
    if(v.y < y) min.y = v.y
    if(v.z < z) min.z = v.z
    min
  }
  def max(v:Vec3) = {
    var max = Vec3(this)
    if(v.x > x) max.x = v.x
    if(v.y > y) max.y = v.y
    if(v.z > z) max.z = v.z
    max
  }

  def xy = Vec2(x,y)
  def xz = Vec2(x,z)
  def yz = Vec2(y,z)
  def yx = Vec2(y,x)
  def zx = Vec2(z,x)
  def zy = Vec2(z,y)

  override def toString() = "[" + x + " " + y + " " + z + "]"
}

object Vec2 {

  def apply() = new Vec2(0,0)
  def apply(v:Vec2) = new Vec2(v.x,v.y)
  def apply( v: Float=0f) = new Vec2( v, v)
  def apply( vv: Double) = { val v=vv.toFloat; new Vec2( v, v) }
  def apply( x: Float, y: Float) = new Vec2(x,y)
  def apply( x: Double, y: Double) =  new Vec2(x.toFloat,y.toFloat) 

  def unapply(v: Vec2): Some[(Float,Float)] = Some((v.x,v.y))

  // implicit object Vec2IsField extends spire.algebra.Field[Vec2] {
  //      // Members declared in spire.algebra.AdditiveGroup
  //   def negate(x:Vec2):Vec2 = -x

  //   // Members declared in spire.algebra.AdditiveMonoid
  //   def zero:Vec2 = Vec2(0)

  //   // Members declared in spire.algebra.AdditiveSemigroup
  //   def plus(x:Vec2,y:Vec2):Vec2 = x + y

  //   // Members declared in spire.algebra.EuclideanRing
  //   def gcd(a:Vec2,b:Vec2):Vec2 = a*b
  //   def mod(a:Vec2,b:Vec2):Vec2 = a/b
  //   def quot(a:Vec2,b:Vec2):Vec2 = a/b

  //   // Members declared in spire.algebra.MultiplicativeGroup
  //   def div(x:Vec2,y:Vec2):Vec2 = x/y

  //   // Members declared in spire.algebra.MultiplicativeMonoid
  //   def one:Vec2 = Vec2(1)

  //   // Members declared in spire.algebra.MultiplicativeSemigroup
  //   def times(x:Vec2,y:Vec2):Vec2 = x*y
  // }
  // implicit object Vec2IsNumeric extends spire.math.Numeric[Vec2] {
  //      // Members declared in spire.algebra.AdditiveGroup
  //   def negate(x:Vec2):Vec2 = -x

  //   // Members declared in spire.algebra.AdditiveMonoid
  //   def zero:Vec2 = Vec2(0)

  //   // Members declared in spire.algebra.AdditiveSemigroup
  //   def plus(x:Vec2,y:Vec2):Vec2 = x + y

  //   // Members declared in spire.algebra.EuclideanRing
  //   def gcd(a:Vec2,b:Vec2):Vec2 = a*b
  //   def mod(a:Vec2,b:Vec2):Vec2 = a/b
  //   def quot(a:Vec2,b:Vec2):Vec2 = a/b

  //   // Members declared in spire.algebra.MultiplicativeGroup
  //   def div(x:Vec2,y:Vec2):Vec2 = x/y

  //   // Members declared in spire.algebra.MultiplicativeMonoid
  //   def one:Vec2 = Vec2(1)

  //   // Members declared in spire.algebra.MultiplicativeSemigroup
  //   def times(x:Vec2,y:Vec2):Vec2 = x*y
  //   def +:(x:Vec2) =
  // }

  // implicit object Vec2Numeric extends scala.math.Numeric[Vec2] {
  //   def plus(x: Vec2, y: Vec2) = x+y
  //   def minus(x: Vec2, y: Vec2) = x-y
  //   def times(x: Vec2, y: Vec2) = x*y
  //   def negate(x: Vec2): Vec2 = -x
  //   def fromInt(x: Int) = Vec2(x)
  //   def toInt(x: Vec2) = x.mag().toInt
  //   def toLong(x: Vec2) = x.mag().toLong
  //   def toFloat(x: Vec2) = x.mag()
  //   def toDouble(x: Vec2) = x.mag().toDouble
  //   def compare(x:Vec2,y:Vec2) = (x.mag() - y.mag()).toInt
  // }
}

class Vec2( var x: Float, var y: Float ) extends Serializable {
  implicit def toF( d: Double ):Float = d.toFloat

  def ==(v:Vec2) = {x==v.x && y==v.y}
  def apply(i:Int) = i match { case 0 => x; case 1 => y}
  def update(i:Int,v:Float) = i match { case 0 => x=v; case 1 => y=v}
  def set(v:Vec2) = { x=v.x; y=v.y }
  def set(v:Float) = { x=v; y=v }
  def set(a:Float,b:Float) = { x=a; y=b}
  def set(v:(Float,Float)) = { x=v._1; y=v._2 }
  def +(v: Vec2) = Vec2( x+v.x, y+v.y)
  def +=(v: Vec2) = { x+=v.x; y+=v.y }
  def -(v: Vec2) = Vec2( x-v.x, y-v.y )
  def -=(v: Vec2) = { x-=v.x; y-=v.y }
  def unary_- = Vec2( -x, -y ) 
  def *(s: Float ) = Vec2(s*x, s*y)
  // def *(s: Double ) = Vec2(s*x, s*y)
  def *(v: Vec2 ) = Vec2(v.x*x, v.y*y)
  def *=(s: Float) = { x*=s; y*=s}
  // def *=(s: Double) = { x*=s; y*=s}
  def /(v: Vec2 ) = Vec2(x/v.x, y/v.y)
  def /(s: Float ) = Vec2(x/s, y/s)
  // def /(s: Double ) = Vec2(x/s, y/s)
  
  def dot(v: Vec2) : Float = x*v.x + y*v.y
  def cross( v: Vec2) = x*v.y - y*v.x
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = { val m = mag(); if(m != 0f) this *= (1.0f / mag()); this }
  def normalized() = { val m = mag(); if(m != 0f) this * (1.0f / mag()) else this }

  def zero() = {x=0;y=0}

  def lerp( v:Vec2, d:Float ) = this + (v-this)*d
  def lerpTo( v:Vec2, d:Float) = this.set(this.lerp(v,d))

  def wrap(l:Vec2,h:Vec2) = {
    x = util.wrap(x,l.x,h.x)
    y = util.wrap(y,l.y,h.y)
  }

  override def toString() = "[" + x + " " + y + "]"
}




