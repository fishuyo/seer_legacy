
package com.fishuyo.seer
package examples.fieldViewer
package live

import dynamic._
import graphics._
import maths._
import io._

object Main extends App with Animatable {
  DesktopApp.loadLibs()
  Scene.push(this)

  //val live = new Ruby("src/main/scala/examples/fieldViewer/live/live.rb")
  val live = new JS("src/main/scala/examples/fieldViewer/live/live.js")

  var fieldViewer = new FieldViewer(60,60)
  Scene.push(fieldViewer)

  DesktopApp()

  override def step(dt:Float) = {
    live.step(dt)
  }

}

class LiveField(ww:Int,hh:Int) extends Field2D(ww,hh) {
  
  var next = Field2D(w,h)

  override def resize(x:Int, y:Int) = {
    next.resize(x,y)
    super.resize(x,y)
  }
  
  override def step(dt: Float) = {

    for( y <- (0 until h); x <- (0 until w)){
      
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += getToroidal(x+i,y+j).r.toInt
      }
      //println( count );
      
      //was alive
      if( this(x,y).r > 0.f ){
        count -= 1
        if( count == 2 || count == 3) next.set(x,y,1.f)
        else {
        next.set(x,y,0.f)
        //println( x + " " + y + " dieing")
        }
      }else if( this(x,y).r == 0.f) { //was dead
        if( count == 3 ){
          next.set(x,y,1.f)
          //println( x + " " + y + " born")
        }
        else next.set(x,y,0.f)
      }
    }

    for( i <- (0 until w*h)) set(i, next(i))

  }
}
