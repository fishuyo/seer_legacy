
package com.fishuyo
package live

import dynamic._
import graphics._
import maths._
//import ray._
import io._

import java.awt.event._
import java.nio._
//import com.jogamp.common.nio.Buffers

import com.badlogic.gdx._
import com.badlogic.gdx.math._
//import com.badlogic.gdx.collision._

object Print{
  def apply( s:String ) = println(s)
}
object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {

    val x = screenX
    val y = screenY
    println( x + " " + y )
    
    val r = Cam.getPickRay(screenX,screenY)
    val xx = ((r.getEndPoint(1.f).x + 1.f) * Main.n/2).toInt  
    val yy = ((r.getEndPoint(1.f).y + 1.f) * Main.n/2).toInt 
    println( xx + " " + yy )
    if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ) Main.field.set( xx,yy, 1.f )
    true
  }

  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    touchDown(screenX,screenY,pointer,0);
  }

  override def keyTyped( c: Char) = {
    c match {
      case '\n' => Main.field.sstep(0)
      case 'r' => Main.live.reload
      case _ => false
    }
    true
  }
}

object Main extends App {

  val live = new Ruby("src/main/scala/live/live.rb")

	live.init()

  val n:Int = live.size().asInstanceOf[Long].toInt //100;
  val field = new LiveField
  field.allocate(n,n)
  live.data(field)

  GLScene.push( field )
  Inputs.addProcessor(MyInput)

  SimpleAppRun()

}

class LiveField extends Field2D {
  
  var next: FloatBuffer = _ //Array[Float] = _

  
  override def sstep(dt: Float) = {

    if( next == null ){
      next = FloatBuffer.allocate( data.capacity ) //data.duplicate //new Array[Float](w*h);
    }

    for( y <- (0 until h); x <- (0 until w)){
      
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += getToroidal(x+i,y+j).toInt
        //count += this(x+i,y+j).toInt
      }
      //println( count );
      
      //was alive
      if( this(x,y) > 0.f ){
        count -= 1
        if( count == 2 || count == 3) next.put(y*w+x,1.f)
        else {
        next.put(y*w+x,0.f)
        //println( x + " " + y + " dieing")
        }
      }else if( this(x,y) == 0.f) { //was dead
        if( count == 3 ){
          next.put(y*w+x,1.f)
          //println( x + " " + y + " born")
        }
        else next.put(y*w+x,0.f)
      }
    }

    for( i <- (0 until next.capacity)) set(i, next.get(i))

  }
}
