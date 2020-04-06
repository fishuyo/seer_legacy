
package com.fishuyo.seer
package world
package particle

import graphics.Animatable
import field.VecField3

import scala.collection.mutable.ListBuffer


class ParticleEmitter(var maxParticles:Int) extends Animatable {

	var ttl = 20f
	var particles = ListBuffer[Particle]()

	var field:Option[VecField3] = None
	var fieldAsForce = true
	var xt = 0f

  var damping = 0f

	def +=(p:Particle) = particles += p
	def ++=(ps:Seq[Particle]) = particles ++= ps
	def addParticle(p:Particle) = particles += p
	def clear() = particles.clear

	override def animate(dt:Float){

		val timeStep = Integrators.dt
		if(ttl > 0) particles = particles.filter( (p) => p.t < ttl )
		if( particles.length > maxParticles ) particles = particles.takeRight(maxParticles)

		val steps = ( (dt+xt) / timeStep ).toInt
    xt += dt - steps * timeStep

    for( t <- (0 until steps)){

      particles.foreach( (p) => {
        p.applyGravity()
        p.applyDamping(damping)
  			if(field.isDefined){
					if(fieldAsForce) p.applyForce(field.get(p.position))
					else p.setVelocity(field.get(p.position))
				}

        p.step() // timeStep
        // p.collideGround(-1f, 0.999999f) 
      })
    }

	}

	def setLifespan(t:Float) = ttl = t
	def setMaxParticles(i:Int) = maxParticles = i

}