package com.fishuyo
package loop

import maths._
import graphics._
import audio._
import io._
import dynamic._

import android.os.Bundle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android._
import com.badlogic.gdx.InputAdapter

import scala.collection.mutable.ListBuffer

class Main extends AndroidApplication {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.depth = 0
    config.useGL20 = true
    val loopscene = new LoopScene
    initialize(new SimpleAppListener, config)
  }
}


class LoopScene extends InputAdapter with GLAnimatable {

  var loop = new Loop(10.f)
  var plot = new AudioDisplay(500)
  plot.color = Vec3(0,1,0)
  plot.scale = Vec3(3)
  var t=0.f

  GLScene.push(this);
  Audio.push(loop);

  Inputs.addProcessor(this)

  override def step(dt:Float){
  	if( t > 10.f){
  		loop.stop; loop.play
  	}else if( t > 1.f && t < 1.5f){
  		loop.clear; loop.stop; loop.record
		}
		t += dt;
  }

  override def draw(){
  	plot.setSamples(loop.b.samples, 0, loop.b.curSize)
		plot.setCursor(2,loop.b.rPos.toInt)
  	plot.draw()
  }


  // Input 
	val down = ListBuffer.fill(20)(false)
  val pos = new Array[Vec3](20)

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {

  	down(pointer) = true
  	pos(pointer) = Vec3(screenX,screenY,0.f)

    val indices = down.zipWithIndex.collect{ case (true,i) => i }
    val p = indices.map( pos(_) )
    val centroid = p.sum / p.length

    if( p.size == 2){
    	loop.gain = p(0).y / 100.f
    	loop.b.speed = p(1).y / 100.f 

			val b1 = p(0).x / 800.f * loop.b.curSize
			val b2 = p(1).x / 800.f * loop.b.curSize
			if (b1 > b2) loop.reverse
			loop.b.setBounds(b1.toInt,b2.toInt)
			plot.setCursor(0,b1.toInt)
			plot.setCursor(1,b2.toInt)
    }

    true
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
  	touchDown(screenX,screenY,pointer,0);
  	true
  }
  
  override def touchUp (x:Int, y:Int, pointer:Int, button:Int) = {
  	down(pointer) = false
  	true
  }

  override def keyTyped( c: Char) = {
    c match {
      //case 'r' => Main.live.reload
      case _ => false
    }
    true
  }
}
