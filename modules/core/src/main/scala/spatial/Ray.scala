package com.fishuyo.seer
package spatial

object Ray{
  def apply(o:Vec3,d:Vec3) = new Ray(o,d)
}

class Ray( val o: Vec3, val d: Vec3 ){

  d.normalize()

  def apply( t: Float ) : Vec3 = o + d*t

  def intersectPlane(p0:Vec3, n:Vec3):Option[Float] = {
    val den = n.dot(d);
    if(den == 0f) None;
    else Some(n.dot(p0 - o) / den)
  }

  def intersectCircle(p0:Vec3, n:Vec3, rmax:Float, rmin:Float = 0f):Option[Float] = {
    val den = n.dot(d)
    if (den == 0) return None
    val t = n.dot(p0 - o) / den
    val r = (apply(t) - p0).mag()
    if (r <= rmax && r >= rmin) Some(t)
    else None
  }

  def intersectSphere(cen:Vec3, r:Float) : Option[Float] = {
    val o_c = o - cen
    val A = d dot d
    val B = 2 * ( d dot o_c )
    val C = (o_c dot o_c) - r*r
    val det = B*B - 4*A*C

    if( det > 0 ){
      val t1 = (-B - math.sqrt(det).toFloat ) / (2*A)
      if ( t1 > 0f ) return Some(t1)
      val t2 = (-B + math.sqrt(det).toFloat ) / (2*A)
      if ( t2 > 0f ) return Some(t2)

    } else if ( det == 0 ){
      val t = -B / (2*A)
      if ( t > 0f ) return Some(t)
    }
    None
  }

  def intersectBox(cen:Vec3, scl:Vec3):Option[Float] = {
    // courtesy of http://www.cs.utah.edu/~awilliam/box/
    var (tmin, tmax, tymin, tymax, tzmin, tzmax) = (0f,0f,0f,0f,0f,0f)

    val mm = Array(Vec3(),Vec3())
    val min = cen - scl / 2;
    val max = cen + scl / 2;
    mm(0) = min;
    mm(1) = max;

    val invD = Vec3(1) / d;
    val s = Array(0,0,0)
    if(invD.x < 0) s(0) = 1
    if(invD.y < 0) s(1) = 1
    if(invD.z < 0) s(2) = 1

    tmin = (mm(s(0)).x - o.x) * invD.x;
    tmax = (mm(1 - s(0)).x - o.x) * invD.x;
    tymin = (mm(s(1)).y - o.y) * invD.y;
    tymax = (mm(1 - s(1)).y - o.y) * invD.y;
    if ((tmin > tymax) || (tymin > tmax)) return None
    if (tymin > tmin) tmin = tymin;
    if (tymax < tmax) tmax = tymax;
    tzmin = (mm(s(2)).z - o.z) * invD.z;
    tzmax = (mm(1 - s(2)).z - o.z) * invD.z;
    if ((tmin > tzmax) || (tzmin > tmax)) return None
    if (tzmin > tmin) tmin = tzmin;
    if (tzmax < tmax) tmax = tzmax;

    if (tmin < 0.0f)
      if (tmax < 0.0f)
        return None
      else
        return Some(tmax)
    else
      return Some(tmin)
  }

  def intersectQuad(cen:Vec3, w:Float, h:Float, quat:Quat=Quat()) : Option[Float] = {
    val n = quat.toZ()
    val dn = d dot n

    val nx = quat.toX()
    val ny = quat.toY()

    // val vertices = (cen + Vec3(-w,-h,0), cen + Vec3(w,-h,0), cen + Vec3(w,h,0), cen + Vec3(-w,h,0))
    val vertices = (cen + nx * -w + ny * -h, cen + nx*w + ny * -h, cen + nx*w + ny*h, cen + nx * -w + ny*h)
    
    if( dn == 0) return None

    val t = -(( o - vertices._1 ) dot n ) / dn
    if( t < 0f) return None
    val x = this(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._4 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ||
        (((vertices._1 - vertices._4) cross ( x - vertices._4 )) dot n) < 0 ) return None

    return Some(t)
  }


  override def toString() = s"$o -> $d"
}
