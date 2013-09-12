
package com.fishuyo
package maths
package particle

import spatial._


trait KinematicState {
	var position = Vec3()
	var lPosition = Vec3()
	var velocity = Vec3()
	var acceleration = Vec3()
}

trait RotationalState {

	var pose = Pose()
	var lPose = Pose()
  var restPose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  var relQuat = Quat()

  var angularAcceleration = Vec3(0)
  var angularVelocity = Vec3(0)
  var euler = Vec3(0.f,0.f,0.f)
  var lEuler = Vec3(0.f,0.f,0.f)

}



// Kinematic State integrators
object KinematicState {

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

}

// Rotational State integrators
object RotationalState {

	def verlet(k:RotationalState)(dt:Float){
    k.angularVelocity =  k.euler - k.lEuler  	
  	k.lEuler = k.euler
  	k.euler = k.euler + k.angularVelocity + k.angularAcceleration * (.5f*dt*dt)

  	// move to particle / constraint
  	// dw -= w * (Trees.damp / mass) //damping
  	// dw -= euler * k //restoring spring force
  	// pose.quat = restPose.quat * Quat().fromEuler(euler.x,euler.y,euler.z)

  	k.angularAcceleration = Vec3(0)
	}

	def euler(k:RotationalState)(dt:Float){
		k.angularVelocity = k.angularVelocity + k.angularAcceleration*dt
		k.lEuler = k.euler
		k.euler = k.euler + k.angularVelocity*dt
  	k.angularAcceleration = Vec3(0)
	}

}


object Particle {
	def apply(pos:Vec3) = { val p = new Particle; p.position = pos; p }
	def apply(pos:Vec3, vel:Vec3) = { val p = new Particle; p.position = pos; p.velocity = vel; p.lPosition = pos - vel; p }
}

class Particle() extends KinematicState {

	var mass = 1.f
	var t = 0.f
	var integrate = KinematicState.verlet(this)_

	def step(dt:Float){
		t += dt
		integrate(dt)
	}

  def applyForce( f: Vec3 ) = acceleration = acceleration + (f / mass)
  def applyGravity() = acceleration = acceleration + Gravity
  def applyDamping( damp: Float ) = acceleration = acceleration - velocity * (damp / mass)

}

class Stick extends KinematicState with RotationalState {
	var mass = 1.f
	var length = 1.f
	
}


object Gravity extends Vec3(0,-10,0)
