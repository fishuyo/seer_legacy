
package com.fishuyo
package io
package drone

import maths._

import com.codeminders.ardrone._

import java.net._
import java.awt.event._
import de.sciss.osc._

object Drone {

  val d2r = math.Pi / 180.f

  var ready = false
  var flying = false
  var drone : ARDrone = _
  var ip = "192.168.1.1"
  var pos = Vec3(0)
  var vel = Vec3(0)
  var dest = Vec3(0)
  var yaw = 0.f
  var destYaw = 0.f
  var dwThresh = 20.f
  var dpThresh = .33f
  var speed = .1f
  var vSpeed = .1f
  var rotSpeed = .9f
  //init()

  def init( ) = {
    try {
      drone = new ARDrone( InetAddress.getByName(ip) )
      println("Connecting...")
      connect
      println("Drone Connected.")
      clearEmergency
      drone.waitForReady(5000)
      println("Drone connected and ready!")
      trim
      ready = true

    } catch {
      case e: Throwable => e.printStackTrace
    }
  }

  def connect() = drone.connect
  def disconnect() = { drone.disconnect; Thread.sleep(1000); drone = null }
  def clearEmergency() = drone.clearEmergencySignal
  def trim() = drone.trim
  def takeOff() = { drone.takeOff; flying = true;}
  def land() = {drone.land; flying = false;}

  def playLed = drone.playLED(1, 10, 5 )

  def toggleFly = {
  if( flying ) drone.land
  else drone.takeOff
  flying = !flying
  println( "fly: " + flying )
  }

  def move( lr: Float, fb: Float, ud: Float, r: Float ) = drone.move(lr,fb,ud,r)
  def moveTo( x:Float,y:Float,z:Float,w:Float ) = {
    dest = Vec3(x,y,z)
    destYaw = w
    while( destYaw < -180.f ) destYaw += 360.f
    while( destYaw > 180.f ) destYaw -= 360.f
  }
  def step(p:Vec3,w:Float ):Any = {

    if( !flying ) return null
    vel = p - pos

    pos = p;
    yaw = w; while( yaw < -180.f ) yaw += 360.f; while( yaw > 180.f) yaw -= 360.f

    var dw = destYaw - yaw
    if( dw > 180.f ) dw -= 360.f 
    if( dw < -180.f ) dw += 360.f 
    if( math.abs(dw) > dwThresh ){
      var r = rotSpeed //.3f
      if( dw < 0.f) r = -rotSpeed //.3f
      Drone.move(0,0,0,r)
      return null
    }
    //println( "diff in yaw: " + dw )

    val dir = (dest - (pos+vel))
    val dp = dir.mag
    val cos = math.cos(w*d2r)
    val sin = math.sin(w*d2r)
    val d = (dest - pos).normalize
    var ud = d.y * vSpeed //.1f
    var fb = -d.x*cos - d.z*sin  //d.x*cos - d.z*sin
    var lr = -d.x*sin + d.z*cos  //-d.x*sin - d.z*cos
    fb = fb * speed //.1f
    lr = lr * speed //.1f
    //println("dp: " + dp + "  "+lr+" "+fb+" "+ud)
    if( dp  > dpThresh ){
      Drone.move(lr.toFloat,fb.toFloat,ud,0)
    }else {
      Drone.hover
    }
    //println( dp )
    null
  }

  def hover = drone.hover

}

object DroneKeyboardControl extends KeyMouseListener {


