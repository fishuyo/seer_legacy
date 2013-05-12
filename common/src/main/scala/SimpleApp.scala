package com.fishuyo

import graphics._
import io._
import audio._

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.graphics.Pixmap

import scala.actors.Actor
import scala.actors.Actor._

object SimpleAppSize {
  val width = 800
  val height = 800
  val aspect = width * 1.f / height
}


class SimpleAppListener extends ApplicationListener {

  var width = SimpleAppSize.width
  var height = SimpleAppSize.height
  var aspect = SimpleAppSize.aspect

  var scene = GLScene
  var camera = Camera //new PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)
  var input = Inputs //new InputMultiplexer
  var navInput = new KeyboardNavInput(Camera.nav)
  var audio = Audio

  var fb:FrameBuffer = null
  var quad:GLPrimitive = null
  // val t1 = new Texture(width, height, format);
  // val t2 = new Texture(width, height, format);

  val fps = new FPSLogger
  var logfps = true
  def setLogFPS(b:Boolean) = logfps = b

  var dtAccum = 0.f

  def create(){

    Gdx.input.setInputProcessor( input )
    input.addProcessor( navInput )

    camera.nav.pos.z = 2.f 
    Gdx.gl.glClearColor(0,0,0,0)

    val path = "res/shaders/"


    Shader(path+"simple.vert", path+"simple.frag")
    Shader(path+"firstPass.vert", path+"firstPass.frag")
    Shader(path+"secondPass.vert", path+"secondPass.frag")
    Shader(1)
    Shader.monitor(1)

    // t1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t1.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
    // t2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t2.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);

    scene.init()

    audio.start

  }
  def render(){
    if(fb == null){
      //fb = new FrameBuffer(Pixmap.Format.RGBA4444, width, height, true)
      //quad = GLPrimitive.quad
    }

    if( logfps ) fps.log
    val timeStep = 1.f/30.f
    dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep ){
      scene.step(timeStep)
      camera.step(timeStep)
      dtAccum -= timeStep
    }
    
    Gdx.gl.glClearColor(Shader.bg._1,Shader.bg._2,Shader.bg._3,Shader.bg._4)
    Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )

    Shader.update

    //fb.begin
    //fb.getColorBufferTexture.bind(0)
    //Gdx.gl.glClearColor(1,1,1,0);
    //Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)

    Shader().begin
      // Shader().setUniformMatrix("u_projectionViewMatrix", camera.combined)
      // Shader().setUniformMatrix("u_modelViewMatrix", camera.view)
      // Shader().setUniformMatrix("u_normalMatrix", camera.view.toNormalMatrix())
      Shader.matrixClear()
      Shader.setMatrices()
      scene.draw
    Shader().end
    //fb.end

    //Shader(1).begin
    //  Shader(1).setUniformMatrix("u_projectionViewMatrix", new Matrix4())
    //  Shader(1).setUniformi("depthTexture",0)
    //  quad.draw
    //Shader(1).end
  }

  def resize(width: Int, height:Int){
    this.width = width
    this.height = height
    aspect = width * 1.f / height
    camera.viewportWidth = aspect
  }
  def pause(){
    audio ! Stop
  }
  def resume(){
    audio ! Play
  }
  def dispose(){
    audio.dispose
  }

  def toggleFullscreen(){


  }
}


