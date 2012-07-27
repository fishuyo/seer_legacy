
package com.fishuyo
package examples.imagewaves

import graphics._
import maths._
import ray._
import io._
import media._

import java.awt.event._


object Main extends App {

  var n = 600;
  val field = new WaveField
  field.allocate(n,n)

  GLScene.push( field );

  val win = new GLRenderWindow
  win.addKeyMouseListener( Input )

}

class WaveField extends Vec3Field2D {
  
  val img = new Vec3Field2D
  val next = new Field2D
  val height = new Field2D
  val oldheight = new Field2D

  val chemA = new ChemField
  val chemB = new ChemField
  var alphaA = .0002f
  var alphaB = .00001f
  val dx = 1.f / Main.n
  var dt = .001f
  var F = .05f
  var K = .0675f

  var c = .6f
  val r2 = (dt*dt)/(dx*dx)
  
  type RA = List[Float]
  val ra = (a: Float, b:Float ) => -a*b*b + F*(1.f-a)
  val rb = (a: Float, b:Float ) => a*b*b - (F+K)*b


  override def allocate( w:Int, h:Int ) = {
    super.allocate(w,h)
    //chemFields.foreach( _.allocate(w,h) )
    img.allocate(w,h)
    next.allocate(w,h)
    height.allocate(w,h)
    oldheight.allocate(w,h)
    chemA.allocate(w,h)
    chemB.allocate(w,h)
  }

  override def sstep(dt:Float) = {

    /*for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      var a =  -4.f * chemA(x,y) + chemA(x+1,y) + chemA(x-1,y) + chemA(x,y+1) + chemA(x,y-1)
      var b =  -4.f * chemB(x,y) + chemB(x+1,y) + chemB(x-1,y) + chemB(x,y+1) + chemB(x,y-1)
      a = chemA(x,y) + a * dtt * alphaA / (dx*dx)
      b = chemB(x,y) + b * dtt * alphaB / (dx*dx)
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)
    }*/
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      
      //var a = chemA.next(x,y) + dtt * ra( chemA.next(x,y), chemB.next(x,y))
      //var b = chemB.next(x,y) + dtt * rb( chemA.next(x,y), chemB.next(x,y))
      //chemA.next.set(x,y,a)
      //chemB.next.set(x,y,b)

      val lap =  -4.f*height(x,y) + height(x+1,y) + height(x-1,y) + height(x,y+1) + height(x,y-1)
      val tmp = height(x,y)
      val v = r2 * c * lap + 2.f*tmp - oldheight(x,y)
      next.set(x,y,v)
      oldheight.set(x,y,tmp)

      set(x,y, img(x,y) * color(v) ) //Vec3(v,v,v) )
    }
    
    for( i <- ( 0 until w*h ) ) height.set(i, next(i)) //{ chemA.set(i, chemA.next(i) ); chemB.set(i, chemB.next(i)) }
  }

  def color( v:Float ) ={
    val l = Vec3(0.f, .2f, .8f) :: Vec3(.6f, .5f, 0.f) :: Vec3(1.f,0,0) :: List()
    var i = (v*(l.length-1)).toInt
    l(i)*v
  }
}

class ChemField extends Field2D {
  val next = new Field2D
  override def allocate(w:Int,h:Int) = { super.allocate(w,h); next.allocate(w,h) }
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
      Main.field.img.readImage( "color.png" )
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
      Main.field.height.set(xx,yy,1.f) //println( "("+xx+", "+ yy+" ) : " + Main.field(xx,yy) );
    }
  }

}
