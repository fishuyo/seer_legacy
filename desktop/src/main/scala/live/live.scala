
package com.fishuyo
package live

import dynamic._
import graphics._
import maths._
import io._

object Main extends App with GLAnimatable {
  SimpleAppRun.loadLibs()
  GLScene.push(this)

  val live = new JS("src/main/scala/live/live.js")

  var n = 100
  var field = new LiveField(n,n)

  GLScene.push( field )

  SimpleAppRun()

  override def step(dt:Float) = {
    if( field.w != n ) field = new LiveField(n,n)
    live.step(dt)
  }

}

class LiveField(w:Int,h:Int) extends Field2D(w,h) {
  
  var next = Field2D(w,h)
  
  override def sstep(dt: Float) = {

    for( y <- (0 until h); x <- (0 until w)){
      
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += getToroidal(x+i,y+j).r.toInt
        //count += this(x+i,y+j).toInt
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
