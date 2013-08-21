
package com.fishuyo
package examples.particleSystems.emitter

import graphics._
import dynamic._
import maths._
import particle._

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  val live = new JS("emitter.js")

  val emitter = new MyEmitter(100)

  val generateRandVel = util.RandVec3(Vec3(-.1),Vec3(.1))

  SimpleAppRun()  

  override def draw(){
  	emitter.draw()
  }

  override def step(dt:Float){

  	val p = Particle(Vec3(), generateRandVel() )
  	emitter.addParticle(p)
  	emitter.step(dt)
    live.step(dt)
  }

}

class MyEmitter(n:Int) extends ParticleEmitter(n) {

	override def draw(){
		particles.foreach( (p) => {
			MatrixStack.push()
			MatrixStack.translate(p.position)
			MatrixStack.scale(Vec3(.1f))

			val c = 1.f - (p.t / ttl)
			Shader.setColor(RGBA(c,c,c,1.f))
			Shader.setMatrices()
			Cube().draw()

			MatrixStack.pop()
		})
	}


}





