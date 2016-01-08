package com.fishuyo.seer

import graphics._
import io._

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.FPSLogger

object Window {
  def width = Gdx.graphics.getWidth()
  def height = Gdx.graphics.getHeight()
  var w0 = 800
  var h0 = 800
  var a0 = 1f
}

object FPS {
  val fps = new FPSLogger
  def print() = fps.log
}

object Time {
  var elapsedTime = 0.0
  def apply() = elapsedTime
}

class SeerAppListener extends ApplicationListener {

  // var width = Window.w0
  // var height = Window.h0
  // var aspect = Window.a0

  // var scene = Scene
  // var camera = Camera 
  // var input = Inputs
  
  var frameCount = 0

  var fixedTimeStep = false
  var timeStep = 1/60f
  var dtAccum = 0f

  // var paused = false

  def create(){
    // println("App create.")

    Gdx.input.setInputProcessor( Inputs )

    Keyboard.bindCamera()

    Camera.nav.pos.z = 2f

    RenderGraph.reset
    RootNode.renderer.scene.init()
  }

  def render(){
    // if(paused) return
    
    val dt = Gdx.graphics.getDeltaTime()
    Time.elapsedTime += dt
    if(fixedTimeStep){
      dtAccum += dt
      while( dtAccum > timeStep ){
        RenderGraph.animate(timeStep)
        dtAccum -= timeStep
        frameCount += 1 
      }
    } else {
      RenderGraph.animate(dt)
      frameCount += 1
    }
    
    RenderGraph.render()
  }

  def resize(width: Int, height:Int){
    println(s"App resize: $width $height")
    // Gdx.gl.glViewport(0, 0, width, height)

    RenderGraph.resize(Viewport(width,height))
    // this.width = width
    // this.height = height
    // aspect = width * 1f / height
    // camera.viewportWidth = aspect
  }
  def pause(){
    println("App pause.")
  }
  def resume(){
    println("App resume.")
  }
  def dispose(){
    println("App dispose.")
  }

}


