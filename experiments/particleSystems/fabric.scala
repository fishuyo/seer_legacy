
package com.fishuyo.seer
package examples.particleSystems.verletFabric

import graphics._
import dynamic._
import maths._
import particle._
import objects._

object Main extends App with Animatable{

  SimpleAppRun.loadLibs()
  Scene.push(this)

  val live = new JS("fabric.js")

  val fabric = new Fabric()


  SimpleAppRun()  

  override def draw(){
    Shader.lightingMix = 0.f
  	fabric.draw()
  }

  override def animate(dt:Float){
  	fabric.animate(dt)
    live.animate(dt)
  }

}





