
package com.fishuyo
package examples.conwayD

import graphics._
import maths._
//import ray._

import java.awt.event._
import java.nio._
//import com.jogamp.common.nio.Buffers

import com.fishuyo.io._

import com.badlogic.gdx._
import com.badlogic.gdx.math._
//import com.badlogic.gdx.collision._

object MyInput extends InputAdapter {

  override def touchDown( screenX:Int, screenY:Int, pointer:Int, button:Int) = {

    val x = screenX
    val y = screenY
    println( x + " " + y )
    
    // val o = Cam.position
    // val v = Cam.projectPoint( x, y )
    // println( v )
    
    val r = Camera.getPickRay(screenX,screenY) //new Ray( o, v-o )
    val xx = ((r.getEndPoint(1.f).x + 1.f) * Main.n/2).toInt  
    val yy = ((r.getEndPoint(1.f).y + 1.f) * Main.n/2).toInt 
    println( xx + " " + yy )
    if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ) Main.field.set( xx,yy, 1.f )
    true
  }

  override def mouseMoved( screenX:Int, screenY:Int) = {
    // val x = screenX
    // val y = screenY
    
    // val o = Camera.position
    // val v = Camera.projectPoint( x, y )
    
    // val r = new Ray( o, v-o )
    // val xx = ((r(200.f).x + 1.f) * Main.n/2).toInt  
    // val yy = ((r(200.f).y + 1.f) * Main.n/2).toInt 
    // if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ){
    //   var count = 0;
    //   for( j <- (-1 to 1); i <- (-1 to 1)){
    //     count += Main.field.getToroidal(xx+i,yy+j).toInt
    //     //count += this(xx+i,yy+j).toInt
    //   }
    //   //println( count );
    //   if( Main.field(xx,yy) > 0.0f ) count -= 1

    //   println( "("+xx+", "+ yy+" ) : " + count );
    // }
    false
  }
  override def touchDragged( screenX:Int, screenY:Int, pointer:Int) = {
    touchDown(screenX,screenY,pointer,0);
  }
  
  override def keyDown( keycode:Int ) = {
    val keyCode = keycode //e.getKeyCode()
    //println( "key: " + keycode + " " + KeyEvent.VK_ENTER)
    //if( keyCode == Input.Keys.ENTER ) Main.field.sstep(0)
    //if( keyCode == KeyEvent.VK_M ) Main.win.capture()
    true
  }

  override def keyTyped( c: Char) = {
    if( c == '\n' ) Main.field.sstep(0)
    true
  }


}

object Main extends App {

  val n = 100;
  val field = new ConwayField(n,n)

  //field.set( Array(1.0f, 0.f,0.f,0.f,0.f,1.f,0.f,0.f,0.f,0.f,1.f,0.f,0.f,0.f,0.f,1.f ));
  //field.set( 2, 2, 1.f );
  //field.set( 3, 2, 1.f );
  //field.set( 4, 2, 1.f );

  GLScene.push( field )
  Inputs.addProcessor(MyInput)

  // val win = new GLRenderWindow
  // win.glcanvas.addKeyListener( Input )
  // win.glcanvas.addMouseListener( Input )
  // win.glcanvas.addMouseMotionListener( Input )

  SimpleAppRun()

}

class ConwayField(w:Int,h:Int) extends Field2D(w,h) {
  
  var next: FloatBuffer = _ //Array[Float] = _

  
  override def sstep(dt: Float) = {

    if( next == null ){
      next = FloatBuffer.allocate( data.capacity ) //data.duplicate //new Array[Float](w*h);
    }

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
        if( count == 2 || count == 3) next.put(y*w+x,1.f)
        else {
        next.put(y*w+x,0.f)
        //println( x + " " + y + " dieing")
        }
      }else if( this(x,y).r == 0.f) { //was dead
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
