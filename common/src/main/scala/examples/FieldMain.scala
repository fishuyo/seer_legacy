
package com.fishuyo
package examples.field3d

import graphics._
import maths._
import ray._

import java.awt.event._

object Mouse extends MouseListener with MouseMotionListener {

  def mouseReleased( e: MouseEvent) = {}
  def mousePressed( e: MouseEvent) = {

    val x = e.getX
    val y = e.getY
    println( x + " " + y )
    
    val o = Camera.position
    val v = Camera.projectPoint( x, y )
    println( v )
    
    val r = new Ray( o, v-o )
    val xx = ((r(200.f).x + 1.f) * 32).toInt  
    val yy = ((r(200.f).y + 1.f) * 32).toInt 
    println( xx + " " + yy )
    //if( xx >= 0 && xx <= 63 && yy >= 0 && yy <= 63 ) Main.field.set( xx,yy, 1.f )
  }

  def mouseClicked( e: MouseEvent) = {}
  def mouseEntered( e: MouseEvent) = {}
  def mouseExited( e: MouseEvent) = {}
  def mouseMoved( e: MouseEvent) = {}
  def mouseDragged( e: MouseEvent) = {}


}

object Main extends App {

  val n = 10;
  val field = new VecField3D( n )
  for( z<-(0 until n); y<-(0 until n); x<-(0 until n)){
    field.set(x,y,z, field.centerOfBin(x,y,z).normalize * -.1f)
  }

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addMouseListener( Mouse )
  win.glcanvas.addMouseMotionListener( Mouse )

}
