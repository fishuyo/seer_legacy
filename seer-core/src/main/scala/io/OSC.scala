
package com.fishuyo
package io

import de.sciss.osc._
import Implicits._

import scala.collection.mutable.Map

object OSC{

  var cfg = UDP.Config()
  var rcv = UDP.Receiver(cfg) 
  var port = 8000

  var ccfg = UDP.Config()  
  ccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()
  var out = UDP.Client( localhost -> 8001, ccfg )

	var callbacks = Map[String,(Any*)=>Unit]()
	//var callbacks2 = Map[String,(Float,Float)=>Unit]()

	def clear() = callbacks.clear()
	def bind( s:String, f:(Any*)=>Unit) = callbacks += s -> f
	//def bind( s:String, f:(Float,Float)=>Unit) = callbacks2 += s -> f

	def listen(port:Int=8000){
		cfg = UDP.Config()
	  cfg.localPort = port  // 0x53 0x4F or 'SO'
	  rcv = UDP.Receiver( cfg )
	  // val sync = new AnyRef

	  def f(s:String)(v:Any*) = {println(s)}
	  //rcv.dump( Dump.Both )
	  rcv.action = {
	    case (Message( name, vals @ _* ), _) =>	    
	      callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Any]])

	    //case (Message( name, v1:Float ), _) =>
	    //  callbacks.getOrElse(name, (v:Float)=>{println(name)} )(v1)
	     
	    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  }
	  rcv.connect()
	}

	def disconnect(){
		rcv.close
		out.close
	}

	def connect(ip:String = "localhost", port:Int=8000) = {
    out.close
    out = UDP.Client( ip -> port, ccfg )
    out.channel.socket.setBroadcast(true)
    out.connect                         
	}
	def send(address:String, value:Any){
		value match{
			case v:Float => out ! Message(address,v)
			case v:Double => out ! Message(address,v)
			case v:Int => out ! Message(address,v)
			case v:Boolean => out ! Message(address,v)
			case v:String => out ! Message(address,v)
		}
	}

	def send(ip:String = "localhost", port:Int=8000, address:String, value:Any){
		connect(ip,port)
		send(address,value)                        
	}
}


