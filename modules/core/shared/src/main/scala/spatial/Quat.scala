/* 
* Quaternions!
*   Port of allocore implementation in al_Quat.hpp
*
*/

package com.fishuyo.seer
package spatial

object Quat {
  implicit def toF( d: Double ) = d.toFloat
  
  val eps = 0.0000001
  val acc_max = 1.000001
  val acc_min = 0.999999
  def apply( w:Float, x:Float, y:Float, z:Float ) = new Quat(w,x,y,z)
  def apply( w:Double, x:Double, y:Double, z:Double ) = new Quat(w,x,y,z)
  def apply(q:Quat) = new Quat(q.w,q.x,q.y,q.z)
  def apply(x:Float, y:Float, z:Float) = new Quat(1,0,0,0).fromEuler(x,y,z)
  def apply(x:Double, y:Double, z:Double) = new Quat(1,0,0,0).fromEuler(x,y,z)
  def apply(euler:Vec3) = new Quat(1,0,0,0).fromEuler(euler)
  def apply() = new Quat(1,0,0,0)

  def up = this().fromEuler(-Pi/2f,0f,0f)
  def down = this().fromEuler(Pi/2f,0f,0f)
  def left = this().fromEuler(0f,-Pi/2f,0f)
  def right = this().fromEuler(0f,Pi/2f,0f)
  def forward = this()
  def back = this().fromEuler(0f,Pi,0f)
}

class Quat(var w:Float, var x:Float, var y:Float, var z:Float ) extends Serializable {
  implicit def toF( d: Double ) = d.toFloat

  def unary_- = Quat( -w, -x, -y, -z ) 
  def +(v: Quat) = Quat( w+v.w, x+v.x, y+v.y, z+v.z )
  def -(v: Quat) = Quat( w-v.w, x-v.x, y-v.y, z-v.z )
  def *(q: Quat) = Quat( w*q.w-x*q.x-y*q.y-z*q.z, w*q.x+x*q.w+y*q.z-z*q.y, w*q.y+y*q.w+z*q.x-x*q.z, w*q.z+z*q.w+x*q.y-y*q.x )
  def *(s: Float ) = Quat(s*w, s*x, s*y, s*z)
  def /(s: Float ) = Quat(w/s, x/s, y/s, z/s)
  
  def +=(v: Quat) = { w+=v.w; x+=v.x; y+=v.y; z+=v.z }
  def -=(v: Quat) = { w-=v.w; x-=v.x; y-=v.y; z-=v.z }
  def *=(q: Quat) = set(this*q)
  def *=(s: Float) = { w*=s; x*=s; y*=s; z*=s }
  
  def set(q: Quat) = { w=q.w; x=q.x; y=q.y; z=q.z }
  def set(qw:Float,a:Float,b:Float,c:Float) = { x=a; y=b; z=c; w=qw }

  def dot(v: Quat) : Float = w*v.w + x*v.x + y*v.y + z*v.z
  //def cross( v: Quat) = Quat( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() )
  def normalize() = {
    val m = magSq()
    if( m*m < Quat.eps ) Quat().setIdentity()
    else if( m > Quat.acc_max || m < Quat.acc_min ) this * (1.0f / math.sqrt( m ))
    else this
  }

  def conj = Quat( w, -x,-y,-z )
  def sgn = Quat(w,x,y,z).normalize
  def inverse = sgn.conj
  def recip = conj / magSq   

  def zero() = {w=0;x=0;y=0;z=0;this}
  def setIdentity() = {w=1;x=0;y=0;z=0;this}

  def fromAxisX( ang: Float ) = {w=math.cos(ang*.5f);x=math.sin(ang*.5f);y=0;z=0; this}
  def fromAxisY( ang: Float ) = {w=math.cos(ang*.5f);x=0;y=math.sin(ang*.5f);z=0; this}
  def fromAxisZ( ang: Float ) = {w=math.cos(ang*.5f);x=0;y=0;z=math.sin(ang*.5f); this}
  def fromAxisAngle( ang: Float, axis:Vec3 ) = { 
    val sin2a = math.sin(ang*.5f)
    w = math.cos(ang*.5f)
    x = axis.x * sin2a
    y = axis.y * sin2a
    z = axis.z * sin2a
    this
  }
  // from euler angles ( elevation, azimuth, bank )
  def fromEuler( x:Float, y:Float, z:Float ): Quat = fromEuler((x,y,z))
  def fromEuler( eu:Vec3 ) : Quat = fromEuler((eu.x,eu.y,eu.z))
  def fromEuler( eu:(Float,Float,Float) ) : Quat= { //eu = Vec3( el, az, ba )
    val c1 = math.cos(eu._1*.5f); val c2 = math.cos(eu._2*.5f); val c3 = math.cos(eu._3*.5f)   
    val s1 = math.sin(eu._1*.5f); val s2 = math.sin(eu._2*.5f); val s3 = math.sin(eu._3*.5f) 
    val tw = c2*c1; val tx = c2*s1; val ty = s2*c1; val tz = -s2*s1
    w = tw*c3 - tz*s3; x = tx*c3 + ty*s3; y = ty*c3 - tx*s3; z = tw*s3 + tz*c3
    this
  }

