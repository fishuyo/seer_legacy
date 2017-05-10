
package com.fishuyo.seer
package hid

import actor._

import spire.math.UByte

import org.hid4java._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

abstract class DeviceElement
case class Button(name:String, pin:Int, value:Int) extends DeviceElement
case class Analog(name:String, pin:Int) extends DeviceElement
case class AnalogSigned(name:String, pin:Int) extends DeviceElement


class Device(product:String, id:Int){
  
  var elements:List[DeviceElement] = List()
  var device:Option[HidDevice] = None
  
  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  val onConnectionEvent = DeviceManager.connectionEventStream
                                     .filter( (e) => e.device.getProduct == product)
                                     .runWith(Sink.foreach{
                                        case DeviceConnectionEvent(d,"attached") => connect()
                                        case DeviceConnectionEvent(d,"detached") => disconnect(d)
                                      })


  val byteStream = Source.unfoldResource[Array[Byte],(Array[Byte],HidDevice)](
    () => { device.get.open; (new Array[Byte](1024), device.get) },
    { case (b,d) => val len = d.read(b); if(len > 0) Some(b.take(len)) else None },
    { case (b,d) => d.close }
  )
  var broadcastBytes: Source[Array[Byte],akka.NotUsed] = _
  var sources: Map[String,Source[Float,akka.NotUsed]] = _

  // connect()

  def connect() = {
    val devices = DeviceManager.getDevices(product)
    if(devices.length > id){
      println(s"$product ($id) connected.")
      device = Some(devices(id))
      // read()
      // byteStream.runWith(Sink.foreach( (b) => println(b.mkString(" ")) ))
      // // A simple producer that publishes a new "message" every second
      // val producer = Source.tick(1.second, 1.second, "New message")
       

      broadcastBytes = byteStream.toMat(BroadcastHub.sink)(Keep.right).run() 
      broadcastBytes.runWith(Sink.ignore)
      // broadcastBytes.runForeach(msg => println(msg.mkString(" ")))

      println(elements)
      // make sources from elements
      sources = elements.map { 
        case Button(name,pin,value) =>
          name -> broadcastBytes.map { case bytes => 
            if ((UByte(bytes(pin)) & UByte(value)) > UByte(0)) 1.0f else 0.0f
          }
        case Analog(name,pin) =>
          name -> broadcastBytes.map { case bytes => 
            UByte(bytes(pin)).toFloat / 255
          }
        case AnalogSigned(name,pin) =>
          name -> broadcastBytes.map { case bytes => 
            bytes(pin).toFloat / 128
          }
      }.toMap
    }
  }
  def disconnect(d:HidDevice) = {
    if(device.isDefined && device.get == d){
      println(s"$product ($id) disconnected.")
      device = None
    }
  }

  def read() = {
    device.get.open
    val bytes = new Array[Byte](1024)
    var len = device.get.read(bytes)
    if(len > 0){
      println(bytes.take(len).mkString(" "))

      elements.foreach { 
        case Button(name,pin,value) =>
          println( name + ": " + ((UByte(bytes(pin)) & UByte(value)) > UByte(0)) )
        case Analog(name,pin) =>
          println( name + ": " + UByte(bytes(pin)) )
        case AnalogSigned(name,pin) =>
          println( name + ": " + bytes(pin) )
      }

      len = device.get.read(bytes)
    }
    device.get.close
  }

}


class PS3Controller(id:Int) extends Device("PLAYSTATION(R)3 Controller", id) {

  elements = List(
    Analog("leftX", 6),
    Analog("leftY", 7),
    Analog("rightX", 8),
    Analog("rightY", 9),
    Button("L2", 3, 1),
    Button("R2", 3, 2),
    Button("L1", 3, 4),
    Button("R1", 3, 8),
    Button("Triangle", 3, 16),
    Button("Circle", 3, 32),
    Button("X", 3, 64),
    Button("Square", 3, 128),
    AnalogSigned("accX", 42),
    AnalogSigned("accY", 44),
    AnalogSigned("accZ", 46)
  )


}