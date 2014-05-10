
package com.fishuyo.seer
package maths
package particle

import graphics._

import scala.collection.mutable.ArrayBuffer


class SpringMesh(val mesh:Mesh, val stiff:Float) extends Animatable {

	var timeStep = .015f
  var damping = 20.f 
  var particles = ArrayBuffer[Particle]()
  var springs = ArrayBuffer[LinearSpringConstraint]()
  var pins = ArrayBuffer[AbsoluteConstraint]()
  var xt = 0.f

  var updateNormals = true

  if( mesh.indices.length > 0){

  	mesh.vertices.foreach( (v) => { 
  		particles += Particle( v )
  	})

  	val l = mesh.primitive match {
	  	case Triangles => mesh.indices.grouped(3).map(_.combinations(2)).flatten
	  	case Lines => mesh.wireIndices.grouped(2)
	  	case Points => List()
	  	case _ => mesh.indices.grouped(2)
	  }
	  
	  l.foreach( (xs) => {
	  		val p = particles(xs(0))
	  		val q = particles(xs(1))
	  		springs += LinearSpringConstraint(p, q, (p.position-q.position).mag, stiff)		
	  })

  } else {

  	 val l = mesh.primitive match {
	  	case Triangles => mesh.vertices.grouped(3).map(_.combinations(2)).flatten
	  	case Lines => mesh.vertices.grouped(2)
	  	case _ => mesh.vertices.grouped(2)
	  }
	  
	  l.foreach( (xs) => {
	  		val p = Particle(xs(0))
	  		val q = Particle(xs(1))
	  		particles ++= p :: q :: List()
	  		springs += LinearSpringConstraint(p, q, (p.position-q.position).mag, stiff)		
	  })
  }

  // for( i <- ( 0 until particles.length / 2)){
  	// pins += AbsoluteConstraint(particles(i), particles(i).position)
	// }

  def +=(p:Particle){
    particles += p
    mesh.vertices += p.position
  }

  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

  def averageVelocity() = (particles.map(_.velocity).sum / particles.length).mag()
  def averageSpringLength() = springs.map( (s) => s.dist / s.length).sum / springs.length
  def averageSpringError() = springs.map(_.error).sum / springs.length

  override def init(){
  	mesh.init()
  }

  override def draw(){
  	mesh.draw()
  }

  override def animate(dt:Float){

    Integrators.setTimeStep(timeStep)

    val steps = ( (dt+xt) / timeStep ).toInt
    xt += dt - steps * timeStep

    for( t <- (0 until steps)){
      for( s <- (0 until 3) ){ 
        springs.foreach( _.solve() )
        pins.foreach( _.solve() )
      }

      particles.foreach( (p) => {
        p.applyGravity()
        p.applyDamping(damping)
        p.step() // timeStep
        p.collideGround(-1.f, 0.999999f) 
      })

    }

    // update vertices from particles
    particles.zipWithIndex.foreach( (p) => {
    	mesh.vertices(p._2) = p._1.position
    })
    if(updateNormals) mesh.recalculateNormals
    mesh.update
  }
}