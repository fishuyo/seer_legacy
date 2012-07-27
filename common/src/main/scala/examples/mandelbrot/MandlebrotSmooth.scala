
package com.fishuyo
package examples.mandlebrotsmooth

import graphics._
import maths._
import ray._

import java.awt.event._
import java.nio._
import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.util.awt.Screenshot

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
    //if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ) Main.field.set( xx,yy, 1.f )
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
      println( "("+xx+", "+ yy+" ) : " + Main.field(xx,yy) );
    }
  }
  def mouseDragged( e: MouseEvent) = {
    mousePressed(e);
  }
  
  def keyPressed( e: KeyEvent ) = {
    val keyCode = e.getKeyCode()
    //if( keyCode == KeyEvent.VK_ENTER ) Main.field.sstep(0)
    //if( keyCode == KeyEvent.VK_M ) Main.win.capture = !Main.win.capture
  }
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}


}

object Main extends App {

  val n = 1000;
  val field = new MandlebrotField
  field.allocate(n,n)
  field.calculate();

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addKeyListener( Input )
  win.glcanvas.addMouseListener( Input )
  win.glcanvas.addMouseMotionListener( Input )

}

class MandlebrotField extends Field2D {
  
  var next: FloatBuffer = _ //Array[Float] = _
  
  def calculate() = {

    for( y <- (0 until h); x <- (0 until w)){
      var p = Vec3( x * 6.f / w - 3.f, y * 6.f / h - 3.f, 0);
      var o = Vec3(0) + p
      
      var t = 0;

      while( p.mag < 20.f && t < 100 ){
        var xx = p.x*p.x - p.y*p.y
        var yy = 2*p.x*p.y
        p = Vec3( xx, yy, 0)
        //p = Vec3( p.x*p.x, p.y*p.y, 0)
        p += o
        t += 1
      }
      var v = t.toFloat - (math.log(math.log(p.mag))/math.log(2.f)).toFloat
      //var v = (math.log(math.sqrt(p.x*p.x+p.y*p.y))/math.pow(2.f, t)).toFloat
      if( v.isNaN ) v = t
      if( v.isInfinite ) v = 0.f
      set(y,h-1-x,v)
    }

    normalize();
    for( i<-(0 until data.capacity)) set(i, this(i)*5.f)
  }

}
