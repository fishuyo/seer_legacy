package com.fishuyo
package ray
import maths.Vec3

import scalacl._

class Ray( val o: Vec3, val d: Vec3 ){
 def apply( t: Float ) : Vec3 = o + d*t
 override def toString() = o + " -> " + d
}

class Beam( val r: (Ray,Ray,Ray,Ray) ){
  
  implicit val context = Context.best

  val ox = CLArray( r._1.o.x, r._2.o.x, r._3.o.x, r._4.o.x )
  val oy = CLArray( r._1.o.y, r._2.o.y, r._3.o.y, r._4.o.y )
  val oz = CLArray( r._1.o.z, r._2.o.z, r._3.o.z, r._4.o.z )
  val dx = CLArray( r._1.d.x, r._2.d.x, r._3.d.x, r._4.d.x )
  val dy = CLArray( r._1.d.y, r._2.d.y, r._3.d.y, r._4.d.y )
  val dz = CLArray( r._1.d.z, r._2.d.z, r._3.d.z, r._4.d.z )



}
