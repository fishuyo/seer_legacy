package com.fishuyo.seer
package examples.synth
import maths._
import graphics._
import trees._
import io._
import dynamic._
import audio._

import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask

import scala.collection.mutable.ListBuffer

object Main extends App with Animatable {

  DesktopApp.loadLibs()
  Scene.push(this)

  var sines = ListBuffer[Osc]()
  for( i<-(0 until 10)) sines += new Sine(0.f)
  sines.foreach( s => Audio.push( s ))

  var display = Array(new AudioDisplay(512), new AudioDisplay(512))
  display(0).pose.pos.set(-1,0,0)

  var osc = new Tri(41.f)
  var lfo = new Saw(1.f)

  Audio.push( (lfo + osc.f ) -> osc  ) //* ((1.f-lfo)*.5f) )

  val live = new Ruby("synth.rb")

  DesktopApp()

  override def step(dt:Float){

    // implicit val timeout = Timeout(5 seconds)

    live.step(dt)

    //val f = Audio.main ? OutBuffer
    //f.zipWithIndex.foreach( (i,a) => display(i).setSamples(a) )

    display(0).setSamples(Audio.out(0))
    display(1).setSamples(Audio.out(1))
  } 
  override def draw(){
    Shader.lightingMix = 0
  	display.foreach( _.draw())
  }
}



