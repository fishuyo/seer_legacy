
package com.fishuyo
package examples.droneOSC

//import graphics._
import maths._
import io._
import io.drone._
import ray._
import media._

import de.sciss.osc._
import com.codeminders.ardrone._

import java.awt._
import javax.swing._
import java.awt.event._


object Main extends App {

  import Implicits._

  Drone.ip = "192.168.3.1"
  val osc = new DroneOSCControl( 10001 )

  //val track = new TransTrack
  
  val frame = new JFrame("droneOSC")
  frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE )
  frame.setSize( 300, 200 )
  frame.setVisible(true)
  frame.addKeyListener( DroneKeyboardControl )
  frame.addKeyListener( Input )

  while( true ){


  }

  //val win = new GLRenderWindow
  //win.addKeyMouseListener( DroneKeyboardControl )
  //win.addKeyMouseListener( Input )

}

class TransTrack {

  val d2r = math.Pi/180.f
  val cfg         = UDP.Config()
  cfg.localPort   = 10000  // 0x53 0x4F or 'SO'
  val rcv         = UDP.Receiver( cfg )
  val sync = new AnyRef
  
  var flying = false; 
  
  println( "TransTrack listening on UDP " + cfg.localPort )

  //rcv.dump( Dump.Both )
  rcv.action = {
  
    case (Message( name, x:Float,y:Float,z:Float ), _) =>
      if( Drone.ready ){
        if( name.startsWith("/tracker2")){
          //println(x + " " + y + " " + z)
          if( y > .2f && !flying ){
             println("TAKEOFF!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
             Drone.takeOff
             flying = true
          }
          if( y < -.55f && flying ){
             Drone.land
             flying = false
          }
        }
      }

    /*case (Message( name, x:Float,y:Float,z:Float,w:Float ), _) =>
      if( Drone.ready ){      
        if( name.startsWith("/tracker1")){
          //println(".")
          Drone.step( Vec3(x,y,z), w )
        }
      }*/

    case (Message( name, v @ _* ), _) =>
      println( "Received message '" + name + "'" )
   
      if( name == "/takeoff" ) sync.synchronized( sync.notifyAll() )
      if( name == "/quit" ) sync.synchronized( sync.notifyAll() )
   
    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
  }

  rcv.connect()
  //sync.synchronized( sync.wait() )
 
}

object Input extends KeyMouseListener {

  override def keyPressed( e: KeyEvent ) = {
    val k = e.getKeyCode
    if( k == KeyEvent.VK_M ){
      //Main.field.go = !Main.field.go
      
      //Main.win.capture match{ 
      //  case v:MediaWriter => v.close(); Main.win.capture = null; Main.field.go = false;
      //  case _ => Main.win.capture = new MediaWriter; Main.field.go = true;
      //}
    }
  }
}

