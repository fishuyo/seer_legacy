
package com.fishuyo
package examples.conway

import graphics._
import maths._
import ray._

import java.awt.event._
import java.nio._
import com.jogamp.common.nio.Buffers

object Input extends KeyListener with MouseListener with MouseMotionListener {

  def mouseReleased( e: MouseEvent) = {}
  def mousePressed( e: MouseEvent) = {

    val x = e.getX
    val y = e.getY
    println( x + " " + y )
    
    val o = Camera.position
    val v = Camera.projectPoint( x, y )
    println( v )
    
    val r = new Ray( o, v-o )
    val xx = ((r(200.f).x + 1.f) * Main.n/2).toInt  
    val yy = ((r(200.f).y + 1.f) * Main.n/2).toInt 
    println( xx + " " + yy )
    if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ) Main.field.set( xx,yy, 1.f )
  }

  def mouseClicked( e: MouseEvent) = {}
  def mouseEntered( e: MouseEvent) = {}
  def mouseExited( e: MouseEvent) = {}
  def mouseMoved( e: MouseEvent) = {
    val x = e.getX
    val y = e.getY
    
    val o = Camera.position
    val v = Camera.projectPoint( x, y )
    
    val r = new Ray( o, v-o )
    val xx = ((r(200.f).x + 1.f) * Main.n/2).toInt  
    val yy = ((r(200.f).y + 1.f) * Main.n/2).toInt 
    if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ){
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += Main.field.getToroidal(xx+i,yy+j).toInt
        //count += this(xx+i,yy+j).toInt
      }
      //println( count );
      if( Main.field(xx,yy) > 0.0f ) count -= 1

      println( "("+xx+", "+ yy+" ) : " + count );
    }
  }
  def mouseDragged( e: MouseEvent) = {
    mousePressed(e);
  }
  
  def keyPressed( e: KeyEvent ) = {
    val keyCode = e.getKeyCode()
    if( keyCode == KeyEvent.VK_ENTER ) Main.field.sstep(0)
    //if( keyCode == KeyEvent.VK_M ) Main.win.capture()
  }
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}


}

object Main extends App {

  val n = 100;
  val field = new ConwayField
  field.allocate(n,n)

  //field.set( Array(1.0f, 0.f,0.f,0.f,0.f,1.f,0.f,0.f,0.f,0.f,1.f,0.f,0.f,0.f,0.f,1.f ));
  //field.set( 2, 2, 1.f );
  //field.set( 3, 2, 1.f );
  //field.set( 4, 2, 1.f );

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addKeyListener( Input )
  win.glcanvas.addMouseListener( Input )
  win.glcanvas.addMouseMotionListener( Input )

}

class ConwayField extends Field2D {
  
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
