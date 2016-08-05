
package com.fishuyo.seer
package io

import de.sciss.osc._
import Implicits._

import java.net.SocketAddress

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

object OSC extends OSCReceiver with OSCSender {
	override def disconnect(){
		rcv.close
		out.close
	}
}

class OSCRecv extends OSCReceiver
trait OSCReceiver {

  var client:SocketAddress = _
  
	// OSC recv config
  var cfg = UDP.Config()
  var rcv = UDP.Receiver(cfg) 

  // callback functions
	var callbacks = Map[String,(Any*)=>Unit]()
	val ghandlers = new ListBuffer[(String,Any*)=>Unit]()
	val phandlers = new ListBuffer[PartialFunction[Message,Unit]]()

	// dummy function
	def f(s:String)(v:Any*) = {} //println(s)}

	/** Clear all callback functions */
	def clear() = { callbacks.clear(); ghandlers.clear(); phandlers.clear() }

	/** Bind new callback function to OSC address */
	def bind( s:String, f:(Any*)=>Unit) = callbacks += s -> f

	def bind(f:(String,Any*)=>Unit) = ghandlers += f
	def bindp(f:PartialFunction[Message,Unit]) = phandlers += f

	/** Start listening for OSC message on given port */
	def listen(port:Int=8000){

		cfg = UDP.Config()
	  cfg.localPort = port  // 0x53 0x4F or 'SO'
	  rcv = UDP.Receiver( cfg )

	  rcv.action = {

      case (b:Bundle, addr) => handleBundle(b,addr)
      case (m:Message, addr) => handleMessage(m,addr)
       
      case (p, addr) => println( "Ignoring: " + p + " from " + addr )
    }
	  rcv.connect()
	}

	def handleBundle(bundle:Bundle, addr:SocketAddress){
    bundle match {
      case Bundle(t, msgs @ _*) =>
        msgs.foreach{
          case b:Bundle => handleBundle(b,addr)
          case m:Message => handleMessage(m,addr)
          case _ => println("unhandled object in bundle")
        }
      case _ => println("bad bundle")
    }
  }

  def handleMessage(msg:Message, addr:SocketAddress){
    client = addr
  	phandlers.foreach(_(msg))
    msg match {
    	case Message( name, vals @ _* ) =>
    		callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Any]])
    		ghandlers.foreach( _(name,vals.asInstanceOf[Seq[Any]]) )
      case _ => println( "Ignoring: " + msg )
    }
  }

	/** close receiver and sender ports */
	def disconnect(){
		rcv.close
	}
}

class OSCSend extends OSCSender
trait OSCSender {

	var bundle = false
	var maxBundleLength = 30
	var messageBuffer = new ListBuffer[Message]()

  // OSC send config
  var ccfg = UDP.Config()  
  ccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()
  var out = UDP.Client( localhost -> 8001, ccfg )

	/** close receiver and sender ports */
	def disconnect(){
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
	def send(msg:Message){
		if( bundle ){ 
			messageBuffer += msg
			if( messageBuffer.length > maxBundleLength){ 
				endBundle()
				startBundle()
			}
		} else out ! msg
	}

	def send(address:String, value:Any){
		val msg = value match{
			case v:Float => Message(address,v)
			case v:Double => Message(address,v)
			case v:Int => Message(address,v)
			case v:Long => Message(address,v.toInt)
			case v:Boolean => Message(address,v)
			case v:String => Message(address,v)
		}
		send(msg)
	}

	def send(address:String, f1:Float, f2:Float){ send(Message(address,f1,f2)) }
	def send(address:String, f1:Float, f2:Float, f3:Float){ send(Message(address,f1,f2,f3)) }
	def send(address:String, f1:Float, f2:Float, f3:Float, f4:Float){ send(Message(address,f1,f2,f3,f4)) }
	def send(address:String, s1:String, s2:String){ send(Message(address,s1,s2)) }

	/** Connect and send message */
	def send(ip:String = "localhost", port:Int=8000, address:String, value:Any){
		connect(ip,port)
		send(address,value)                        
	}

	def startBundle(count:Int=30){ maxBundleLength = count; bundle = true }
	def endBundle(){
		bundle = false
		if( messageBuffer.length > 0){
			// println("sending bundle size " + messageBuffer.length)
			out ! Bundle.now( messageBuffer : _* )
			messageBuffer.clear
		}
	}
}
