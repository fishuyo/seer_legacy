
package com.fishuyo
package examples.osc

import graphics._
import maths._
import ray._

import de.sciss.osc._

object Main extends App {


  import Implicits._

  println( """
  Receiver test

  is waiting for an incoming message
  on UDP port 21327
  Send "/quit" to terminate.
  """ )

  val cfg         = UDP.Config()
  cfg.localPort   = 21327  // 0x53 0x4F or 'SO'
  val rcv         = UDP.Receiver( cfg )
  val sync = new AnyRef

  rcv.dump( Dump.Both )
  rcv.action = {
    case (Message( name, _ @ _* ), _) =>
      println( "Received message '" + name + "'" )
      if( name == "/quit" ) sync.synchronized( sync.notifyAll() )
    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
  }
  rcv.connect()
  sync.synchronized( sync.wait() )
  
  //GLScene.push( field );

  //val win = new GLRenderWindow
  //win.glcanvas.addMouseListener( Mouse )
  //win.glcanvas.addMouseMotionListener( Mouse )

}
