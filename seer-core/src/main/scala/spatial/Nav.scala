/* Port of Allocore al_Pose.hpp originally by
	Wesley Smith, 2010, wesley.hoke@gmail.com
	Lance Putnam, 2010, putnam.lance@gmail.com
	Graham Wakefield, 2010, grrrwaaa@gmail.com
	Pablo Colapinto, 2010, wolftype@gmail.com
*/

package com.fishuyo.seer
package spatial

object Nav{
	def apply() = new Nav()
}

/** Pose that moves through space */
class Nav( p:Vec3=Vec3(0) ) extends Pose(p) {

	var smooth = 0f
	var scale = 1f
	var vel = Vec3(0); var velS = Vec3(0)
	var worldVel = Vec3(0); var worldVelS = Vec3(0)
	var angVel = Vec3(0); var angVelS = Vec3(0)
	var turn = Vec3(0); var nudge = Vec3(0)
	var mUR = Vec3(0); var mUU = Vec3(0); var mUF = Vec3(0)

	def view(eu:(Float,Float,Float)){ view(Quat().fromEuler(eu)) }
	def view(q:Quat){ quat = q; updateDirVectors() }

	def velPose() = new Pose( velS, Quat().fromEuler(angVelS) )

	def stop() = {
		vel.zero; velS.zero;
		worldVel.zero; worldVelS.zero;
		angVel.zero; angVelS.zero;
		turn.zero; nudge.zero;
		updateDirVectors()
	}

	def moveToOrigin() = {
		quat.setIdentity; pos.zero
		stop
	}

	// def lookAt( p: Vec3, amt:Float=1f) = {

	// }
	def goTo( p:Vec3, amt:Float=1f) = {
		val dir = (p - pos).normalize
	}

	def updateDirVectors() = { quat = quat.normalize; mUR = ur(); mUU = uu(); mUF = uf() }

	def step( dt:Float ) = {
		var s = scale * dt
		val amt = 1f - smooth

		//smooth velocities
		velS = velS.lerp( vel*s + nudge, amt )
		angVelS = angVelS.lerp( angVel*s + turn, amt )
		worldVelS = worldVelS.lerp( worldVel*s, amt )

		nudge.zero; turn.zero

		//rotate
		quat *= Quat().fromEuler(angVelS)
		updateDirVectors()

		//move
		pos.x += velS dot Vec3( mUR.x, mUU.x, mUF.x)
		pos.y += velS dot Vec3( mUR.y, mUU.y, mUF.y)
		pos.z += velS dot Vec3( mUR.z, mUU.z, mUF.z)
		println(pos)

	}
}


