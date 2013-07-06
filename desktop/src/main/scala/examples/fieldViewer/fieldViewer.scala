
package com.fishuyo
package examples.fieldViewer

import dynamic._
import graphics._
import maths._
import io._

object Main extends App with GLAnimatable {
  SimpleAppRun.loadLibs()
  GLScene.push(this)

  val fieldViewer = new ConwayFV(100,100)
  GLScene.push(fieldViewer)

  val live = new Ruby("src/main/scala/examples/fieldViewer/fieldViewer.rb")

  SimpleAppRun()

  override def step(dt:Float) = {
    live.runEverytime(dt)
  }

}

class FieldViewer(var w:Int, var h:Int) extends GLAnimatable {

  var running = true;
  var field = new Field2D(w,h)
  var next = new Field2D(w,h)

  def resize(width:Int,height:Int){
    w = width
    h = height
    field.resize(w,h)
    next.resize(w,h)
  }

  def toggleRunning() = running = !running

  override def init() = runOnce()
  override def draw() = field.draw
  override def step(dt:Float) = if(running) runEverytime(dt)


  def runOnce(){

  }

  def runEverytime(dt:Float){
  }
}



// make subclass of FieldViewer and override runOnce and runEverytime

class ConwayFV(ww:Int, hh:Int) extends FieldViewer(ww,hh) {

  override def runEverytime(dt:Float) = {
    for( y <- (0 until h); x <- (0 until w)){  
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += field.getToroidal(x+i,y+j).r.toInt
      }
      
      //was alive
      if( field(x,y).r > 0.f ){
        count -= 1
        if( count == 2 || count == 3) next.set(x,y,1.f)
        else {
        next.set(x,y,0.f)
        //println( x + " " + y + " dieing")
        }
      }else if( field(x,y).r == 0.f) { //was dead
        if( count == 3 ){
          next.set(x,y,1.f)
          //println( x + " " + y + " born")
        }
        else next.set(x,y,0.f)
      }
    }
    field.set( next )
  }
  
}