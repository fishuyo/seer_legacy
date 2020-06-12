
package com.fishuyo.seer
package spatial

object AABB {
  def apply(min:Vec3, max:Vec3) = new AABB(min,max)
  def apply(cen:Vec3, halfsize:Float) = new AABB(cen - Vec3(halfsize), cen + Vec3(halfsize))
}

class AABB(val min:Vec3, val max:Vec3) {

  val center:Vec3 = min.lerp(max, 0.5f)
  val dim:Vec3 = max - min
  // var halfsize = math.abs(center.x - min.x).toFloat

  def set(mn:Vec3,mx:Vec3) = {
    min.set(mn); max.set(mx)
    dim.set(max - min)
    center.set(min + dim * 0.5f)
  }

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
