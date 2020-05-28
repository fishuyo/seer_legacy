
package com.fishuyo.seer
package spatial

object AABB {
  def apply( min:Vec3, max:Vec3 ) = new AABB(min,max)
  def apply( cen:Vec3, halfsize:Float ) = new AABB(cen,halfsize)
}

class AABB( var min:Vec3, var max:Vec3 ) {

  var center:Vec3 = min.lerp(max, .5f)
  var halfsize = math.abs(center.x - min.x).toFloat

  //println( "AABB created center: " + a + " " + b + " "+c+" "+halfsize)

  def this( cen:Vec3, halfsize:Float ) = this( cen - Vec3(halfsize), cen + Vec3(halfsize))

  def contains( p:Vec3 ):Boolean = {
    if( p.x < min.x || p.x > max.x) return false
    if( p.y < min.y || p.y > max.y) return false
    if( p.z < min.z || p.z > max.z) return false
    true
  }

  def intersectsSphere( cen: Vec3, r: Float ):Boolean = {
    var dx = 0f; var dy = 0f; var dz = 0f; var d = 0f;
    if( cen.x < min.x ) dx = cen.x - min.x
    else if( cen.x > max.x ) dx = cen.x - max.x
    if( cen.y < min.y ) dy = cen.y - min.y
    else if( cen.y > max.y ) dy = cen.y - max.y
    if( cen.z < min.z ) dz = cen.z - min.z
    else if( cen.z > max.z ) dz = cen.z - max.z
    d = dx*dx + dy*dy + dz*dz
    d <= r*r
  }

}
