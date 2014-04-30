
package com.fishuyo.seer
package examples.particleSystems.strings

import scala.collection.mutable.ListBuffer

import graphics._
import dynamic._
import maths._
import particle._
import io._

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}


object Main extends App with Animatable{

  DesktopApp.loadLibs()
  Scene.push(this)
  Inputs.addProcessor(video.ScreenCaptureKey)



  val strings = ListBuffer[String]()
  for( i<-(0 until 5)) strings += new String(Vec3(), .5f, .01f, 1.0f)

  val live = new Ruby("strings.rb")

  DesktopApp()  

  override def init(){
    val compNode = new RenderNode
    compNode.shader = "composite"
    compNode.clear = false
    val quag = new Drawable {
      val m = Mesh(Primitive2D.quad)
      override def draw(){
        // Shader("composite").setUniformf("u_blend0", 1.0f)
        // Shader("composite").setUniformf("u_blend1", 1.0f)
        // Shader("composite").setUniformMatrix("u_projectionViewMatrix", new Matrix4())
        m.draw()
      }
    }
    compNode.scene.push( quag )
    SceneGraph.root.outputTo(compNode)
    compNode.outputTo(compNode)
    compNode.outputTo(ScreenNode)
  }
  override def draw(){
    Shader.lightingMix = 0.f
  	strings.foreach( _.draw() )
  }

  override def animate(dt:Float){
  	// strings.foreach( _.animate(dt) )
    live.animate(dt)
  }

}



class String( var pos:Vec3=Vec3(0), var length:Float=1.f, var dist:Float=.05f, var stiff:Float=1.f) extends Animatable {

  var particles = ListBuffer[Particle]()
  var links = ListBuffer[LinearSpringConstraint]()
  var pins = ListBuffer[AbsoluteConstraint]()

  val numLinks = (length / dist).toInt

  var damping = 20.f

  for( i<-(0 to numLinks)){
  	val p = Particle(pos)
  	if( i > 0){
  		links += LinearSpringConstraint(p,particles(i-1),dist,stiff)
  	}
  	particles += p
  }

  pins += AbsoluteConstraint(particles(0), Vec3(pos))
  pins += AbsoluteConstraint(particles.last, Vec3(pos))

  var vertices = new Array[Float](3*2*numLinks)
  var mesh:GdxMesh = null

  override def animate( dt: Float ) = {

    for( s <- (0 until 5) ){ 
      links.foreach( _.solve() )
      links = links.filter( (l) => !l.isTorn )
      pins.foreach( _.solve() )
    }

    particles.foreach( (p) => {
      // p.applyForce(Gravity)
      p.applyDamping(damping)
      p.step() 
    })


    // val ts = .015
    // val animates = ( (dt+xt) / ts ).toInt
    // xt += dt - animates * ts

    // for( t <- (0 until animates)){
    //   for( s <- (0 until 3) ){ 
    //     links.foreach( _.solve() )
    //     pins.foreach( _.solve() )
    //   }

    //   particles.foreach( (p) => {
    //     if( field != null ) p.applyForce( field(p.position) ) 
    //     p.applyGravity()
    //     p.applyDamping(20.f)
    //     p.animate(.015f) 
    //   })

    // }
  }

  override def draw() {
    Shader.setColor(RGBA(0,1,0,1))
    if( mesh == null) mesh = new GdxMesh(false,2*numLinks,0,VertexAttribute.Position)
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
    mesh.render( Shader(), GL20.GL_LINES)    
  }

  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

}

