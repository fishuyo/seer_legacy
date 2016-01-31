
package com.fishuyo.seer
package spatial

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
  }
}

class Vec3( var x: Float, var y: Float, var z: Float ) extends Serializable {
  //def ==(v:Vec3) = {x==v.x && y==v.y && z==v.z}
  def apply(i:Int) = i match { case 0 => x; case 1 => y; case 2 => z;}
  def update(i:Int,v:Float) = i match { case 0 => x=v; case 1 => y=v; case 2 => z=v;}
  def set(v:Vec3) = { x=v.x; y=v.y; z=v.z }
  def set(v:Float) = { x=v; y=v; z=v }
  def set(a:Float,b:Float,c:Float) = { x=a; y=b; z=c; }
  def set(v:(Float,Float,Float)) = { x=v._1; y=v._2; z=v._3 }
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
  def *=(v: Vec3) = { x*=v.x; y*=v.y; z*=v.z}
  def /(s: Float ) = Vec3(x/s, y/s, z/s)
  def /=(s: Float ) = {x/=s; y/=s; z/=s }
  
  def dot(v: Vec3) : Float = x*v.x + y*v.y + z*v.z
  def cross( v: Vec3) = Vec3( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = { this *= (1.0f / mag()); this } 
  def normalized() = this * (1.0f / mag() )

  def zero() = {x=0;y=0;z=0}

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

  implicit object Vec3Numeric extends scala.math.Numeric[Vec2] {
    def plus(x: Vec2, y: Vec2) = x+y
    def minus(x: Vec2, y: Vec2) = x-y
    def times(x: Vec2, y: Vec2) = x*y
    def negate(x: Vec2): Vec2 = -x
    def fromInt(x: Int) = Vec2(x)
    def toInt(x: Vec2) = x.mag().toInt
    def toLong(x: Vec2) = x.mag().toLong
    def toFloat(x: Vec2) = x.mag()
    def toDouble(x: Vec2) = x.mag().toDouble
    def compare(x:Vec2,y:Vec2) = (x.mag() - y.mag()).toInt
  }
}

class Vec2( var x: Float, var y: Float ) extends Serializable {
  //def ==(v:Vec2) = {x==v.x && y==v.y && z==v.z}
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
  def *(v: Vec2 ) = Vec2(v.x*x, v.y*y)
  def *=(s: Float) = { x*=s; y*=s}
  def /(s: Float ) = Vec2(x/s, y/s)
  
  def dot(v: Vec2) : Float = x*v.x + y*v.y
  def cross( v: Vec2) = x*v.y - y*v.x
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = this *= (1.0f / mag() ) //fix this
  def normalized() = this * (1.0f / mag() )

  def zero() = {x=0;y=0}

  def lerp( v:Vec2, d:Float ) = this + (v-this)*d
  def lerpTo( v:Vec2, d:Float) = this.set(this.lerp(v,d))

  override def toString() = "[" + x + " " + y + "]"
}




