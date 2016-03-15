


package com.fishuyo.seer
package examples.particle

import graphics._
import spatial._
import util._
import io._
import particle._

import rx._
import concurrent.duration._

object ParticleSystemExample extends SeerApp { 

  Gravity.set(0,-0.01,0)

  val emitter = new ParticleEmitter(10000) {
    // val mesh = Mesh()
    // mesh.primitive = Points
    val s = Sphere().scale(0.005)
    override def draw(){
      particles.foreach { case p =>
        s.pose.pos = p.position
        s.draw
      }
    }
  }

  val n = 20
  val field = new VecField3D(n,Vec3(0),5f)
  var updateField = false
  emitter.ttl = 100f
  emitter.damping = 100f
  emitter.field = Some(field)
  emitter.fieldAsForce = true
  randomizeField()

  def randomizeField(){
    for( z<-(0 until n); y<-(0 until n); x<-(0 until n)){
      val cen = field.centerOfBin(x,y,z).normalize
      // field.set(x,y,z,Vec3(0))
      if(emitter.fieldAsForce) field.set(x,y,z, Random.vec3())
      else field.set(x,y,z, Random.vec3()*0.01)
      //field.set(x,y,z, cen * -.1f)
      //field.set(x,y,z, Vec3(x,y,z).normalize * .1f)
      //field.set(x,y,z, Vec3( -cen.z + -cen.x*.1f, -cen.y, cen.x ).normalize * .1f )
      //field.set(x,y,z, Vec3( math.sin(cen.x*3.14f), 0, math.cos(cen.z*3.14f) ).normalize * .1f)  
      //field.set(x,y,z, Vec3( cen.x, y/10.0f, cen.z).normalize * .1f )
      //field.set(x,y,z, Vec3(0,.1f,0) )
      //field.set(x,y,z, (Vec3(0,1,0)-cen).normalize * .1f )
    }
  }

  // add a particle every 10ms
  Schedule.every(10 millis){
    emitter += Particle(Random.vec3()) //Vec3(Random.float(),2,0))
  }

  // change motion blur feedback constants with mouse while holding b key
  var fbnode:FeedbackNode = _
  Mouse.xy.trigger{
    val v = Mouse.xy.now
    if(Keyboard.down.now == 'b') fbnode.setBlend(v.x, v.y)
  }

  override def init(){
    // add motion blur node
    fbnode = new FeedbackNode(0.98,0.025)
    RenderGraph.reset
    RootNode.outputTo(fbnode)
    fbnode.outputTo(new ScreenNode())
  }

  override def draw(){
    emitter.draw()
  }

  override def animate(dt:Float){
    emitter.animate(dt)
  }
}