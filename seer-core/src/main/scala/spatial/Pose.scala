/* Port of Allocore al_Pose.hpp originally by
	Wesley Smith, 2010, wesley.hoke@gmail.com
	Lance Putnam, 2010, putnam.lance@gmail.com
	Graham Wakefield, 2010, grrrwaaa@gmail.com
	Pablo Colapinto, 2010, wolftype@gmail.com
*/

package com.fishuyo.seer
package spatial

object Pose {
	def apply():Pose = apply(Vec3(),Quat())
	def apply(p:Pose):Pose = apply(Vec3(p.pos),Quat(p.quat))
	def apply( pos:Vec3=Vec3(0), quat:Quat=Quat(1,0,0,0)) = new Pose(pos,quat)
}
/** Class Pose represents a position and orientation in 3d space */
class Pose( var pos:Vec3=Vec3(0), var quat:Quat=Quat(1,0,0,0) ){
  
  def vec = pos

  /** translate and rotate pose by another pose */
  def *(p:Pose) = new Pose( pos+p.pos, quat*p.quat)
  def *=(p:Pose) = { pos += p.pos; quat *= p.quat; this }

  def set(p:Pose) = { pos.set(p.pos); quat.set(p.quat)}

  def translate(x:Float,y:Float,z:Float):Pose = translate(Vec3(x,y,z))
  def translate(p:Vec3):Pose = { pos += p; this }

  def rotate(x:Float, y:Float, z:Float):Pose = rotate(Quat(x,y,z))
  def rotate(q:Quat):Pose = { quat *= q; this }

  //return Azimuth Elevation and distance to point v
  //def getAED(v:Vec3): (Float,Float,Float) = {}

  def getUnitVectors():(Vec3,Vec3,Vec3) = (quat.toX, quat.toY, quat.toZ)
  def getDirVectors():(Vec3,Vec3,Vec3) = (quat.toX, quat.toY, -quat.toZ)
  def ur() = quat.toX
  def uu() = quat.toY
  def uf() = -quat.toZ

  def setIdentity = { pos.zero(); quat.setIdentity() }

  /** return linear interpolated Pose from this to p by amount d*/
  def lerp(p:Pose, d:Float) = new Pose( pos.lerp(p.pos, d), quat.slerp(p.quat, d) )

  // def toMatrix() = {
  // 	val m = quat.toMatrix
  // 	m.`val`(12) = pos.x
  // 	m.`val`(13) = pos.y
  // 	m.`val`(14) = pos.z
  // 	m
  // }
}

