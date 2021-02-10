package seer
package flow

import de.sciss.osc._
import Implicits._

import java.net.SocketAddress

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

object OSC {
  type OSCHandler = PartialFunction[(Message,SocketAddress),Unit]
}

class OSCRecv extends OSCReceiver
trait OSCReceiver {
  import OSC._
  
  var client:SocketAddress = _
  
  // OSC recv config
  var cfg = UDP.Config()
  var rcv = UDP.Receiver(cfg) 

  // callback functions
  val handlers = new ListBuffer[OSCHandler]()

  /** Clear all callback functions */
  def clear() = handlers.clear()

  /** Bind new callback function to OSC address */
  def bind(f:OSCHandler) = handlers += f
  def unbind(f:OSCHandler) = handlers -= f

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
    rcv.connect
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
    handlers.foreach(_((msg,addr)))
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
  def connect(ip:String, port:Int) = {
    out.close
    out = UDP.Client( ip -> port, ccfg )
    out.channel.socket.setBroadcast(true)
    out.connect                         
  }

  /** Send OSC message to prviously connected ip */
  def send(msg:Message){
    // if( bundle ){ 
    //   messageBuffer += msg
    //   if( messageBuffer.length > maxBundleLength){ 
    //     endBundle()
    //     startBundle()
    //   }
    // } else{ 
      try { out ! msg }
      catch { case e:Exception => () }//println(e) }
    // }
  }

  def send(address:String, value:Any){
    val msg = value match{
      case v:Float => Message(address,v)
      case v:Seq[Float] => Message(address,v:_*)
      case v:Double => Message(address,v)
      case v:Int => Message(address,v)
      case v:Long => Message(address,v.toInt)
      case v:Boolean => Message(address,v)
      case v:String => Message(address,v)
    }
    send(msg)
  }

  def send(address:String, fs:Float*){ send(Message(address,fs:_*)) }
  // def send(address:String, f1:Float, f2:Float){ send(Message(address,f1,f2)) }
  // def send(address:String, f1:Float, f2:Float, f3:Float){ send(Message(address,f1,f2,f3)) }
  // def send(address:String, f1:Float, f2:Float, f3:Float, f4:Float){ send(Message(address,f1,f2,f3,f4)) }
  def send(address:String, s1:String, s2:String){ send(Message(address,s1,s2)) }

  /** Connect and send message */
  def send(ip:String, port:Int, address:String, value:Any){
    connect(ip,port)
    send(address,value)                        
  }

  // def startBundle(count:Int=30){ maxBundleLength = count; bundle = true }
  // def endBundle(){
  //   bundle = false
  //   if( messageBuffer.length > 0){
  //     // println("sending bundle size " + messageBuffer.length)
  //     out ! Bundle.now( messageBuffer : _* )
  //     messageBuffer.clear
  //   }
  // }
}
