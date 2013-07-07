
package com.fishuyo
package io

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

import java.io._

// import scala.actors.Actor
// import scala.actors.Actor._
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

import collection.mutable.ListBuffer

case class Receive

object TransTrack {
  val system = ActorSystem("Network")
  val actor = system.actorOf(Props( new TransTrack(7008)), name = "TransTrack")
}

class TransTrack(val port:Int=7008) extends Actor{

  type Vec3 = (Float,Float,Float)
  type Quat = (Float,Float,Float,Float)
  type Callback = (Int, Array[Float]) => Any

  val rcallbacks = new ListBuffer[Callback]()
  val bcallbacks = new ListBuffer[Callback]()
  val pcallbacks = new ListBuffer[Callback]()

  var debug = false
  def setDebug(b:Boolean) = debug = b

  var sock:DatagramSocket = _
  val buf = new Array[Byte](2048)
  val packet = new DatagramPacket( buf, 2048 )

  override def preStart(){
    sock = new DatagramSocket(port)
    println( "Listening on port " + port + " ..." )
    self ! Receive
  }
  
  def receive = {    
    case Receive =>
    	try{
    		sock.receive( packet )
  
	      val data = new DataInputStream( new ByteArrayInputStream(packet.getData)).readLine
	      if( debug ) println(data)
	      
	      data.split(" ") match {
	        case Array("point",i,x,y,z,a,b,c,w) => pointEvent( i.toInt, Array(x.toFloat, y.toFloat, z.toFloat) )
	        case Array("rigid_body",i,x,y,z,a,b,c,w) => rigidEvent( i.toInt, Array(x.toFloat, y.toFloat, z.toFloat, a.toFloat, b.toFloat, c.toFloat, w.toFloat) )
	        case Array("bone",i,x,y,z,a,b,c,w) => boneEvent( i.toInt, Array(x.toFloat, y.toFloat, z.toFloat, a.toFloat, b.toFloat, c.toFloat, w.toFloat) )
	        case _ => if(debug) println("non tracker data received from: " + packet.getAddress)
	      }
      }catch {
      	case e:Exception => println(e)
      }
      self ! Receive
  }

  def rigidEvent( body:Int, pose:Array[Float] ) = rcallbacks.foreach( _(body,pose) )
  def boneEvent( body:Int, pose:Array[Float] ) = bcallbacks.foreach( _(body,pose) )
  def pointEvent( body:Int, pose:Array[Float] ) = pcallbacks.foreach( _(body,pose) )

  def bind( event:String, f: Callback ) = {
  	event match {
  		case "rigid_body" => rcallbacks += f
  		case "bone" => bcallbacks += f
  		case "point" => pcallbacks += f
  		case _ => rcallbacks += f
  	}
  }

  def clear() = { rcallbacks.clear; bcallbacks.clear; pcallbacks.clear }

}

