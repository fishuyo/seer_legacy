
package com.fishuyo
package examples.particleSystems.verletFabric

import graphics._
import dynamic._
import maths._
import particle._
import objects._

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  val live = new JS("fabric.js")

  val fabric = new Fabric()


  SimpleAppRun()  

  override def draw(){
    Shader.lighting = 0.f
  	fabric.draw()
  }

  override def step(dt:Float){
  	fabric.step(dt)
    live.step(dt)
  }

}





