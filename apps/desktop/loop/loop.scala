package com.fishuyo
package examples.loop

import graphics._
import maths._
import spatial._
import io._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)
  
  var looper = new Looper
  var l = 0

  Audio.push( looper )
  GLScene.push( looper )

  val live = new Ruby("loop.rb")

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

     val r1 = Camera.ray(i(0),i(1))
    // val r2 = Camera.ray(coords(2).toInt,coords(3).toInt)

    val c = looper.plots(l).pose.pos
    val t1 = r1.intersectQuad(c,.5f,.5f)
    // val t2 = r2.intersectQuad(c,.5f,.5f)

    if( t1.isDefined ){
      val p1 = (r1(t1.get) - c)*2.f + Vec3(.5,.5,0)
      println( p1 )
      // val p2 = r2(t2.get) - c + Vec3(.5,.5,0)

      // looper.setGain(l,p1.y)
      // looper.setSpeed(l, p2.y * 2.f )
      // looper.setBounds(l, p1.x, p2.x)
    } 
    

  })

 

  Shader.lighting=0.f
  Shader.setBgColor(RGBA(1,1,1,1))

  SimpleAppRun()  

  override def step(dt:Float){

    live.step(dt)
  }

  override def draw(){
    live.draw()
    buttons.draw()
  }

}



