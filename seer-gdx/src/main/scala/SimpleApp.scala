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
  var a0 = 1.f
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
  var camera = Camera //new PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)
  var input = Inputs //new InputMultiplexer
  var navInput = new KeyboardNavInput(Camera.nav)
  // var audio = Audio

  var frameCount = 0
  // val fps = new FPSLogger
  // var logfps = false

  var dtAccum = 0.f
  var paused = false

  def create(){

    Gdx.input.setInputProcessor( input )
    input.addProcessor( navInput )

    camera.nav.pos.z = 2.f

    Shader.load("basic", DefaultShaders.basic._1, DefaultShaders.basic._2)
    Shader.load("texture", DefaultShaders.texture._1, DefaultShaders.texture._2)
    Shader.load("composite", DefaultShaders.composite._1, DefaultShaders.composite._2)
    Shader.load("text", DefaultShaders.text._1, DefaultShaders.text._2)

    // Shader.load("firstPass", DefaultShaders.firstPass._1, DefaultShaders.firstPass._2)
    // Shader.load("secondPass", DefaultShaders.secondPass._1, DefaultShaders.secondPass._2)

    scene.init()

    // if(audio.sources.length > 0) audio.start
  }

  def render(){
    if(paused) return
    
    // if( logfps ) fps.log
    val timeStep = 1.f/60.f
    dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep ){
      SceneGraph.animate(timeStep)
      dtAccum -= timeStep
      frameCount +=1 
    }
    
    Shader.update
    
    Gdx.gl.glClearColor(Shader.bg.r,Shader.bg.g,Shader.bg.b,Shader.bg.a)
    Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

    if( Shader.blend ){ 
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
    }else {
      Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
    }
    //Gdx.gl.glEnable(GL20.GL_LINE_SMOOTH);

    // Gdx.gl.glEnable(GL20.GL_CULL_FACE);
    // Gdx.gl.glCullFace(GL20.GL_BACK);
    
    // Gdx.gl.glDepthFunc(GL20.GL_LESS);
    // Gdx.gl.glDepthMask(true);

    SceneGraph.render() //renderNodes.foreach( _.render )

  }

  def resize(width: Int, height:Int){
    SceneGraph.resize(Viewport(width,height))
    this.width = width
    this.height = height
    aspect = width * 1.f / height
    // camera.viewportWidth = aspect
  }
  def pause(){
    //audio ! Stop
    // paused = true
  }
  def resume(){
    //audio ! Play
    // paused = false
  }
  def dispose(){
    println("Dispose called.")
    // audio.stop
    Thread.sleep(100)
    // audio.main ! "dispose"
    //Kinect.disconnect()
  }

}

