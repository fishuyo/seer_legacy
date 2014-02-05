

package com.fishuyo.seer
package maths
package particle


 trait Constraint{
 	var active=true
 	def setActive(b:Boolean) = active = b
	def solve();
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
}

object LinearSpringConstraint {
	def apply(p:Particle,q:Particle,l:Float=1.f,s:Float=1.f,tear:Float=0.f) = new LinearSpringConstraint(p,q,l,s,tear)
}

class LinearSpringConstraint(val p:Particle, val q:Particle, var length:Float, stiffness:Float, tearThreshold:Float) extends Constraint {
	val imP = 1.f/p.mass
  val imQ = 1.f/q.mass
  val wP = ( imP / (imP+imQ) ) * stiffness;
  val wQ = ( imQ / (imP+imQ) ) * stiffness;

  var torn = false
  def isTorn() = torn

	override def solve(){
		if( torn ) return

		val d = p.position - q.position
    val dist = d.mag
    if( dist == 0.f ) return

    if( tearThreshold > 0.f && dist > tearThreshold ){
    	torn = true
    	return
    }

    val error = (length - dist) / dist

    p.position = p.position + d * wP * error
    q.position = q.position - d * wQ * error
	}
}

// object LinearRigidConstraint {
// 	def apply(p:Particle,q:Particle,l:Float=1.f) = new LinearRigidConstraint(p,q,l)
// }

// class LinearRigidConstraint(val p:Particle, val q:Particle, length:Float) extends Constraint {

// 	override def solve(){
// 		val d = p.position - q.position
//     q.position = p.position - d * length
// 	}
// }

class RotationalSpringConstraint(val p:Stick, var zeroQuat:Quat=Quat(), k:Float=0.1f) extends Constraint {

  var torn = false
  def isTorn() = torn

	override def solve(){
		if( torn ) return

		// val d = p.euler - zeroQuat.toEulerVec
  //   val dist = d.mag
  //   if( dist == 0.f ) return

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



