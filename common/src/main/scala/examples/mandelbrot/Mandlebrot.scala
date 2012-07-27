
package com.fishuyo
package examples.mandlebrot

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

  val n = 400;
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
      var p = Vec3( x * 4.f / w - 2.f, y * 4.f / h - 2.f, 0);
      var orig = Vec3(0) + p
      
      var t = 0;

      while( p.mag < 2.f && t < 100 ){
        var xx = p.x*p.x - p.y*p.y
        var yy = 2*p.x*p.y
        p = Vec3( xx, yy, 0)
        //p = Vec3( p.x*p.x, p.y*p.y, 0)
        p += orig
        t += 1
      }

      set(y,x,t)
    }

    normalize();
  }

}
