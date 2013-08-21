
package com.fishuyo
package maths
package particle




trait KinematicState {
	var position = Vec3()
	var lPosition = Vec3()
	var velocity = Vec3()
	var acceleration = Vec3()
}

trait RotationalState {

	var orientation = Quat()
	var lOrientation = Quat()

}



// Kinematic State integrators
object KinematicState {

	def verlet(k:KinematicState)(dt:Float){
    k.velocity = k.position - k.lPosition
    k.lPosition = k.position
    k.position = k.position + k.velocity + k.acceleration * ( .5f * dt * dt )

    k.acceleration = Vec3()
	}

	def euler(k:KinematicState)(dt:Float){
		k.velocity = k.velocity + k.acceleration*dt
		k.position = k.position + k.velocity*dt
	}

}

// Rotational State integrators
object RotationalState {

	def verlet(k:KinematicState)(dt:Float){
    val v = k.position - k.lPosition
    k.lPosition = k.position
    k.position = k.position + v + k.acceleration * ( .5f * dt * dt )

    k.acceleration = Vec3()
	}

	def euler(k:KinematicState)(dt:Float){
		k.velocity = k.velocity + k.acceleration*dt
		k.position = k.position + k.velocity*dt
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


object Gravity extends Vec3(0,-10,0)
