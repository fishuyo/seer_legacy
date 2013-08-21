

package com.fishuyo
package maths
package particle


 trait Constraint{
 	var active=true
 	def setActive(b:Boolean) = active = b
	def solve();
}

object AbsoluteConstraint{
	def apply(p:Particle, pos:Vec3) = new AbsoluteConstraint(p,pos)
}
class AbsoluteConstraint(val p:Particle, var position:Vec3) extends Constraint {
	override def solve(){
		if( !active ) return
		p.position = position
		p.lPosition = position
		p.velocity = Vec3(0)
		p.acceleration = Vec3(0)
	}
}

object LinearSpringConstraint {
	def apply(p:Particle,q:Particle,l:Float=1.f,s:Float=1.f,tear:Float=0.f) = new LinearSpringConstraint(p,q,l,s,tear)
}

class LinearSpringConstraint(val p:Particle, val q:Particle, length:Float, stiffness:Float, tearThreshold:Float) extends Constraint {
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



