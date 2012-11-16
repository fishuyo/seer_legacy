package com.fishuyo

import graphics._
import io._

import com.badlogic.gdx.utils.GdxNativesLoader
//import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.InputMultiplexer

object SimpleAppSize {
  val width = 600
  val height = 600
  val aspect = width * 1.f / height
}


class SimpleAppListener extends ApplicationListener {

  var width = SimpleAppSize.width
  var height = SimpleAppSize.height
  var aspect = SimpleAppSize.aspect

  var scene = GLScene
  var camera = Cam //new PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)
  val input = Inputs //new InputMultiplexer

  def create(){
    //input.addProcessor( KeyboardNavInput )
    Gdx.input.setInputProcessor( input )
    camera.translate(0,0,2) 
    Gdx.gl.glClearColor(0,0,0,0)
  }
  def render(){
    val timeStep = 1.f/60.f
    /*dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep )

    }*/
    scene.step(timeStep)
    Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT )
    camera.update
    camera.apply(Gdx.graphics.getGL11)
    scene.draw
  }
  def resize(width: Int, height:Int){
    this.width = width
    this.height = height
    aspect = width * 1.f / height
    camera.viewportWidth = aspect
  }
  def pause(){}
  def resume(){}
  def dispose(){}

  def toggleFullscreen(){


  }
}
