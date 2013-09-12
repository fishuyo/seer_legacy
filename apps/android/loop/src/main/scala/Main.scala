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

  var t = 0.f
  var l = 0
  var newPos = Vec3(0)

  val buttons = Model()
  buttons.scale.set(.05f)

  for( i<-(-2 to 2)){
    val offset = Vec3(0,2.5f,0)
    var m = buttons.translate( offset * i)
    m.color = RGBA(0,0,0,1)
    m.add( Quad.asLines() )
    i match {
      case 2 => m.onPick = (h) => { val r = looper.toggleRecord(l); if(r) m.color = (RGBA(1,0,0,1)) else m.color = (RGBA(0,0,0,1)) } 
      case 1 => m.onPick = (h) => { val p = looper.togglePlay(l); if(p) m.color = (RGBA(1,0,0,1)) else m.color = (RGBA(0,1,0,1)) } 
      case 0 => m.onPick = (h) => { val s = looper.stack(l); if(s) m.color = (RGBA(1,0,0,1)) else m.color = (RGBA(0,0,0,1)) }
      case -1 => m.onPick = (h) => { looper.setMaster(l) }
      case -2 => m.onPick = (h) => { looper.duplicate(l,1) }
    }
  }


  Mouse.use()
  Mouse.bind("down", (i:Array[Int]) => {
    val (x,y) = (i(0),i(1))
    val ray = Camera.ray(x,y)
    val hit = buttons.intersect(ray) //.intersect(buttons)
    // if (hit.isDefined) hit.get.obj.onPick(hit.get)
    

  })

  Touch.use();
  Touch.bind("multi", (num:Int, coords:Array[Float]) => {
    num match {
      case 2 => {
        val r1 = Camera.ray(coords(0),coords(1))
        val r2 = Camera.ray(coords(2),coords(3))

        val c = looper.plots(l).pose.pos
        val t1 = r1.intersectQuad(c,.5,.5)
        val t2 = r2.intersectQuad(c,.5,.5)

        if( t1.isDefined && t2.isDefined){
          val p1 = r1(t1.get) - c + Vec3(.5,.5,0)
          val p2 = r2(t2.get) - c + Vec3(.5,.5,0)

          looper.setGain(0,p1.y)
          looper.setSpeed(0, p2.y * 2.f )
          looper.setBounds(l, p1.x, p2.x)
        }
        
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

  Shader.lighting=0.f
  Shader.setBgColor(RGBA(1,1,1,1))
  looper.setMode("sync")


  //val live = new Ruby("res/live.rb", "com.fishuyo.loop" :: List())

  override def step(dt:Float){
    Camera.nav.pos.lerpTo(newPos, 0.15f)

    pos = Camera.nav.pos + Vec3.apply(-0.45,0,-0.55)
    buttons.pose.pos.lerpTo(pos, 0.2)
    //live.step(dt)
    //if( t > 10.f){
    //  looper.stop(0); looper.play(0)
    //}else if( t > 1.f && t < 1.5f){
    //  looper.clear(0); looper.stop(0); looper.record(0)
  	//}
  	//t += dt;
  }

  override def draw(){

  }

}