  override def keyPressed( e: KeyEvent ) = {
    var lr = 0.f
    var fb = 0.f
    var ud = 0.f
    var rot = 0.f
    val keyCode = e.getKeyCode()
    if( keyCode == KeyEvent.VK_ENTER ){

      Drone.toggleFly

      //import util.Random.nextFloat
      //Main.field.c += Vec3( .1f*(nextFloat - .5f), .1f*(nextFloat - .5f), 0 )
      //Main.field.sstep(0)
    }
    if( keyCode == KeyEvent.VK_ESCAPE ) Drone.land
    if( keyCode == KeyEvent.VK_P ) Drone.init
    if( keyCode == KeyEvent.VK_F) lr += -.5f
    if( keyCode == KeyEvent.VK_H) lr += .5f
    if( keyCode == KeyEvent.VK_T) fb += -.5f
    if( keyCode == KeyEvent.VK_G) fb += .5f

    if( keyCode == KeyEvent.VK_J) rot += -.5f
    if( keyCode == KeyEvent.VK_L) rot += .5f
    if( keyCode == KeyEvent.VK_I) ud += .5f
    if( keyCode == KeyEvent.VK_K) ud += -.5f

    if( lr != 0.f || fb != 0.f || rot != 0.f || ud != 0.f ) Drone.move( lr, fb, ud, rot )

    if( keyCode == KeyEvent.VK_SPACE ) Drone.hover

    if( keyCode == KeyEvent.VK_Y ) Drone.takeOff

    if( keyCode == KeyEvent.VK_M ){
      //Main.field.go = !Main.field.go

      //Main.win.capture match{
      //  case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
      //  case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      //}
    }
  }
  override def keyReleased( e: KeyEvent ) = {
    val k = e.getKeyCode()
    if( Drone.drone != null )
    if( k == KeyEvent.VK_SPACE)
    Drone.move(0,0,0,0)
  }
  override def keyTyped( e: KeyEvent ) = {}
}

class DroneOSCControl( val port:Int) {

  val cfg = UDP.Config()
  cfg.localPort = port
  val rcv = UDP.Receiver( cfg )

  //rcv.dump( Dump.Both )
  rcv.action = {
    case(Message( "/drone/connect", _ @ _*), _) => Drone.init
    case(Message( "/drone/disconnect", _ @ _*), _) => Drone.disconnect
    case(Message( "/drone/clearEmergency", _ @ _*), _) => Drone.clearEmergency
    case(Message( "/drone/takeoff", _ @ _*), _) => println("TAKEOFF!"); Drone.takeOff
    case(Message( "/drone/land", _ @ _*), _) => println("LAND!"); Drone.land
    case(Message( "/drone/move", a:Float,b:Float,c:Float,d:Float ), _) => println("MOVE: " + a + " " + b + " " + c + " " + d); Drone.move(a,b,c,d)
    case(Message( "/drone/moveto", x:Float,y:Float,z:Float,w:Float ), _) => Drone.moveTo(x,y,z,w); //println("MOVE_TO: "+x+" "+y+" "+z+" "+w)
    case(Message( "/drone/setposh", x:Float,y:Float,z:Float,w:Float ), _) => Drone.step(Vec3(x,y,z),w); //println("pos: "+x+" "+y+" "+z+" "+w)
    case(Message( "/drone/setSpeed", s:Float ), _) => Drone.speed = s; if(s<0.f) Drone.speed=0.f; if(s>1.f) Drone.speed=1.f;
    case(Message( "/drone/setVSpeed", s:Float ), _) => Drone.vSpeed = s; if(s<0.f) Drone.vSpeed=0.f; if(s>1.f) Drone.vSpeed=1.f;
    case(Message( "/drone/setRotSpeed", s:Float ), _) => Drone.rotSpeed = s; if(s<0.f) Drone.rotSpeed=0.f; if(s>1.f) Drone.rotSpeed=1.f;
    case(Message( "/drone/setThresh", s:Float ), _) => Drone.dpThresh = s; if(s<0.f) Drone.dpThresh=0.f; if(s>1.f) Drone.dpThresh=1.f;
    case(Message( "/drone/setRotThresh", s:Float ), _) => Drone.dwThresh = s; if(s<0.f) Drone.dwThresh=0.f; if(s>180.f) Drone.dwThresh=180.f;
    case(Message( "/drone/hover", _ @ _*), _) => println("HOVEr!"); Drone.hover
    case(Message( name, f @ _*), _) => null


    case( p, addr ) => null

  }
  rcv.connect()
  println( "DroneOSCControl started on port " + port )

}
