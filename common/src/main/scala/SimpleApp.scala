package com.fishuyo

import graphics._
import io._

import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.FPSLogger

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
  var camera = Cam //new PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)
  var input = Inputs //new InputMultiplexer
  var audio:SimpleAudio = _

  val fps = new FPSLogger
  var dtAccum = 0.f

  def create(){
    //input.addProcessor( KeyboardNavInput )
    Gdx.input.setInputProcessor( input )
    camera.translate(0,0,2) 
    Gdx.gl.glClearColor(0,0,0,0)
    audio = new SimpleAudio(44100)
    //audio.start

    println(System.getProperty("user.dir"))
    println( "local: " + Gdx.files.getLocalStoragePath )

    val f = Gdx.files.internal("res/wind.mp3")
    val wind = Gdx.audio.newMusic(f)
    wind.setVolume(.3f)
    wind.setLooping(true)
    //wind.play

  }
  def render(){
    fps.log
    val timeStep = 1.f/30.f
    dtAccum += Gdx.graphics.getDeltaTime()
    while( dtAccum > timeStep ){
      scene.step(timeStep)
      dtAccum -= timeStep
    }
    
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

case class Process
case class Stop
case class Play
case class Gain(g:Float)

class SimpleAudio( sampleRate: Int) extends Actor {
  val bufSize = 1024;
  var gain = .5f;
  var playing = true;
  val device = Gdx.audio.newAudioDevice(44100, true)
  val samples = new Array[Float](bufSize)
  val out = new Array[Float](bufSize)
  var v = 0.f;
  var inc = .01f;
  for( i<- 0  to 360){
    samples(i) = v;
    v += inc;
    if( v >= .9f) inc = -.01f;
    if( v <= -.9f) inc = .01f;
  }

  def act(){
    self ! Process
    loop{
      react{
        case Process => if( playing ){

            for( i<-(0 until samples.length)) out(i) = samples(i)*gain
            device.writeSamples(out,0,360)//samples.length)
            self ! Process
          }
        case Stop => playing = false
        case Play => playing = true; self ! Process
        case Gain(g) => gain = g;

      }
    }
  }

  def dispose(){
    device.dispose
  }
}
