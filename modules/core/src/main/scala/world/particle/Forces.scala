
package seer
package world
package particle


class Attractor extends Particle {
  var radius = 0.0f
  var strength = 0.4f
  def apply(particles:Seq[Particle]){
    particles.foreach{ case p =>

      val dir = position - p.position
      val r2 = dir.mag //Sq

      if( r2 > radius){
        val f = strength * mass*p.mass / r2 
        p.applyForce( dir.normalized * f )
      }
    }
  }
}

class Repulsor extends Attractor { strength = -0.5f }