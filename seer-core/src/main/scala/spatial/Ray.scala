package com.fishuyo
package spatial

import maths.Vec3

object Ray{
	def apply(o:Vec3,d:Vec3) = new Ray(o,d)
}
class Ray( val o: Vec3, val d: Vec3 ){
 def apply( t: Float ) : Vec3 = o + d*t
 override def toString() = o + " -> " + d
}

