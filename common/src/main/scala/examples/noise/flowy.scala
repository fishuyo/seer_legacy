
package com.fishuyo
package examples.flow

import graphics._
import maths._
import ray._
import io._
import media._

import java.awt.event._


object Main extends App {

  var n = 600;
  val field = new VelocityField
  field.allocate(n,n)

  GLScene.push( field );

  val win = new GLRenderWindow
  win.addKeyMouseListener( Input )

}

class VelocityField extends Vec3Field2D {

  var c = 0.f;

  override def allocate( w:Int, h:Int ) = {
    super.allocate(w,h)
  }

  override def sstep(dt:Float) = {

    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      
    }
    
  }
}

object Input extends KeyMouseListener {

  override def keyPressed( e: KeyEvent ) = {
    val keyCode = e.getKeyCode()
    if( keyCode == KeyEvent.VK_ENTER ){
      Main.field.sstep(.01f)
    }
    if( keyCode == KeyEvent.VK_M ){
      Main.field.go = !Main.field.go
      
      /*Main.win.capture match{ 
        case v:MediaWriter => v.close(); Main.win.capture = null;
        case _ => Main.win.capture = new MediaWriter;
      }*/
    }
    if( keyCode == KeyEvent.VK_R ){
      Main.field.readImage( "input.png" )
    }
    if( keyCode == KeyEvent.VK_F ) Main.field.c += .01f
    if( keyCode == KeyEvent.VK_V ) Main.field.c -= .01f
    println( Main.field.c )
  }
  
  override def mouseDragged( e: MouseEvent) = mousePressed(e)
  override def mousePressed( e: MouseEvent) = {
    val x = e.getX
    val y = e.getY
    
    val o = Camera.position
    val v = Camera.projectPoint( x, y )
    
    val r = new Ray( o, v-o )
    val xx = ((r(200.f).x + 1.f) * Main.n/2).toInt
    val yy = ((r(200.f).y + 1.f) * Main.n/2).toInt
    if( xx >= 0 && xx <= Main.n-1 && yy >= 0 && yy <= Main.n-1 ){
//      Main.field.height.set(xx,yy,1.f) //println( "("+xx+", "+ yy+" ) : " + Main.field(xx,yy) );
    }
  }

}
