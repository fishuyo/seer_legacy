package com.fishuyo
package examples.synth
import maths._
import graphics._
import trees._
import io._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable {

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  var sines = ListBuffer[Osc]()
  for( i<-(0 until 10)) sines += new Sine(0.f)
  sines.foreach( s => Audio.push( s ))

  var display = Array(new AudioDisplay(512), new AudioDisplay(512))
  display(0).pose.pos.set(-1,0,0)

  var osc = new Tri(41.f)
  var lfo = new Saw(1.f)

  Audio.push( (lfo + osc.f ) -> osc  ) //* ((1.f-lfo)*.5f) )

  val live = new Ruby("src/main/scala/synth/synth.rb")

  SimpleAppRun() 

  override def step(dt:Float){
    live.step(dt)

    display(0).setSamples(Audio.out(0))
    display(1).setSamples(Audio.out(1))
  } 
  override def draw(){
  	display.foreach( _.draw())
  }
}



