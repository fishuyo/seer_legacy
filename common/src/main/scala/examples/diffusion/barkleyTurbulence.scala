
package com.fishuyo
package examples.reactdiffuse.barkleyT

import graphics._
import maths._
import ray._
import io._
import media._

import java.awt.event._


object Main extends App {

  var n = 200;
  val field = new ReactDiffuseField
  field.allocate(n,n)

  field.chemB.set( field.A*.5f )
  
  GLScene.push( field );

  val win = new GLRenderWindow
  win.addKeyMouseListener( Input )

}

class ReactDiffuseField extends Vec3Field2D {
  
  val next = new Field2D

  val chemA = new ChemField
  val chemB = new ChemField
  var alphaA = .75f
  var alphaB = 0f
  var eps = 1.f/12.f
  val dx = 320.f / 200.f //Main.n
  var dtt = .02f
  var A = .75f
  var B = .07f
  

  type RA = List[Float]
  val ra = (a: Float, b:Float ) => (1.f/eps)*(a*(1.f-a)*(a-(b+B)/A))
  val rb = (a: Float, b:Float ) => a*a*a-b


  override def allocate( w:Int, h:Int ) = {
    super.allocate(w,h)
    //chemFields.foreach( _.allocate(w,h) )
    next.allocate(w,h)
    chemA.allocate(w,h)
    chemB.allocate(w,h)
  }

  override def sstep(dt:Float) = {
    val s = 5;
    for( j <- (-s to s); i <- (-s to s) ) chemA.set( w/2+i, h/2+j, 1.f )

    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      var a =  -4.f * chemA(x,y) + chemA(x+1,y) + chemA(x-1,y) + chemA(x,y+1) + chemA(x,y-1)
      var b =  -4.f * chemB(x,y) + chemB(x+1,y) + chemB(x-1,y) + chemB(x,y+1) + chemB(x,y-1)
      a = chemA(x,y) + a * dtt * alphaA / (dx*dx)
      b = chemB(x,y) + b * dtt * alphaB / (dx*dx)
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)
    }
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      
      var a = chemA.next(x,y) + dtt * ra( chemA.next(x,y), chemB.next(x,y))
      var b = chemB.next(x,y) + dtt * rb( chemA.next(x,y), chemB.next(x,y))
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)

      val color = Vec3(0,1,0) * a + Vec3(.5f,.0f,.5f) * b
      set(x,y, color )
    }
    
    for( i <- ( 0 until w*h ) ){ chemA.set(i, chemA.next(i) ); chemB.set(i, chemB.next(i)) }
  }


  def multiplyKernel( f: Field2D, x:Int, y:Int, l: Array[Float] ) : Float = {

    var s = 1// math.sqrt(l.length).toInt //assume odd square kernel
    //s = s / 2
    var v = 0.f
    var c = 0
    for( j<-(-s to s); i<-(-s to s)){
      v += l(c) * f(x+i,y+j)
      c += 1
    }
    v
  }
}

class ChemField extends Field2D {
  val next = new Field2D
  override def allocate(w:Int,h:Int) = { super.allocate(w,h); next.allocate(w,h) }
}

object Kernel {
  def dx2 = List(0.f,0,0, 1,-2,1, 0,0,0 )
  def dy2 = List(0.f,1,0, 0,-2,0, 0,1,0 )
  def laplacian = Array(0.f,1,0, 1,-4,1, 0,1,0 )
  def edgeEnhance = List(0.f,0.f,0.f,-1.f,1.f,0.f,0.f,0.f,0.f)
  def edgeDetect = List(0.f,1.f,0.f,1.f,-4.f,1.f,0.f,1.f,0.f)
  def emboss = List(-2.f,-1.f,0.f,-1.f,1.f,1.f,0.f,1.f,2.f)
  def sharpen = List(0.f,0.f,0.f,0.f,0.f, 0.f,0.f,-1.f,0.f,0.f, 0.f,-1.f,5.f,-1.f,0.f, 0.f,0.f,-1.f,0.f,0.f, 0.f,0.f,0.f,0.f,0.f)
  def blur = List(0.f,0.f,0.f,0.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,1.f,1.f,1.f,0.f, 0.f,0.f,0.f,0.f,0.f)
  def gaus = List(.25f,.25f,.25f,.25f,-2.f,.25f,.25f,.25f,.25f)

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
//      Main.field.readImage( "input.png" )
    }
    //if( keyCode == KeyEvent.VK_F ) Main.field.alpha += .05f
    //if( keyCode == KeyEvent.VK_V ) Main.field.alpha -= .05f
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
      for( j<-(-1 to 1); i<-(-1 to 1)) Main.field.chemB.set(xx+i,yy+j,1.f) //println( "("+xx+", "+ yy+" ) : " + Main.field(xx,yy) );
    }
  }

}
