package com.fishuyo.seer
package spatial

import maths.Vec3

object Ray{
	def apply(o:Vec3,d:Vec3) = new Ray(o,d)
}

class Ray( val o: Vec3, val d: Vec3 ){

	def apply( t: Float ) : Vec3 = o + d*t


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

  def intersectQuad(cen:Vec3, w:Float, h:Float) : Option[Float] = {
  	val n = Vec3(0,0,1)
    val dn = d dot n

    val vertices = (cen + Vec3(-w,-h,0), cen + Vec3(w,-h,0), cen + Vec3(w,h,0), cen + Vec3(-w,h,0))
    
    if( dn == 0) return None

    val t = -(( o - vertices._1 ) dot n ) / dn
    if( t < 0.f) return None
    val x = this(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._4 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ||
        (((vertices._1 - vertices._4) cross ( x - vertices._4 )) dot n) < 0 ) return None

    return Some(t)
  }


	override def toString() = o + " -> " + d
}

//Intersection with Geometry

// 1. Geometry.intersect( ray )

// 2. intersect( Geometry, ray )

// 3. ray.intersect( Geometry )


// val ray = Camera.ray(x,y)
// ray.intersect( )