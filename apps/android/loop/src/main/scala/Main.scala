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

  // var loop = new Loop(10.f)
  // var plot = new AudioDisplay(500)
  // plot.color = Vec3(0,1,0)
  // plot.scale = Vec3(3)
  // Audio.push(loop);

  GLScene.push(this);

  var looper = new Looper

  Audio.push( looper )
  GLScene.push( looper )

  var t=0.f

  var l = 0
  var newPos = Vec3(0)

  Touch.use();
  Touch.bind("multi", (num:Int, coords:Array[Float]) => {
    num match {
      case 2 => {
        looper.setGain(0,coords(1) / 100.f)
        looper.setSpeed(0, coords(3) / 100.f )

        val b1 = coords(0) / 800.f 
        val b2 = coords(2) / 800.f
        looper.setBounds(0, b1, b2)
      }
    } 
  })
  Touch.bind("fling", (button:Int,v:Array[Float]) => {
    val thresh = 500
    if (v(0) > thresh) l = l-1
    else if (v(0) < -thresh) l = l+1
    else if (v(1) > thresh) l = l-4
    else if (v(1) < -thresh) l = l+4

    if( l < 0 ) l = l%looper.plots.size + looper.plots.size
    else l = l%looper.plots.size

    newPos.set( looper.plots(l).pose.pos + Vec3(0,0,0.5) )
    
  })

  //val live = new Ruby("res/live.rb", "com.fishuyo.loop" :: List())

  override def step(dt:Float){

    Camera.nav.pos.lerpTo(newPos, 0.1f)
    //live.step(dt)
  	if( t > 10.f){
  		looper.stop(0); looper.play(0)
  	}else if( t > 1.f && t < 1.5f){
  		looper.clear(0); looper.stop(0); looper.record(0)
		}
		t += dt;
  }

  override def draw(){
    //live.draw()
  // 	plot.setSamples(loop.b.samples, 0, loop.b.curSize)
		// plot.setCursor(2,loop.b.rPos.toInt)
  // 	plot.draw()
  }

}
