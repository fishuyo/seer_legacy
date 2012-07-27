
package com.fishuyo
package examples.juliacolor

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
    if( keyCode == KeyEvent.VK_ENTER ){
      import util.Random.nextFloat
      Main.field.c += Vec3( .1f*(nextFloat - .5f), .1f*(nextFloat - .5f), 0 )
      Main.field.sstep(0)
    }
    if( keyCode == KeyEvent.VK_V) Main.field.c += Vec3( -.1f,0,0)
    if( keyCode == KeyEvent.VK_B) Main.field.c += Vec3( 0,-.1f,0)
    if( keyCode == KeyEvent.VK_F) Main.field.c += Vec3( .1f,0,0)
    if( keyCode == KeyEvent.VK_G) Main.field.c += Vec3( 0,.1f,0)
    println( Main.field.c )
    if( keyCode == KeyEvent.VK_M ){
      //Main.field.go = !Main.field.go
      
      Main.win.capture match{ 
        case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
        case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      }
    }
  }
  def keyReleased( e: KeyEvent ) = {}
  def keyTyped( e: KeyEvent ) = {}


}

object Main extends App {

  val n = 400;
  val field = new MandlebrotField
  field.allocate(n,n)
  field.sstep(0);

  GLScene.push( field );

  val win = new GLRenderWindow
  win.glcanvas.addKeyListener( Input )
  win.glcanvas.addMouseListener( Input )
  win.glcanvas.addMouseMotionListener( Input )

}

class MandlebrotField extends Vec3Field2D {
  
  var next = new Vec3Field2D //FloatBuffer = _ //Array[Float] = _
  var c = Vec3( 0, 0, 0 )
  
  override def sstep(dt:Float) = {

    if( next.w == 0 ) next.allocate( w,h )

    import util.Random.nextFloat
    c += Vec3( .1f*(nextFloat - .5f), .1f*(nextFloat - .5f), 0 )

    for( y <- (0 until h); x <- (0 until w)){
      var p = Vec3( x * 6.f / w - 3.f, y * 6.f / h - 3.f, 0);
      var o = Vec3(0) + p
      
      var t = 0;

      while( p.mag < 20.f && t < 100 ){
        var xx = p.x*p.x - p.y*p.y
        var yy = math.abs(2*p.x*p.y)
        p = Vec3( xx, yy, 0)
        //p = Vec3( p.x*p.x, p.y*p.y, 0)
        p += c
        t += 1
      }
      var v = t.toFloat - (math.log(math.log(p.mag))/math.log(2.f)).toFloat
      //var v = (math.log(math.sqrt(p.x*p.x+p.y*p.y))/math.pow(2.f, t)).toFloat
      if( v.isNaN ) v = 0.f
      if( v.isInfinite ) v = 0.f

      set(y,h-1-x, Vec3(v,v,v) )
    }

    normalize();
    for( i<-(0 until w*h)) set(i, color(this(i).x) )
  }

  def color( x: Float ) : Vec3 = {

    val colors = Array( Vec3(0,0,0), Vec3(.3,.7,.1), Vec3(.6, .3, .3), Vec3( .6, 0,0), Vec3(.1,.6,.6),Vec3(.6,.6,.6), Vec3( 1,.9,.9))
    var i = math.abs(x*colors.length-1).toInt - 1
    if( i < 0 ) i = 0
    if( i >= colors.length-1) i = colors.length-2
    val c1 = colors(i)
    val c2 = colors(i+1)
    c1 * (1-x) + c2 * (x)

  }

}


