
package com.fishuyo
package rangosc

import graphics._
import maths._
import ray._

import de.sciss.osc._

object Main extends App {

  var port_in = 22222
  var port_out = 13000
  var max_time = 10000

  var mode_count = 4
  var rythmic_mode = false

  import Implicits._

  println( """Listening on UDP port 22222""" )
  val ccfg = UDP.Config()  
  val cccfg = UDP.Config()  
  ccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()
  cccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()

  val c = UDP.Client( localhost -> port_out, ccfg )
  val cc = UDP.Client( localhost -> 26000, cccfg )

  c.connect()
  cc.connect()

  val cfg         = UDP.Config()
  cfg.localPort   = port_in  // 0x53 0x4F or 'SO'
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
  //sync.synchronized( sync.wait() )
  
  //GLScene.push( field );

  //val win = new GLRenderWindow
  //win.glcanvas.addMouseListener( Mouse )
  //win.glcanvas.addMouseMotionListener( Mouse )

  while(true){

    import util.Random._
    val l = 4
    val i = nextInt(l)
    val p = nextFloat

    if( rythmic_mode ){
      
      val t = nextInt(50) + 10
      for( k <- ( 0 until l )){ 
        println("recording loop " + k + " for " + t*100 + " ms")
        c ! Message( "/toggleRecord", k )
        Thread.sleep( t * 100 )
        c ! Message( "/toggleRecord", k )
        //Thread.sleep( t*100 )
      }
      Thread.sleep( 3 * t * 100 )
      max_time = 0

    }
    
    if( p < .001f ){
      println("sending external /play on 26000")
      cc ! Message( "/play" )
    }else if( p < .02f ){
      println("playonce loop " + i+4 )
      c ! Message( "/playOnce", i+4 )
    }
    
    if( p < .1f ){
      
      println( "reversing " + i )
      c ! Message( "/reverse", i )

    }else if( p < .3f ){
      
      val t = nextInt( 80 ) + 5
      println("recording loop " + i + " for " + t*100 + " ms")
      c ! Message( "/toggleRecord", i )
      Thread.sleep( t * 100 )
      c ! Message( "/toggleRecord", i )
      max_time -= t*100
    
    }else if( p < .5f ){

    } 

    val slep = 500
    Thread.sleep(slep)

    max_time -= slep
    if( max_time <= 0 ){
      max_time = nextInt(30000) + 15000
      mode_count -= 1
      if( mode_count <= 0 ){
        mode_count = nextInt(4) + 2
        rythmic_mode = !rythmic_mode
        println( "SWITCHING rythmic mode to " + rythmic_mode )
      }
      
      for( indx <- (0 until l)) c ! Message( "/stop", indx )

      mode_count
      println("Killing! next duration ~ " + max_time / 1000 + " s" )
    }
  }



}
