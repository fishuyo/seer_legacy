

package com.fishuyo.seer
package world
package particle

import spatial._

 trait Constraint{
 	var active=true
 	def setActive(b:Boolean) = active = b
	def solve()
	def p:KinematicState
	def q:KinematicState
}

object AbsoluteConstraint{
	def apply(p:KinematicState, pos:Vec3) = new AbsoluteConstraint(p, Particle(pos))
	def apply(p:KinematicState, q:KinematicState) = new AbsoluteConstraint(p,q)
}
class AbsoluteConstraint(val p:KinematicState, var q:KinematicState) extends Constraint {
	override def solve(){
		if( !active ) return
		p.position = q.position
		p.lPosition = q.lPosition
		p.velocity = q.velocity
		p.acceleration = q.acceleration
	}

	def set(pos:Vec3){ set(Particle(pos))}
	def set(k:KinematicState){ q = k }
}

object LinearSpringConstraint {
  def apply(p:Particle,q:Particle,s:Float) = {
    val dist = (p.position - q.position).mag
    new LinearSpringConstraint(p,q,dist,s,0f)
  }
	def apply(p:Particle,q:Particle,l:Float,s:Float) = {
    new LinearSpringConstraint(p,q,l,s,0f)
  }
}

class LinearSpringConstraint(val p:Particle, val q:Particle, var length:Float, stiffness:Float, tearThreshold:Float) extends Constraint {
	var imP = 1f/p.mass
  var imQ = 1f/q.mass
  var wP = ( imP / (imP+imQ) ) * stiffness;
  var wQ = ( imQ / (imP+imQ) ) * stiffness;

  var dist = 0f
  var error = 0f
  var torn = false

  def isTorn() = torn
  def length(v:Float){ length = v }

  def updateWeights(){
		imP = 1f/p.mass
		imQ = 1f/q.mass
		wP = ( imP / (imP+imQ) ) * stiffness;
		wQ = ( imQ / (imP+imQ) ) * stiffness;
  }

	override def solve(){
		if( torn ) return

		val d = p.position - q.position
    dist = d.mag
    if( dist == 0f ) return

    if( tearThreshold > 0f && dist > tearThreshold ){
    	torn = true
    	return
    }

    error = (length - dist) / dist

    p.position = p.position + d * wP * error
    q.position = q.position - d * wQ * error
	}
}

// object LinearRigidConstraint {
// 	def apply(p:Particle,q:Particle,l:Float=1f) = new LinearRigidConstraint(p,q,l)
// }

// class LinearRigidConstraint(val p:Particle, val q:Particle, length:Float) extends Constraint {

// 	override def solve(){
// 		val d = p.position - q.position
//     q.position = p.position - d * length
// 	}
// }

class RotationalSpringConstraint(val p:Stick, var zeroQuat:Quat=Quat(), k:Float=0.1f) extends Constraint {
	val q  = Particle(Vec3())
  var torn = false
  def isTorn() = torn

	override def solve(){
		if( torn ) return

		// val d = p.euler - zeroQuat.toEulerVec
  //   val dist = d.mag
  //   if( dist == 0f ) return

  //   p.euler = p.euler + d * k

    p.orientation = p.orientation.slerp(zeroQuat, k)
		
		// val quat = Quat().fromEuler(p.euler)
		// val pos = p.position + quat.toZ()*p.length

  	// q.position = pos
  	// zeroQuat = quat * relativeQuat
		// q.lEuler.set(q.euler)
  	// q.euler = (zeroQuat * Quat().fromEuler(q.euler)).toEulerVec

	}
}



