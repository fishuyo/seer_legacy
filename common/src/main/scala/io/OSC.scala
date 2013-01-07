
package com.fishuyo
package io

import de.sciss.osc._
import scala.collection.mutable.Map

object OSC{

	var callbacks = Map[String,(Float)=>Unit]()
	var callbacks2 = Map[String,(Float,Float)=>Unit]()

	def bind( s:String, f:(Float)=>Unit) = callbacks += s -> f
	def bind( s:String, f:(Float,Float)=>Unit) = callbacks2 += s -> f

	def listen(port:Int=8000){
		val cfg         = UDP.Config()
	  cfg.localPort   = port  // 0x53 0x4F or 'SO'
	  val rcv         = UDP.Receiver( cfg )
	  val sync = new AnyRef

	  //rcv.dump( Dump.Both )
	  rcv.action = {
	    case (Message( name, v1:Float, v2:Float ), _) =>
	      callbacks2.getOrElse(name, (v:Float,w:Float)=>{println(name)} )(v1,v2)

	    case (Message( name, v1:Float ), _) =>
	      callbacks.getOrElse(name, (v:Float)=>{println(name)} )(v1)
	     
	    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  }
	  rcv.connect()
	}
}