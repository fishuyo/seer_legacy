
package com.fishuyo
package examples.edgedetect

import graphics._
import maths._
import ray._
import media._

import java.awt.event._
import java.nio._
import javax.imageio._
import java.io._
import java.awt.image.BufferedImage

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.util.awt.Screenshot

object Input extends KeyListener with MouseListener with MouseMotionListener {

  def mouseReleased( e: MouseEvent) = {}
  def mousePressed( e: MouseEvent) = {}
  def mouseClicked( e: MouseEvent) = {}
  def mouseEntered( e: MouseEvent) = {}
  def mouseExited( e: MouseEvent) = {}
  def mouseMoved( e: MouseEvent) = {}
  def mouseDragged( e: MouseEvent) = {}
  
  def keyPressed( e: KeyEvent ) = {
    val keyCode = e.getKeyCode()
    if( keyCode == KeyEvent.VK_ENTER ){
      Main.field.sstep(.01f)
    }
    if( keyCode == KeyEvent.VK_M ){
      //Main.field.go = !Main.field.go
      
      Main.win.capture match{ 
        case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
        case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      }
    }
    if( keyCode == KeyEvent.VK_R ){
      Main.field.readImage( "input.png" )
    }
    if( keyCode == KeyEvent.VK_1 ) Main.field.k = 0
    if( keyCode == KeyEvent.VK_2 ) Main.field.k = 1
    if( keyCode == KeyEvent.VK_3 ) Main.field.k = 2
    if( keyCode == KeyEvent.VK_4 ) Main.field.k = 3
    if( keyCode == KeyEvent.VK_5 ) Main.field.k = 4
    if( keyCode == KeyEvent.VK_6 ) Main.field.k = 5
    if( keyCode == KeyEvent.VK_7 ) Main.field.k = 6
    if( keyCode == KeyEvent.VK_F ) Main.field.alpha += .05f
    if( keyCode == KeyEvent.VK_V ) Main.field.alpha -= .05f
    println("alpha = " + Main.field.alpha)
  }
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}
}

object Main extends App {

  var n = 600;
  val field = new EdgeField
  field.allocate(n,n)

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addKeyListener( Input )
  win.glcanvas.addMouseListener( Input )
  win.glcanvas.addMouseMotionListener( Input )

}

class EdgeField extends Field2D {
  
  val next = new Field2D
  val kernals = List[List[Float]]( Kernel.edgeEnhance, Kernel.edgeDetect, Kernel.emboss, Kernel.sharpen, Kernel.blur, Kernel.gaus, Kernel.laplacian )
  var alpha = .1f
  var k = 0

  override def sstep(dt:Float) = {

    if( next.w != w || next.h != h ) next.allocate( w,h )

    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      
      var v = multiplyKernel(x,y, kernals(k) )
      if(k == 6 ) v = this(x,y) + dt * alpha * v 
      next.set( x,y,v ) 
    }
 
    for( i <- ( 0 until w*h ) ) set(i, next(i) )
  }

  def multiplyKernel( x:Int, y:Int, l: List[Float] ) : Float = {

    var s = math.sqrt(l.length).toInt //assume square kernel
    s = s / 2
    var v = 0.f
    var c = 0
    for( j<-(-s to s); i<-(-s to s)){
      v += l(c) * getToroidal(x+i,y+j)
      c += 1
    }
    v
  }

}

object Kernel {
  def dx2 = List(0.f,0,0, 1,-2,1, 0,0,0 )
  def dy2 = List(0.f,1,0, 0,-2,0, 0,1,0 )
  def laplacian = List(0.f,1,0, 1,-4,1, 0,1,0 )
  def edgeEnhance = List(0.f,0.f,0.f,-1.f,1.f,0.f,0.f,0.f,0.f)
  def edgeDetect = List(0.f,1.f,0.f,1.f,-4.f,1.f,0.f,1.f,0.f)
  def emboss = List(-2.f,-1.f,0.f,-1.f,1.f,1.f,0.f,1.f,2.f)
  def sharpen = List(0.f,0.f,0.f,0.f,0.f, 0.f,0.f,-1.f,0.f,0.f, 0.f,-1.f,5.f,-1.f,0.f, 0.f,0.f,-1.f,0.f,0.f, 0.f,0.f,0.f,0.f,0.f)
  def blur = List(0.f,0.f,0.f,0.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,0.f,0.f,0.f,0.f)
  def gaus = List(.25f,.25f,.25f,.25f,-2.f,.25f,.25f,.25f,.25f)

}