  //local unit vectors
  def toX() = Vec3(1.0 - 2.0*y*y - 2.0*z*z, 2.0*x*y + 2.0*z*w, 2.0*x*z - 2.0*y*w)
  def toY() = Vec3(2.0*x*y - 2.0*z*w, 1.0 - 2.0*x*x - 2.0*z*z, 2.0*y*z + 2.0*x*w)
  def toZ() = Vec3(2.0*x*z + 2.0*y*w, 2.0*y*z - 2.0*x*w, 1.0 - 2.0*x*x - 2.0*y*y)

  def toEuler() : (Float,Float,Float) = {
    val az = math.asin( -2.0 * (x*z - w*y))
    val el = math.atan2( 2.0 * (y*z + w*x), w*w - x*x - y*y + z*z)
    val bank = math.atan2( 2.0 * (x*y + w*z), w*w + x*x - y*y - z*z)
    (el,az,bank)
  }
  def toEulerVec() : Vec3 = {
    val az = math.asin( -2.0 * (x*z - w*y))
    val el = math.atan2( 2.0 * (y*z + w*x), w*w - x*x - y*y + z*z)
    val bank = math.atan2( 2.0 * (x*y + w*z), w*w + x*x - y*y - z*z)
    Vec3(el,az,bank)
  }

  def fromMatrix(m:Mat4):Quat = fromMatrix(m.data)
  def fromMatrix(m:Array[Float]) = {
    val trace = m(0)+m(5)+m(10)
    w = math.sqrt(1f + trace)*0.5f

    if(trace > 0f) {
      x = (m(9) - m(6))/(4f*w)
      y = (m(2) - m(8))/(4f*w)
      z = (m(4) - m(1))/(4f*w)
    }
    else {
      if(m(0) > m(5) && m(0) > m(10)) {
        // m(0) is greatest
        x = math.sqrt(1f + m(0)-m(5)-m(10))*0.5f
        w = (m(9) - m(6))/(4f*x)
        y = (m(4) + m(1))/(4f*x)
        z = (m(8) + m(2))/(4f*x)
      }
      else if(m(5) > m(0) && m(5) > m(10)) {
        // m(1) is greatest
        y = math.sqrt(1f + m(5)-m(0)-m(10))*0.5f
        w = (m(2) - m(8))/(4f*y)
        x = (m(4) + m(1))/(4f*y)
        z = (m(9) + m(6))/(4f*y)
      }
      else { //if(m(10) > m(0) && m(10) > m(5)) {
        // m(2) is greatest
        z = math.sqrt(1f + m(10)-m(0)-m(5))*0.5f
        w = (m(4) - m(1))/(4f*z)
        x = (m(8) + m(2))/(4f*z)
        y = (m(9) + m(6))/(4f*z)
      }
    }
    this
  }

  def toCoordinateFrame(ux:Vec3, uy:Vec3, uz:Vec3) = {
    val wx=2*w*x; val wy=2*w*y; val wz=2*w*z
    val xx=2*x*x; val xy=2*x*y; val xz=2*x*z
    val yy=2*y*y; val yz=2*y*z; val zz=2*z*z

    ux.x = -zz - yy + 1
    ux.y = wz + xy
    ux.z = xz - wy

    uy.x = xy - wz
    uy.y = -zz - xx + 1
    uy.z = wx + yz

    uz.x = wy + xz
    uz.y = yz - wx
    uz.z = -yy - xx + 1

    (ux,uy,uz)
  }

  def toMatrix(m:Mat4):Mat4 = { toMatrix(m.data); m }
  def toMatrix(m:Array[Float]) = {
    val ux,uy,uz = Vec3()
    toCoordinateFrame(ux,uy,uz)

    m( 0) = ux.x;  m( 4) = uy.x;  m( 8) = uz.x;  m(12) = 0;
    m( 1) = ux.y;  m( 5) = uy.y;  m( 9) = uz.y;  m(13) = 0;
    m( 2) = ux.z;  m( 6) = uy.z;  m(10) = uz.z;  m(14) = 0;
    m( 3) = 0;    m( 7) = 0;    m(11) = 0;    m(15) = 1;
    m
  }


  // def toMatrix() = new Matrix4(new Quaternion(x,y,z,w))
  // def toQuaternion() = new Quaternion(x,y,z,w)

