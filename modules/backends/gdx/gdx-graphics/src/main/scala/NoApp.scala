package seer

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


class NoAppListener extends ApplicationListener {

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

    Keyboard.bindCamera()

    // camera.nav.pos.z = 2f

    // RenderGraph.reset
    // RootNode.renderer.scene.init()
  }

  def render(){
    // if(paused) return
    
    // val dt = Gdx.graphics.getDeltaTime()
    // Time.elapsedTime += dt
    // if(fixedTimeStep){
    //   dtAccum += dt
    //   while( dtAccum > timeStep ){
    //     // Renderer().animate(timeStep)
    //     RenderGraph.animate(timeStep)
    //     dtAccum -= timeStep
    //     frameCount += 1 
    //   }
    // } else {
    //   RenderGraph.animate(dt)
    //   frameCount += 1
    // }
    
    // RenderGraph.render()

  }

  def resize(width: Int, height:Int){
    println(s"App resize: $width $height")
    Gdx.gl.glViewport(0, 0, width, height)

    // Renderer().resize(Viewport(width,height))
    // RenderGraph.resize(Viewport(width,height))
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


