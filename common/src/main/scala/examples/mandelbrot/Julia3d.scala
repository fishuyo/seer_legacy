
package com.fishuyo
package examples.juliaset3

import graphics._
import maths._
import ray._
import media._

import java.awt.event._
import java.nio._
import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.util.awt.Screenshot

object Input extends KeyListener with MouseListener with MouseMotionListener {

  def mouseReleased( e: MouseEvent) = {}
  def mousePressed( e: MouseEvent) = {

    val x = e.getX
    val y = e.getY
    
    val o = Camera.position
    val v = Camera.projectPoint( x, y )
    println( v )
    
    val r = new Ray( o, v-o )
    val xx = ((r(200.f).x + 1.f) * Main.n/2).toInt  
    val yy = ((r(200.f).y + 1.f) * Main.n/2).toInt 
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
    if( keyCode == KeyEvent.VK_ENTER ){
      import util.Random.nextFloat
      Main.field.constant += Vec3( .1f*(nextFloat - .5f), .1f*(nextFloat - .5f), .1f*(nextFloat -.5f) )
      Main.field.sstep(0)
    }
    if( keyCode == KeyEvent.VK_V) Main.field.constant += Vec3( -.1f,0,0)
    if( keyCode == KeyEvent.VK_B) Main.field.constant += Vec3( 0,-.1f,0)
    if( keyCode == KeyEvent.VK_F) Main.field.constant += Vec3( .1f,0,0)
    if( keyCode == KeyEvent.VK_G) Main.field.constant += Vec3( 0,.1f,0)
    println( Main.field.constant )
    if( keyCode == KeyEvent.VK_M ){
      Main.field.go = !Main.field.go
      /*
      Main.win.capture match{ 
        case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
        case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      }*/
    }
  }
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}


}

object Main extends App {

  val n = 200;
  val field = new MandlebrotField
  field.allocate(n,n)
  field.sstep(0);

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addKeyListener( Input )
  win.glcanvas.addMouseListener( Input )
  win.glcanvas.addMouseMotionListener( Input )

}

class MandlebrotField extends Field2D {
  
  var next: FloatBuffer = _ //Array[Float] = _
  var constant = Vec3( 0, 0, 0 )

  override def sstep(dt: Float) = {

    if( next == null ) next = FloatBuffer.allocate( data.capacity )

    import util.Random.nextFloat
    constant += Vec3( .1f*(nextFloat - .5f), .1f*(nextFloat - .5f), .1f*(nextFloat - .5f) )
    
    for( y <- (0 until h); x <- (0 until w)){
      var p = Vec3( x * 4.f / w - 2.f, y * 4.f / h - 2.f, 0);

      var count=0.f
      for( j <- (-1 to 1); i <- (-1 to 1 ) ) count += getToroidal(y+j,x+i)
      count *= .1f;

      var t = 0;

      while( p.mag < 2.f && t < 100 ){
        var xx = p.x*p.x - p.y*p.y
        var yy = 2*p.x*p.y
        p = Vec3( xx, yy, 0)
        //p = Vec3( p.x*p.x, p.y*p.y, 0)
        p += constant // count // Vec3( count, count, 0 )
        t += 1
      }

      next.put(y*w+x,t) //set(y,x,t)
    }
    
    for( i<-(0 until next.capacity)) set(i, next.get(i))
    normalize();
  }


}
