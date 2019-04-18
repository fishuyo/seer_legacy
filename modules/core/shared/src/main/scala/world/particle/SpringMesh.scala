
package com.fishuyo.seer
package world
package particle

import graphics._
import spatial.Vec3

import scala.collection.mutable.ArrayBuffer


class SpringMesh(val mesh:MeshLike, val stiff:Float=1f, val tear:Float = 0f) extends Animatable {

	var timeStep = .015f
  var damping = 20f 
  var particles = ArrayBuffer[Particle]()
  var springs = ArrayBuffer[LinearSpringConstraint]()
  var pins = ArrayBuffer[AbsoluteConstraint]()
  var xt = 0f

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
        val dist = (p.position-q.position).mag
	  		springs += LinearSpringConstraint(p, q, dist, stiff)		
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
        val dist = (p.position-q.position).mag
	  		springs += LinearSpringConstraint(p, q, dist, stiff)		
	  })
  }

 //  val center = Particle(particles.map(_.position).sum / particles.length)
 //  for(p <- particles){ 
 //  	springs += LinearSpringConstraint(p, center, 0.05f)
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
        // p.collideGround(-1f, 0.999999f) 
      })

    }

    // update vertices from particles
    particles.zipWithIndex.foreach( (p) => {
    	mesh.vertices(p._2) = p._1.position
    })

    // update indices if torn
    if(mesh.primitive == Lines){
      val ts = springs.zipWithIndex.filter(_._1.torn).flatMap{ case (s,i) => Seq(2*i,2*i+1)}
      springs = springs.filterNot(_.torn)
      ts.reverseMap { case i => 
        mesh.wireIndices.remove(i)
      }
    }

    if(updateNormals) mesh.recalculateNormals
    mesh.update
  }
}