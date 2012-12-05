package com.fishuyo

import graphics._
import io._
import audio._

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.glutils._

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

  val fps = new FPSLogger
  var dtAccum = 0.f

  def create(){

    Gdx.input.setInputProcessor( input )
    input.addProcessor( navInput )

    camera.nav.pos.z = 2.f 
    Gdx.gl.glClearColor(0,0,0,0)

    val path = "../common/src/main/scala/graphics/shaders/"

    Shader(path+"simple.vert", path+"simple.frag")

    audio.start

  }
  def render(){
    fps.log
    val timeStep = 1.f/30.f
    dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep ){
      scene.step(timeStep)
      camera.step(timeStep)
      dtAccum -= timeStep
    }
    
    Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT )

    camera.update
    //camera.apply(Gdx.graphics.getGL11)
    Shader.update
    Shader().begin
      Shader().setUniformMatrix("u_projectionViewMatrix", camera.combined)
      Shader().setUniformMatrix("u_modelViewMatrix", camera.view)
      Shader().setUniformMatrix("u_normalMatrix", camera.view.toNormalMatrix())

      scene.draw
    Shader().end
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


