package com.fishuyo

import graphics._
import io._
import audio._

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Mesh

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

  var fbo:FrameBuffer = null
  var quad:Mesh = null
  // val t1 = new Texture(width, height, format);
  // val t2 = new Texture(width, height, format);

  var frameCount = 0
  val fps = new FPSLogger
  var logfps = true
  def setLogFPS(b:Boolean) = logfps = b

  var dtAccum = 0.f
  var paused = false

  def create(){

    Gdx.input.setInputProcessor( input )
    input.addProcessor( navInput )

    camera.nav.pos.z = 2.f 
    Gdx.gl.glClearColor(0,0,0,0)

    val path = "res/shaders/"

    //Shader(path+"simple.vert", path+"simple.frag")
    Shader("firstPass",path+"firstPass.vert", path+"firstPass.frag")
    Shader("secondPass",path+"secondPass.vert", path+"secondPass.frag")
    Shader.monitor("firstPass")
    Shader.monitor("secondPass")

    // t1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t1.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
    // t2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t2.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);

    scene.init()

    audio.start

  }
  def render(){
    if(paused) return
    if(quad == null){
      //FrameBuffers(width,height)

      fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
      quad = Primitive2D.quad
    }

    if( logfps ) fps.log
    val timeStep = 1.f/30.f
    dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep ){
      scene.step(timeStep)
      camera.step(timeStep)
      dtAccum -= timeStep
      frameCount +=1 
    }
    
    Gdx.gl.glClearColor(Shader.bg._1,Shader.bg._2,Shader.bg._3,Shader.bg._4)
    Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    //Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)
    //Gdx.gl.glEnable(GL20.GL_BLEND);
    //Gdx.gl.glEnable(GL20.GL_LINE_SMOOTH);

    // Gdx.gl.glEnable(GL10.GL_CULL_FACE);
    // Gdx.gl.glCullFace(GL10.GL_BACK);
    
    Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
    // Gdx.gl.glDepthFunc(GL10.GL_LESS);
    // Gdx.gl.glDepthMask(true);

    Shader.update

    ///////////////

    // fill the g-buffer
    fbo.begin() //FrameBuffer(0).begin();
    Shader("firstPass").begin();
    {
      //Gdx.gl.glClearColor(1,1,1,0)
      Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
      // Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)

      MatrixStack.clear()
      Shader.setMatrices()
      scene.draw()
    }
    Shader().end();
    fbo.end() //FrameBuffer(0).end();

    // bind first pass to texture0
    fbo.getColorBufferTexture().bind(0) //FrameBuffer(0).getColorBufferTexture().bind(0);


    // color
    Shader("secondPass").begin();
    {
      Shader().setUniformi("u_texture0", 0);
      Shader().setUniformMatrix("u_projectionViewMatrix", new Matrix4())
      //Shader().setUniformMatrix("u_modelViewMatrix", new Matrix4())
      // Shader().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
      scene.draw2()
      quad.render(Shader(), GL10.GL_TRIANGLES)
    }
    Shader().end();
    ///////////////////////

    // //fb.begin
    // //fb.getColorBufferTexture.bind(0)
    // //Gdx.gl.glClearColor(1,1,1,0);
    // //Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    // Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)

    // Shader().begin
    //   // Shader().setUniformMatrix("u_projectionViewMatrix", camera.combined)
    //   // Shader().setUniformMatrix("u_modelViewMatrix", camera.view)
    //   // Shader().setUniformMatrix("u_normalMatrix", camera.view.toNormalMatrix())
    //   Shader.matrixClear()
    //   Shader.setMatrices()
    //   scene.draw
    // Shader().end
    // //fb.end

    // //Shader(1).begin
    // //  Shader(1).setUniformMatrix("u_projectionViewMatrix", new Matrix4())
    // //  Shader(1).setUniformi("depthTexture",0)
    // //  quad.draw
    // //Shader(1).end
  }

  def resize(width: Int, height:Int){
    this.width = width
    this.height = height
    aspect = width * 1.f / height
    camera.viewportWidth = aspect
    val oldfbo = fbo
    fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
    if(oldfbo != null) oldfbo.dispose
  }
  def pause(){
    //audio ! Stop
    paused = true
    //Trackpad.disconnect()
  }
  def resume(){
    //audio ! Play
    paused = false
    //Trackpad.connect()
  }
  def dispose(){
    audio.dispose
    //Kinect.disconnect()
  }

}


