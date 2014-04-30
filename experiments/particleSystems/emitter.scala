
package com.fishuyo.seer
package examples.particleSystems.emitter

import graphics._
import dynamic._
import maths._
import particle._

import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20


import scala.collection.mutable.ListBuffer

object Main extends App with Animatable{

  DesktopApp.loadLibs()
  Scene.push(this)

  val live = new JS("emitter.js")

  val emitter = new MyEmitter(100)

  DesktopApp()  

  override def init(){
    // Shader.load("basic", Gdx.files.internal("res/t.vert"), Gdx.files.internal("res/t.frag"))
    // Shader.monitor("basic")
  }

  override def draw(){
  	emitter.draw()
  }

  override def animate(dt:Float){
  	emitter.animate(dt)
    live.animate(dt)
  }

}


class MyEmitter(var maxParticles:Int) extends Animatable {

  var ttl = 10.f
  var particles = ListBuffer[Stick]()

  var origin = Vec3()
  var direction = Vec3(-1, 1, 0).normalize
  var spin = Vec3(0, 1, 0).normalize
  var spread = 0.f//.02f
  var velocity = .1f

  var randVel = Random.vec3.map( v => (v * spread + direction) * velocity)
  var randAngVel = Random.vec3.map( v => (v * 0.f + spin) * velocity)


  def addParticle(p:Stick) = particles += p

  override def animate(dt:Float){

    val p = Stick(origin, randVel(), Quat(), randAngVel()  )
    addParticle(p)

    particles = particles.filter( (p) => p.t < ttl )
    if( particles.length > maxParticles ) particles = particles.takeRight(maxParticles)
    particles.foreach( (p) => {
      p.applyForce(Gravity)
      p.step()
    })

  }

  override def draw(){

    particles.foreach( (p) => {
      MatrixStack.push()
      MatrixStack.translate(p.position)
      MatrixStack.rotate(p.orientation)
      // MatrixStack.rotate(Quat().fromEuler(p.euler))
      MatrixStack.scale(Vec3(.01f,.01f,.2f))

      val c = 1.f - (p.t / ttl)
      Shader.setColor(RGBA(c,.6f,.6f,1.f))
      Shader.setMatrices()
      Cube().draw()
      // Sphere().draw()
      MatrixStack.pop()
    })
  }

  def setLifespan(t:Float) = ttl = t
  def setMaxParticles(i:Int) = maxParticles = i

}






