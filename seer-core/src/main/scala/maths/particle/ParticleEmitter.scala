
package com.fishuyo

package maths
package particle

import graphics.GLAnimatable

import scala.collection.mutable.ListBuffer

class ParticleEmitter(var maxParticles:Int) extends GLAnimatable {

	var ttl = 10.f
	var particles = ListBuffer[Particle]()

	def addParticle(p:Particle) = particles += p

	override def step(dt:Float){

		particles = particles.filter( (p) => p.t < ttl )
		if( particles.length > maxParticles ) particles = particles.takeRight(maxParticles)
		particles.foreach( (p) => {
			// p.applyForce(Gravity)
			p.step(dt)
		})

	}

	def setLifespan(t:Float) = ttl = t
	def setMaxParticles(i:Int) = maxParticles = i

}