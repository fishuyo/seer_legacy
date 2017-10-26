
package com.fishuyo.seer
package hid

import actor._

import spire.math.UByte

import org.hid4java._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import collection.mutable.HashMap

import scala.language.dynamics

sealed trait DeviceElement { def name:String }
case class Button(name:String, pin:Int, value:Int) extends DeviceElement
case class ButtonEx(name:String, pin:Int, value:Int) extends DeviceElement //exclusive value
case class Analog(name:String, pin:Int) extends DeviceElement
case class AnalogSigned(name:String, pin:Int) extends DeviceElement

sealed trait DeviceType
case object Default extends DeviceType 
sealed trait Joystick extends DeviceType 
case object AnalogJoystick extends Joystick
case object DualAnalogJoystick extends Joystick 


object Device {
  val registeredDevices = HashMap[String, (HidDevice) => Device]()
  
  register("PLAYSTATION(R)3 Controller", new PS3Controller(_))
  register("Joy-Con (L)", new JoyconL(_))
  register("Joy-Con (R)", new JoyconR(_))

  def apply(device:HidDevice) = registeredDevices.getOrElse(device.getProduct, new UnknownDevice(_))(device)

  // device.getProduct match {
  //   case "PLAYSTATION(R)3 Controller" => new PS3Controller(device)
  //   case "Joy-Con (L)" => new JoyconL(device)
  //   case "Joy-Con (R)" => new JoyconR(device)
  //   case _ => new UnknownDevice(device)
  // }

  // mechanism to register and implement devices at runtime..
  // def register(d:Device) = registeredDevices(d.name)
  def register(name:String, construct:(HidDevice)=>Device) = registeredDevices(name) = construct
}

abstract class Device(val device:HidDevice) extends Dynamic{
  
  // var device:Option[HidDevice] = None
  val elements:List[DeviceElement]// = List()
  val bufferSize = 1024
  val name = ""
  val deviceType:DeviceType = Default

  var id = 0

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  val byteStreamSource = Source.unfoldResource[Array[Byte],(Array[Byte],HidDevice)](
    () => { device.open; (new Array[Byte](bufferSize), device) },
    { case (b,d) => val len = d.read(b); if(len > 0) Some(b.take(len)) else None },
    { case (b,d) => d.close }
  )
  var broadcastBytes: Source[Array[Byte],akka.NotUsed] = _
  // var sources: Map[String,Source[Float,akka.NotUsed]] = _


  // var init = (d:Device)=>{}

  def open() = {
    // val devices = DeviceManager.getDevices(product)
    // if(devices.length > id){
      // println(s"$product ($id) connected.")
      // device = Some(devices(id))
       
      broadcastBytes = byteStreamSource.toMat(BroadcastHub.sink)(Keep.right).run().buffer(1,OverflowStrategy.dropHead)
      // broadcastBytes.runWith(Sink.ignore)

      // init(this)
    // }
  }

  def debugPrint() = if(broadcastBytes != null) broadcastBytes.runForeach(msg => println(msg.mkString(" ")))

  def close() = {
    // if(device.isDefined && device.get == d){
      device.close
      // println(s"$product ($id) disconnected.")
      // device = None
    // }
  }


  def destutter = Flow[Float].statefulMapConcat(() => {
    var last:Float = 0f
    elem =>
      if (elem != last) { last = elem; List(elem) }
      else Nil
  })

  def getSources() = {
    if(broadcastBytes == null) open()

    elements.map { 
      case Button(name,pin,value) =>
        name -> broadcastBytes.map { case bytes => 
          if ((UByte(bytes(pin)) & UByte(value)) > UByte(0)) 1.0f else 0.0f
        }.via(destutter)
      case ButtonEx(name,pin,value) =>
        name -> broadcastBytes.map { case bytes => 
          if (UByte(bytes(pin)) == UByte(value)) 1.0f else 0.0f
        }.via(destutter)
      case Analog(name,pin) =>
        name -> broadcastBytes.map { case bytes => 
          UByte(bytes(pin)).toFloat / 255
        }.via(destutter)
      case AnalogSigned(name,pin) =>
        name -> broadcastBytes.map { case bytes => 
          bytes(pin).toFloat / 128
        }.via(destutter)
    }.toMap
  }

  def selectDynamic(name:String) = getSources()(name)

  // def read() = {
  //   device.get.open
  //   val bytes = new Array[Byte](1024)
  //   var len = device.get.read(bytes)
  //   if(len > 0){
  //     println(bytes.take(len).mkString(" "))

  //     elements.foreach { 
  //       case Button(name,pin,value) =>
  //         println( name + ": " + ((UByte(bytes(pin)) & UByte(value)) > UByte(0)) )
  //       case ButtonEx(name,pin,value) =>
  //         println( name + ": " + (UByte(bytes(pin)) == UByte(value)) )
  //       case Analog(name,pin) =>
  //         println( name + ": " + UByte(bytes(pin)) )
  //       case AnalogSigned(name,pin) =>
  //         println( name + ": " + bytes(pin) )
  //     }

  //     len = device.get.read(bytes)
  //   }
  //   device.get.close
  // }

}



