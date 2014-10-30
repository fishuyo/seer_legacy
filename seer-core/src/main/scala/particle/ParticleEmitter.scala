
package com.fishuyo.seer
package particle

import graphics.Animatable

import scala.collection.mutable.ListBuffer


class ParticleEmitter(var maxParticles:Int) extends Animatable {

	var ttl = 10f
	var particles = ListBuffer[Particle]()

	def addParticle(p:Particle) = particles += p

	override def animate(dt:Float){

		particles = particles.filter( (p) => p.t < ttl )
		if( particles.length > maxParticles ) particles = particles.takeRight(maxParticles)
		particles.foreach( (p) => {
			// p.applyForce(Gravity)
			p.step()
		})

	}

	def setLifespan(t:Float) = ttl = t
	def setMaxParticles(i:Int) = maxParticles = i

}