  def slerp(q:Quat, d:Float): Quat = {
    var (a,b) = (0f,0f)
    var negb = false
    var dotprod = dot(q)

    if( dotprod < -1 ) dotprod = -1
    else if( dotprod > 1 ) dotprod = 1

    if( dotprod < 0){
      dotprod = -dotprod
      negb = true
    }

    val ang = math.acos( dotprod )
    if( math.abs(ang) > Quat.eps ){
      val sini = 1f / math.sin(ang)
      a = math.sin(ang * (1.0 - d))*sini
      b = math.sin(ang*d)*sini
      if(negb) b = -b
    } else {
      a = d
      b = 1.0 - d
    }

    val quat = Quat(a*w+b*q.w, a*x+b*q.x, a*y+b*q.y, a*z+b*q.z)
    quat.normalize
  }

  def slerpTo(q:Quat, d:Float) = this.set( this.slerp(q,d))
  
  def rotate(v:Vec3) = {
      // dst = ((q * quat(v)) * q^-1)
      // faster & simpler:
      // we know quat(v).w == 0
      val p = Quat(
        -x*v.x - y*v.y - z*v.z,
         w*v.x + y*v.z - z*v.y,
         w*v.y - x*v.z + z*v.x,
         w*v.z + x*v.y - y*v.x
      )
      // faster & simpler:
      // we don't care about the w component
      // and we know that conj() is simply (w, -x, -y, -z):
      Vec3(
        p.x*w - p.w*x + p.z*y - p.y*z,
        p.y*w - p.w*y + p.x*z - p.z*x,
        p.z*w - p.w*z + p.y*x - p.x*y
      )
    //  p *= conj();  // p * q^-1
    //  return Vec<3,T>(p.x, p.y, p.z);
  }

  def getRotationTo(src:Vec3, dst:Vec3):Quat = {
    val q = Quat()
    
    val d = src dot dst
    if (d >= 1f) {
      // vectors are the same
      return q;
    }
    if (d < -0.999999999f) {
      // vectors are nearly opposing
      // pick an axis to rotate around
      var axis = Vec3(0, 1, 0) cross src
      // if colinear, pick another:
      if (axis.magSq() < 0.00000000001f) {
        axis = Vec3(0, 0, 1) cross src
      }
      //axis.normalize();
      q.fromAxisAngle(math.Pi, axis);
    } else {
      val s = math.sqrt((d+1f)*2f)
      val invs = 1.0/s
      val c = src cross dst
      q.x = c(0) * invs;
      q.y = c(1) * invs;
      q.z = c(2) * invs;
      q.w = s * 0.5;
    }
    return q.normalize();
  }

  def fromForwardUp(dir:Vec3, up:Vec3):Quat = { 
    val v2 = up.cross(dir).normalize
    val v3 = dir.cross(v2)
    val Vec3(m00,m01,m02) = v2
    val Vec3(m10,m11,m12) = v3
    val Vec3(m20,m21,m22) = dir

    val num8 = (m00 + m11) + m22
    val q = Quat()
    if(num8 > 0.0f){
      var num = math.sqrt(num8 + 1)
      q.w = num * 0.5
      num = 0.5 / num
      q.x = (m12 - m21) * num
      q.y = (m20 - m02) * num
      q.z = (m01 - m10) * num
      return q
    }

    if((m00 >= m11) && (m00 >= m22)){
      val num7 = math.sqrt(((1 + m00) - m11) - m22)
      val num4 = 0.5 / num7
      q.x = 0.5 * num7
      q.y = (m01 + m10) * num4
      q.z = (m02 + m20) * num4
      q.w = (m12 - m21) * num4
      return q
    }
    if(m11 > m22){
      val num6 = math.sqrt(((1 + m11) - m00) - m22)
      val num3 = 0.5 / num6
      q.x = (m10 + m01) * num3
      q.y = 0.5 * num6
      q.z = (m21 + m12) * num3
      q.w = (m20 - m02) * num3
      return q
    }
    val num5 = math.sqrt(((1 + m22) - m00) - m11)
    val num2 = 0.5 / num5
    q.x = (m20 + m02) * num2
    q.y = (m21 + m12) * num2
    q.z = 0.5 * num5
    q.w = (m01 - m10) * num2
    return q
  }

  def pow(v:Float):Quat = {
    val m = mag()
    if( m == 0f) return Quat()
    val theta = math.acos(w / m)
    val imag = Vec3(x,y,z) / (m * math.sin(theta))
    imag *= math.sin(v*theta)
    Quat(math.cos(v*theta), imag.x, imag.y, imag.z) * math.pow(m,v)
  }

  override def toString() = "[" + w + " " + x + " " + y + " " + z + "]"
}


