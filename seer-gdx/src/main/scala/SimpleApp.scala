package com.fishuyo.seer

import graphics._
import io._
// import audio._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.glutils.{FrameBuffer=>GdxFrameBuffer}
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Mesh

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

class SeerAppListener extends ApplicationListener {

  var width = Window.w0
  var height = Window.h0
  var aspect = Window.a0

  var scene = Scene
  var camera = Camera //new PerspectiveCamera(67f, SimpleAppSize.aspect, 1f)
  var input = Inputs //new InputMultiplexer
  // var navInput = new KeyboardNavInput(Camera.nav)
  // var audio = Audio

  var frameCount = 0
  // val fps = new FPSLogger
  // var logfps = false

  var fixedTimeStep = false
  var timeStep = 1/60f
  var dtAccum = 0f
  var paused = false

  def create(){
    println("App create.")

    Gdx.input.setInputProcessor( input )
    // input.addProcessor( navInput )

    camera.nav.pos.z = 2f

    val basic = Shader.load(DefaultShaders.basic)
    // val basic = Shader.load("shaders/test")
    // basic.monitor
    
    val root = new RenderNode
    root.renderer.scene = Scene
    root.renderer.camera = Camera 
    root.renderer.shader = basic
    RenderGraph.addNode(root)
    // val r = new Renderer
    // r.scene = Scene
    // r.camera = Camera 
    // r.shader = basic
    // Renderer() = r

    // Scene.init()

    // Shader.load("basic", DefaultShaders.basic._1, DefaultShaders.basic._2)
    // Shader.load("texture", DefaultShaders.texture._1, DefaultShaders.texture._2)
    // Shader.load("composite", DefaultShaders.composite._1, DefaultShaders.composite._2)
    // Shader.load("text", DefaultShaders.text._1, DefaultShaders.text._2)

    // Shader.load("firstPass", DefaultShaders.firstPass._1, DefaultShaders.firstPass._2)
    // Shader.load("secondPass", DefaultShaders.secondPass._1, DefaultShaders.secondPass._2)
  }

  def render(){
    if(paused) return
    
    // if( logfps ) fps.log
    val dt = Gdx.graphics.getDeltaTime()
    if(fixedTimeStep){
      dtAccum += dt
      while( dtAccum > timeStep ){
        // Renderer().animate(timeStep)
        RenderGraph.animate(timeStep)
        dtAccum -= timeStep
        frameCount += 1 
      }
    } else {
      RenderGraph.animate(dt)
      frameCount += 1
    }
    
    RenderGraph.render()
    // Renderer().render()

  }

  def resize(width: Int, height:Int){
    println(s"App resize: $width $height")
    Gdx.gl.glViewport(0, 0, width, height)

    // Renderer().resize(Viewport(width,height))
    RenderGraph.resize(Viewport(width,height))
    this.width = width
    this.height = height
    aspect = width * 1f / height
    // camera.viewportWidth = aspect
  }
  def pause(){
    println("App pause.")

    //audio ! Stop
    // paused = true
  }
  def resume(){
    println("App resume.")
    //audio ! Play
    // paused = false
  }
  def dispose(){
    println("App dispose.")
    // audio.stop
    Thread.sleep(100)
    Gdx.app.exit
    // audio.main ! "dispose"
  }

}


