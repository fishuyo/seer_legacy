
package com.fishuyo
package examples.dronefractal

import graphics._
import maths._
import io._
import io.drone._
import ray._
import media._

import de.sciss.osc._
import com.codeminders.ardrone._

import java.awt.event._


object Main extends App {

  import Implicits._

  val osc = new DroneOSCControl( 10001 )

  val n = 200;
  val field = new NewtonField
  field.allocate(n,n)
  field.sstep(0);

  GLScene.push( field );

  val win = new GLRenderWindow
  win.addKeyMouseListener( DroneKeyboardControl )
  win.addKeyMouseListener( Input )

}

object Input extends KeyMouseListener {

  override def keyPressed( e: KeyEvent ) = {
    val k = e.getKeyCode
    if( k == KeyEvent.VK_M ){
      Main.field.go = !Main.field.go
      
      //Main.win.capture match{ 
      //  case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
      //  case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      //}
    }
  }
}

class NewtonField extends Vec3Field2D {
  
  var next = new Vec3Field2D //FloatBuffer = _ //Array[Float] = _
  var c = Vec3( 0, 0, 0 )
  var time = 0.f
  var rot = 1.f

  override def sstep(dt:Float) = {

     time += dt
     if( time > 1.f ){
      Drone.move(0,0,0,rot)
      rot *= -1.f
      time = 0.f
     }

    if( next.w == 0 ) next.allocate( w,h )

    import scala.util.Random._
    c += Vec3( nextFloat *.1f - .05f, nextFloat * .1f - .05f, 0 )

    for( y <- (0 until h); x <- (0 until w)){
      var p = Vec3( x * 11.f / w - 5.5f, y * 11.f / h - 5.5f, 0);
      var o = Vec3(0) + p
      
      var t = 0;

      val cm = (u:Vec3,v:Vec3) => { Vec3( u.x*v.x-u.y*v.y, u.x*v.y+v.x*u.y, 0) }
      val cd = (u:Vec3,v:Vec3) => { Vec3( (u.x*v.x+u.y*v.y)/(v.x*v.x+v.y*v.y), (u.y*v.x-u.x*v.y)/(v.x*v.x+v.y*v.y), 0) }
      
      val f = (p:Vec3) => { cm(p,cm(p,cm(p,cm(p,p)))) - Vec3(1,0,0) } //cm(p,p)*6 + p*11 + Vec3(6,0,0) }
      val df = (p:Vec3) => { cm(p,cm(p,p))*5 + cm(p,c) } //- p*12 + Vec3(11,0,0) }

      while( f(p).mag > 1e-3 && t < 100 ){
        p -= cd( f(p), df(p) )
        t += 1
      }
      var v = t.toFloat
      //var v = t.toFloat - (math.log(math.log(p.mag))/math.log(2.f)).toFloat
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
    var i = math.abs(2*x*colors.length-1).toInt - 1
    if( i < 0 ) i = 0
    if( i >= colors.length-1) i = colors.length-2
    val c1 = colors(i)
    val c2 = colors(i+1)
    c1 * (1-x) + c2 * (x)

  }

}

