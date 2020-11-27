
package seer
package spatial.immutable

object Vec3 {

  def apply() = new Vec3(0,0,0)
  def apply(v:Vec3) = new Vec3(v.x,v.y,v.z)
  def apply( v: Float=0f) = new Vec3( v, v, v)
  def apply( vv: Double) = { val v=vv.toFloat; new Vec3( v, v, v) }
  def apply( x: Float, y: Float, z: Float) = new Vec3(x,y,z)
  def apply( x: Double, y: Double, z: Double) =  new Vec3(x.toFloat,y.toFloat,z.toFloat) 

  def unapply( v: Vec3): Some[(Float,Float,Float)] = Some((v.x,v.y,v.z))

  implicit object Vec3Numeric extends math.Numeric[Vec3] {
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
    def parseString(str:String) = None

  }
}

class Vec3( val x: Float, val y: Float, val z: Float ){
  def ==(v:Vec3) = {x==v.x && y==v.y && z==v.z}
  def apply(i:Int) = i match { case 0 => x; case 1 => y; case 2 => z;}
  def +(v: Vec3) = Vec3( x+v.x, y+v.y, z+v.z )
  def -(v: Vec3) = Vec3( x-v.x, y-v.y, z-v.z )
  def unary_- = Vec3( -x, -y, -z ) 
  def *(s: Float ) = Vec3(s*x, s*y, s*z)
  def *(v: Vec3 ) = Vec3(v.x*x, v.y*y, v.z*z)
  def /(s: Float ) = Vec3(x/s, y/s, z/s)
  
  def dot(v: Vec3) : Float = x*v.x + y*v.y + z*v.z
  def cross( v: Vec3) = Vec3( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalized() = this * (1.0f / mag() )

  def lerp( v:Vec3, d:Float ) = this + (v-this)*d

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

  implicit object Vec3Numeric extends math.Numeric[Vec2] {
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
    def parseString(str:String) = None
  }
}

class Vec2( val x: Float, val y: Float ){
  def ==(v:Vec2) = {x==v.x && y==v.y}
  def apply(i:Int) = i match { case 0 => x; case 1 => y}
  def +(v: Vec2) = Vec2( x+v.x, y+v.y)
  def -(v: Vec2) = Vec2( x-v.x, y-v.y )
  def unary_- = Vec2( -x, -y ) 
  def *(s: Float ) = Vec2(s*x, s*y)
  def *(v: Vec2 ) = Vec2(v.x*x, v.y*y)
  def /(s: Float ) = Vec2(x/s, y/s)
  
  def dot(v: Vec2) : Float = x*v.x + y*v.y
  def cross( v: Vec2) = x*v.y - y*v.x
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalized() = this * (1.0f / mag() )

  def lerp( v:Vec2, d:Float ) = this + (v-this)*d

  override def toString() = "[" + x + " " + y + "]"
}




