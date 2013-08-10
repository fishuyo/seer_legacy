
package com.fishuyo
package spatial

import maths._

object AABB {
  def apply( a:Vec3, b:Vec3 ) = new AABB(a,b)
  def apply( c:Vec3, halfsize:Float ) = new AABB(c,halfsize)
}
class AABB( var a:Vec3, var b:Vec3 ) {

  var c:Vec3 = a.lerp(b, .5f)
  var halfsize = math.abs(c.x - a.x).toFloat

  //println( "AABB created center: " + a + " " + b + " "+c+" "+halfsize)

  def this( center:Vec3, halfsize:Float ) = this( center - Vec3(halfsize), center + Vec3(halfsize))

  def contains( p:Vec3 ):Boolean = {
    if( p.x < a.x || p.x > b.x) return false
    if( p.y < a.y || p.y > b.y) return false
    if( p.z < a.z || p.z > b.z) return false
    true
  }
  def intersectsSphere( c: Vec3, r: Float ):Boolean = {
    var dx = 0.f; var dy = 0.f; var dz = 0.f; var d = 0.f;
    if( c.x < a.x ) dx = c.x - a.x
    else if( c.x > b.x ) dx = c.x - b.x
    if( c.y < a.y ) dy = c.y - a.y
    else if( c.y > b.y ) dy = c.y - b.y
    if( c.z < a.z ) dz = c.z - a.z
    else if( c.z > b.z ) dz = c.z - b.z
    d = dx*dx + dy*dy + dz*dz
    d <= r*r
  }

}
