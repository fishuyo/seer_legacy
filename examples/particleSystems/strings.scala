
package com.fishuyo
package examples.particleSystems.strings

import scala.collection.mutable.ListBuffer

import graphics._
import dynamic._
import maths._
import particle._
import io._

import com.badlogic.gdx.graphics._


object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  val strings = ListBuffer[String]()
  for( i<-(0 until 5)) strings += new String(Vec3(), .5f, .01f, 1.0f)

  val live = new JS("strings.js")

  SimpleAppRun()  

  override def draw(){
  	strings.foreach( _.draw() )
  }

  override def step(dt:Float){
  	strings.foreach( _.step(dt) )
    live.step(dt)
  }

}



class String( var pos:Vec3=Vec3(0), var length:Float=1.f, var dist:Float=.05f, var stiff:Float=1.f) extends GLAnimatable {

  var particles = ListBuffer[Particle]()
  var links = ListBuffer[LinearSpringConstraint]()
  var pins = ListBuffer[AbsoluteConstraint]()

  val numLinks = (length / dist).toInt

  for( i<-(0 to numLinks)){
  	val p = Particle(pos)
  	if( i > 0){
  		links += LinearSpringConstraint(p,particles(i-1),dist,stiff)
  	}
  	particles += p
  }

  pins += AbsoluteConstraint(particles(0), Vec3(pos))
  // pins += AbsoluteConstraint(particles.last, Vec3(pos))

  var vertices = new Array[Float](3*2*numLinks)
  var mesh:Mesh = null

  override def step( dt: Float ) = {

    for( s <- (0 until 5) ){ 
      links.foreach( _.solve() )
      links = links.filter( (l) => !l.isTorn )
      pins.foreach( _.solve() )
    }

    particles.foreach( (p) => {
      p.applyForce(Gravity)
      p.applyDamping(20.f)
      p.step(dt) 
    })

  }

  override def draw() {
    if( mesh == null) mesh = new Mesh(false,2*numLinks,0,VertexAttribute.Position)
    var i = 0
    var off = 0
    for( i<-(0 until links.size)){
	    // if( !links(i).isTorn ){
	      val p = links(i).p.position
	      val q = links(i).q.position
	      val v = i+off
	      vertices(6*v) = p.x
	      vertices(6*v+1) = p.y
	      vertices(6*v+2) = p.z
	      vertices(6*v+3) = q.x
	      vertices(6*v+4) = q.y
	      vertices(6*v+5) = q.z
    	// } else off -= 1
    }
    mesh.setVertices(vertices,0,links.size*6)
    mesh.render( Shader(), GL10.GL_LINES)    
  }

  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

}

