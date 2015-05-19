
package com.fishuyo.seer
package particle

import spatial._
import graphics._

import collection.mutable.ListBuffer

class SpringString(val pos:Vec3, val numSegments:Int=4, val dist:Float=0.1, val stiff:Float=1.0 ) extends Animatable{

  var damping = 20f
  
  val particles = ListBuffer[Particle]()
  val springs = ListBuffer[LinearSpringConstraint]()
  val pins = ListBuffer[AbsoluteConstraint]()

  particles += Particle(pos)
  pins += AbsoluteConstraint(particles.last, pos)

  for( i <- 0 until numSegments){
    val p = Particle(Vec3())
    val s = LinearSpringConstraint(p, particles.last, dist, stiff)
    particles += p 
    springs += s 
  }

  override def animate(dt:Float){
    for( s <- (0 until 3) ){ 
      springs.foreach( _.solve() )
      pins.foreach( _.solve() )
    }

    particles.foreach( (p) => {
      p.applyGravity()
      p.applyDamping(damping)
      p.step() // timeStep
      p.collideGround(-1f, 0.999999f) 
    })
  }

}