package com.fishuyo.seer

import graphics._
import io._
import audio._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.glutils.{FrameBuffer=>GdxFrameBuffer}
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Mesh

// import scala.actors.Actor
// import scala.actors.Actor._

object SimpleAppSize {
  val width = 800
  val height = 800
  val aspect = width * 1.f / height
}


class SimpleAppListener extends ApplicationListener {

  var width = SimpleAppSize.width
  var height = SimpleAppSize.height
  var aspect = SimpleAppSize.aspect

  var scene = Scene
  var camera = Camera //new PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)
  var input = Inputs //new InputMultiplexer
  var navInput = new KeyboardNavInput(Camera.nav)
  var audio = Audio

  var renderNodes = new ListBuffer[RenderNode]()

  var fbo:GdxFrameBuffer = null
  var quad:Mesh = null

  var frameCount = 0
  val fps = new FPSLogger
  var logfps = true

  var dtAccum = 0.f
  var paused = false

  def create(){

    Gdx.input.setInputProcessor( input )
    input.addProcessor( navInput )

    camera.nav.pos.z = 2.f
    // Gdx.gl.glClearColor(0,0,0,0)

    // val path = "res/shaders/"

    //Shader(path+"simple.vert", path+"simple.frag")
    Shader.load("basic", DefaultShaders.basic._1, DefaultShaders.basic._2)
    // Shader.load("firstPass", DefaultShaders.firstPass._1, DefaultShaders.firstPass._2)
    // Shader.load("secondPass", DefaultShaders.secondPass._1, DefaultShaders.secondPass._2)

    // t1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t1.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
    // t2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // t2.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);

    // val node = new RenderNode
    // node.scene = Scene
    // node.camera = Camera

    // SceneGraph.addNode(node)
    // renderNodes += node
    scene.init()

    audio.start
  }

  def render(){
    if(paused) return
    
    if(quad == null){
      //FrameBuffers(width,height)

      fbo = FrameBuffer(width, height)
      quad = Primitive2D.quad
    }

    if( logfps ) fps.log
    val timeStep = 1.f/30.f
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
    //Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)

    if( Shader.blend ){ 
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
    }else {
      Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
    }
    //Gdx.gl.glEnable(GL20.GL_LINE_SMOOTH);

    // Gdx.gl.glEnable(GL10.GL_CULL_FACE);
    // Gdx.gl.glCullFace(GL10.GL_BACK);
    
    // Gdx.gl.glDepthFunc(GL10.GL_LESS);
    // Gdx.gl.glDepthMask(true);

    SceneGraph.render() //renderNodes.foreach( _.render )

    ///////////////
    ///////////////

    // if(Shader.multiPass){
    //   // fill the g-buffer
    //   fbo.begin()
    //   Shader("firstPass").begin();
    // }else { 
    //   Shader("basic").begin()
    // }
    
    // //Gdx.gl.glClearColor(1,1,1,0)
    // Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    // // Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)

    // MatrixStack.clear()
    // Shader.setMatrices()
    // scene.draw()
    
    // Shader().end();

    // if(Shader.multiPass){
    //   fbo.end() //FrameBuffer(0).end();

    //   //bind first pass to texture0
    //   fbo.getColorBufferTexture().bind(0) //FrameBuffer(0).getColorBufferTexture().bind(0);

    //   //color
    //   Shader("secondPass").begin();
    //   {
    //     Shader().setUniformi("u_texture0", 0);
    //     Shader().setUniformMatrix("u_projectionViewMatrix", new Matrix4())
    //     //Shader().setUniformMatrix("u_modelViewMatrix", new Matrix4())
    //     // Shader().setUniformMatrix("u_normalMatrix", modelViewMatrix.toNormalMatrix())
    //     // scene.draw2()
    //     quad.render(Shader(), GL10.GL_TRIANGLES)
    //   }
    //   Shader().end();
    // }

  }

  def resize(width: Int, height:Int){
    SceneGraph.resize(Viewport(width,height))
    this.width = width
    this.height = height
    aspect = width * 1.f / height
    camera.viewportWidth = aspect
    val oldfbo = fbo
    fbo = FrameBuffer(width, height)
    if(oldfbo != null) oldfbo.dispose
  }
  def pause(){
    //audio ! Stop
    // paused = true
    //Trackpad.disconnect()
  }
  def resume(){
    //audio ! Play
    // paused = false
    //Trackpad.connect()
  }
  def dispose(){
    println("Dispose called.")
    // audio.stop
    Thread.sleep(100)
    // audio.main ! "dispose"
    //Kinect.disconnect()
  }

}


