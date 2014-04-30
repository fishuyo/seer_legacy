
package com.fishuyo.seer
package examples.particleSystems.grass

import scala.collection.mutable.ListBuffer

import graphics._
import dynamic._
import maths._
import particle._
import io._

import util._

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}
import com.badlogic.gdx.Gdx


object Main extends App with Animatable{

  DesktopApp.loadLibs()
  Scene.push(this)

  val blades = ListBuffer[Grass]()
  for( i<-(0 until 5); j<-(0 until 5)) blades += new Grass( Vec3( i*0.1f+Random.float()*0.1f, 0, -j*0.1+Random.float()*0.1f), .1f+Random.float()*0.2f, 1)

  val live = new Ruby("grass.rb")

  var wind=true
  var gusting=false
  var t = 0.f
  var duration = 0.f
  var mag = 0.f

  DesktopApp()  

  override def draw(){
    Shader.lightingMix = 1.0f

    Shader.setBlend(true)
    Gdx.gl.glLineWidth(3)

    var i=0
    for( x <- (0 until 10); y <- (0 until 10)){
      MatrixStack.push()
      MatrixStack.translate(Vec3(x,0,y)*.5f)
      Shader.setMatrices()
      blades.foreach( b => {Random.seed(i); i+=1; b.draw() })
      MatrixStack.pop()
    }
  }

  override def animate(dt:Float){
    live.animate(dt)

    t += dt
    Random.seed

    // if(wind){
    //   if(t > duration){
    //     t = 0.f
    //     duration = Random.float(0.1f,0.8f)()

    //     if(Random.float() < 0.8f){
    //       gusting = true
    //       mag = Random.float(-0.001f,-0.008f)()
    //     }else{
    //       gusting = false
    //     }
    //     println( duration + " " + mag)
    //   }

    //   if(gusting){
    //     Main.blades.foreach( _.applyForce(Vec3(0.f,0.f,mag-Random.float()*.004f) ))  
    //   }
    // }
  
    blades.foreach( _.animate(dt) )

  }

}



class Grass( var pos:Vec3=Vec3(0), var length:Float=0.25f, var numLinks:Int=2) extends Animatable {

  var sticks = ListBuffer[Stick]()
  var links = ListBuffer[RotationalSpringConstraint]()
  var pins = ListBuffer[AbsoluteConstraint]()

  // val numLinks = (length / dist).toInt

  var damping = .05f

  for( i<-(0 to numLinks)){
  	val p = Stick(pos, Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  	p.length = length
    
    links += new RotationalSpringConstraint(p, Quat().fromEuler(Vec3(math.Pi/2,0,0)), .01f)

  	if( i > 0){
      pins += AbsoluteConstraint(p, sticks(i-1).end )
  	}
  	sticks += p
  }

  var vertices = new Array[Float]((3+3)*2*(numLinks+1))
  var mesh:GdxMesh = null

  override def animate( dt: Float ) = {

    for( s <- (0 until 1) ){ 
      links.foreach( _.solve() )
      pins.foreach( _.solve() )
    }

    sticks.foreach( (p) => {
      // p.applyForce(Vec3(0,-.1,0))
      p.applyAngularDamping(damping)
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

    //   sticks.foreach( (p) => {
    //     if( field != null ) p.applyForce( field(p.position) ) 
    //     p.applyGravity()
    //     p.applyDamping(20.f)
    //     p.animate(.015f) 
    //   })

    // }
  }

  override def draw() {
    if( mesh == null) mesh = new GdxMesh(false,2*(numLinks+1),0,VertexAttribute.Position, VertexAttribute.Normal) //, VertexAttribute.ColorUnpacked)
    var i = 0
    var off = 0
    val d = Random.vec3()
    val r = Random.float(-.1f,.1f)

    for( i<-(0 until sticks.size)){
	    // if( !links(i).isTorn ){
	      val p = sticks(i).position
	      val q = sticks(i).end.position
	      val v = i+off
        vertices(12*v) = p.x
        vertices(12*v+1) = p.y
        vertices(12*v+2) = p.z
        vertices(12*v+3) = d.x+r()
        vertices(12*v+4) = 0.f
        vertices(12*v+5) = d.z+r()
        vertices(12*v+6) = q.x
	      vertices(12*v+7) = q.y
	      vertices(12*v+8) = q.z
	      vertices(12*v+9) = d.x+r()
	      vertices(12*v+10) = 0.f
	      vertices(12*v+11) = d.z+r()
    	// } else off -= 1
    }
    mesh.setVertices(vertices,0,vertices.length)
    Shader.setColor( Vec3(0,.6+Random.float()*.2, Random.float()*.1), .3f)
    mesh.render( Shader(), GL10.GL_LINES)    
  }

  def applyForce( f: Vec3 ) = sticks.foreach( _.applyTorque(f) )

}

