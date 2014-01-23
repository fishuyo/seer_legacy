
package com.fishuyo.seer
package io

import de.sciss.osc._
import Implicits._

import scala.collection.mutable.Map

object OSC{

	// OSC recv config
  var cfg = UDP.Config()
  var rcv = UDP.Receiver(cfg) 
  var port = 8000

  // OSC send config
  var ccfg = UDP.Config()  
  ccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()
  var out = UDP.Client( localhost -> 8001, ccfg )

  // callback functions
	var callbacks = Map[String,(Any*)=>Unit]()

	// dummy function
	def f(s:String)(v:Any*) = {println(s)}


	/** Clear all callback functions */
	def clear() = callbacks.clear()

	/** Bind new callback function to OSC address */
	def bind( s:String, f:(Any*)=>Unit) = callbacks += s -> f

	/** Start listening for OSC message on given port */
	def listen(port:Int=8000){

		cfg = UDP.Config()
	  cfg.localPort = port  // 0x53 0x4F or 'SO'
	  rcv = UDP.Receiver( cfg )
	  // val sync = new AnyRef

	  //rcv.dump( Dump.Both )
	  // rcv.action = {
	  //   case (Message( name, vals @ _* ), _) =>	    
	  //     callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Any]])

	  //   //case (Message( name, v1:Float ), _) =>
	  //   //  callbacks.getOrElse(name, (v:Float)=>{println(name)} )(v1)
	     
	  //   case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  // }
	  rcv.action = {

      case (b:Bundle, _) => handleBundle(b)
      case (m:Message, _) => handleMessage(m)
       
      case (p, addr) => println( "Ignoring: " + p + " from " + addr )
    }
	  rcv.connect()
	}

	def handleBundle(bundle:Bundle){
    bundle match {
      case Bundle(t, msgs @ _*) =>
        msgs.foreach{
          case b:Bundle => handleBundle(b)
          case m:Message => handleMessage(m)
          case _ => println("unhandled object in bundle")
        }
      case _ => println("bad bundle")
    }
  }

  def handleMessage(msg:Message){
    msg match {
    	case Message( name, vals @ _* ) => callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Any]])
      case _ => println( "Ignoring: " + msg )
    }
  }

	/** close receiver and sender ports */
	def disconnect(){
		rcv.close
		out.close
	}

	/** Set send ip and port */
	def connect(ip:String = "localhost", port:Int=8000) = {
    out.close
    out = UDP.Client( ip -> port, ccfg )
    out.channel.socket.setBroadcast(true)
    out.connect                         
	}

	/** Send OSC message to prviously connected ip */
	def send(address:String, value:Any){
		value match{
			case v:Float => out ! Message(address,v)
			case v:Double => out ! Message(address,v)
			case v:Int => out ! Message(address,v)
			case v:Boolean => out ! Message(address,v)
			case v:String => out ! Message(address,v)
		}
	}

	def send(address:String, f1:Float, f2:Float){ out ! Message(address,f1,f2) }
	def send(address:String, f1:Float, f2:Float, f3:Float){ out ! Message(address,f1,f2,f3) }
	def send(address:String, f1:Float, f2:Float, f3:Float, f4:Float){ out ! Message(address,f1,f2,f3,f4) }

	/** Connect and send message */
	def send(ip:String = "localhost", port:Int=8000, address:String, value:Any){
		connect(ip,port)
		send(address,value)                        
	}
}


