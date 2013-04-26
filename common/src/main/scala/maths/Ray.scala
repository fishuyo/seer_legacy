package com.fishuyo
package maths

class Ray( val o: Vec3, val d: Vec3 ){
 def apply( t: Float ) : Vec3 = o + d*t
 override def toString() = o + " -> " + d
}

