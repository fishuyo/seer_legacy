
package com.fishuyo.seer
package maths
package particle

import spatial._


trait KinematicState {
	var position = Vec3()
	var lPosition = Vec3()
	var velocity = Vec3()
	var acceleration = Vec3()
}

trait RotationalState extends KinematicState {

	// var pose = Pose()
	// var lPose = Pose()

 //  var restPose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
 //  var relQuat = Quat()

  var euler = Vec3(0)
  var lEuler = Vec3(0)
  var angularAcceleration = Vec3(0)
  var angularVelocity = Vec3(0)

  var quat = Quat()
  var lQuat = Quat()
  var dQuat = Quat()
  var ddQuat = Quat()
}



object Integrators {

	// Kinematic State integrators
	def verlet(k:KinematicState)(dt:Float){
    k.velocity = k.position - k.lPosition
    k.lPosition = k.position
    k.position = k.position + k.velocity + k.acceleration * ( .5f * dt * dt )
    k.acceleration = Vec3(0)
	}

	def euler(k:KinematicState)(dt:Float){
		k.velocity = k.velocity + k.acceleration*dt
		k.lPosition = k.position
		k.position = k.position + k.velocity*dt
		k.acceleration = Vec3(0)
	}

	// rotationalState itnegrators
	def verlet(k:RotationalState)(dt:Float){

		verlet(k.asInstanceOf[KinematicState])(dt)

		k.dQuat = k.quat * k.lQuat.inverse
		k.lQuat = k.quat
		k.quat = ( (k.ddQuat) * k.dQuat * k.quat).normalize
		k.ddQuat = Quat()

   //  k.angularVelocity =  k.euler - k.lEuler  	
  	// k.lEuler = k.euler
  	// k.euler = k.euler + k.angularVelocity + k.angularAcceleration * (.5f*dt*dt)
  	// k.angularAcceleration = Vec3(0)
	}

	def euler(k:RotationalState)(dt:Float){

		euler(k.asInstanceOf[KinematicState])(dt)

		k.angularVelocity = k.angularVelocity + k.angularAcceleration*dt
		k.lEuler = k.euler
		k.euler = k.euler + k.angularVelocity*dt
  	k.angularAcceleration = Vec3(0)
	}
}

object Particle {
	def apply(pos:Vec3) = { val p = new Particle; p.position = pos; p.lPosition = pos; p }
	def apply(pos:Vec3, vel:Vec3) = { val p = new Particle; p.position = pos; p.velocity = vel; p.lPosition = pos - vel; p }
}

class Particle extends KinematicState {

	var mass = 1.f
	var t = 0.f
	var integrate = Integrators.verlet(this)_

	def step(dt:Float){
		t += dt
		integrate(dt)
	}

  def applyForce( f: Vec3 ) = acceleration = acceleration + (f / mass)
  def applyGravity() = acceleration = acceleration + Gravity
  def applyDamping( damp: Float ) = acceleration = acceleration - velocity * (damp / mass)

}

object Stick {
	def apply(pos:Vec3, q:Quat) = { val p = new Stick; p.position = pos; p.lPosition = pos; p.euler = q.toEulerVec; p.lEuler = p.euler; p.quat = q; p.lQuat = q; p }
	def apply(pos:Vec3, vel:Vec3, quat:Quat, angVel:Vec3) = { 
		val p = new Stick; p.position = pos; 
		p.velocity = vel; p.lPosition = pos - vel;
		p.euler = quat.toEulerVec; p.quat = quat
		p.angularVelocity = angVel; p.lQuat = Quat().fromEuler(angVel).inverse * p.quat
		p.lEuler = p.euler - angVel
		p
	}
}

class Stick extends RotationalState {
	var mass = 1.f
	var t = 0.f
	var length = 1.f
	val end = new Particle

	var integrate = Integrators.verlet(this)_

	def step(dt:Float){
		t += dt
		integrate(dt)
		end.lPosition = end.position
		end.position = position + -quat.toZ()*length
		// end.position = position + Quat().fromEuler(euler).toZ()*length
	}

	def applyForce( f: Vec3 ) = acceleration = acceleration + (f / mass)
  def applyGravity() = acceleration = acceleration + Gravity
  def applyDamping( damp: Float ) = acceleration = acceleration - velocity * (damp / mass)	

	def applyAngularForce( f: Vec3 ) = { angularAcceleration = angularAcceleration + (f / mass); ddQuat = Quat().fromEuler(f/mass) * ddQuat }
  def applyAngularDamping( damp: Float ) = { angularAcceleration = angularAcceleration - angularVelocity * (damp / mass); ddQuat = dQuat.inverse.slerp(Quat(),1.f-damp) * ddQuat }	
}


object Gravity extends Vec3(0,-10,0)
