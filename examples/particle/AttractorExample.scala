
package com.fishuyo.seer
package examples.particle

import graphics._
import spatial._
import util._
import io._
import particle._

import rx._
import concurrent.duration._

import org.lwjgl.opengl.GL11


object AttractorExample extends SeerApp { 

  Gravity.set(0,0,0)

  val attractor = new Attractor
  val repulsor = new Attractor
  repulsor.strength = -0.1f

  val emitter = new ParticleEmitter(100000) {
    val mesh = Mesh()
    mesh.primitive = Points
    val s = Sphere().scale(0.005)
    override def draw(){
      mesh.clear
      particles.foreach { case p =>
        mesh.vertices += p.position
        // s.pose.pos = p.position
        // s.draw
      }
      mesh.update
      mesh.draw
    }
  }

  // val n = 20
  // val field = new VecField3D(n,Vec3(0),5f)
  // var updateField = false
  // emitter.field = Some(field)
  // emitter.fieldAsForce = true
  // randomizeField()

  emitter.ttl = -100f
  emitter.damping = 10f

  // def randomizeField(){
  //   for( z<-(0 until n); y<-(0 until n); x<-(0 until n)){
  //     val cen = field.centerOfBin(x,y,z).normalize
  //     // field.set(x,y,z,Vec3(0))
  //     if(emitter.fieldAsForce) field.set(x,y,z, Random.vec3())
  //     else field.set(x,y,z, Random.vec3()*0.01)
  //   }
  // }

  // add 50000 particles
  (0 until 50000).foreach{ case i => emitter += Particle(Random.vec3())}

  // add a particle every 10ms
  // Schedule.every(10 millis){
    // emitter += Particle(Random.vec3())
    // emitter += Particle(repulsor.position + Random.vec3()*0.1) //Vec3(Random.float(),2,0))
  // }

  // change motion blur feedback constants with mouse while holding b key
  var fbnode:FeedbackNode = _
  val xy = Mouse.xy.map( _*10 - Vec2(5) )
  Mouse.xy.trigger{
    val v = Mouse.xy.now
    if(Keyboard.down.now == 'b') fbnode.setBlend(v.x, v.y)
    else if(Keyboard.down.now == 'r') repulsor.position = Vec3(xy.now)
    else attractor.position = Vec3(xy.now)
  }

  override def init(){
    // com.badlogic.gdx.Gdx.gl.glEnable( 0x8642 ); // enable gl_PointSize ???

    // add motion blur node
    fbnode = new FeedbackNode(0.5,0.5)
    // fbnode = new FeedbackNode(0.98,0.025)
    RenderGraph.reset
    RootNode.outputTo(fbnode)
    fbnode.outputTo(new ScreenNode())
  }

  override def draw(){
    com.badlogic.gdx.Gdx.gl.glEnable( 0x8642 ); // enable gl_PointSize ???
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    GL11.glEnable(GL11.GL_POINT_SMOOTH);
    FPS.print
    emitter.draw()
  }

  override def animate(dt:Float){
    attractor(emitter.particles)
    // repulsor(emitter.particles)
    emitter.animate(dt)
  }
}