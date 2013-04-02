
package com.fishuyo
package io

import de.sciss.osc._
import scala.collection.mutable.Map

object OSC{

	var callbacks = Map[String,(Float*)=>Unit]()
	//var callbacks2 = Map[String,(Float,Float)=>Unit]()

	def clear() = callbacks.clear()
	def bind( s:String, f:(Float*)=>Unit) = callbacks += s -> f
	//def bind( s:String, f:(Float,Float)=>Unit) = callbacks2 += s -> f

	def listen(port:Int=8000){
		val cfg         = UDP.Config()
	  cfg.localPort   = port  // 0x53 0x4F or 'SO'
	  val rcv         = UDP.Receiver( cfg )
	  val sync = new AnyRef

	  def f(s:String)(v:Float*) = {println(s)}
	  //rcv.dump( Dump.Both )
	  rcv.action = {
	  	//case (Message( "/colors", i1:Int, i2:Int, i3:Int), _) => callbacks.getOrElse("/color",f("/color")_)(Seq[Float](i1/255.f,i2/255.f,i3/255.f):_*)
	    case (Message( name, vals @ _* ), _) =>
	      callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Float]]:_*)

	    //case (Message( name, v1:Float ), _) =>
	    //  callbacks.getOrElse(name, (v:Float)=>{println(name)} )(v1)
	     
	    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  }
	  rcv.connect()
	}
}