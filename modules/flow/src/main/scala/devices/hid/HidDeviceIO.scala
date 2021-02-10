package seer
package flow
package hid

// import com.fishuyo.seer.actor._

import spire.math.UByte

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import collection.mutable.HashMap

/**
  * SourceElement trait for case classes representing 
  * input elements/types of an hid device
  */
sealed trait SourceElement { def name:String }
case class Button(name:String, index:Int, mask:Int) extends SourceElement //rename Bitmask
case class ButtonEx(name:String, index:Int, value:Int) extends SourceElement //rename Value
case class Analog(name:String, index:Int) extends SourceElement
case class AnalogSigned(name:String, index:Int) extends SourceElement
// ButtonBitMask(index = 0, mask = 0xFF)
// ButtonEquals(index = 1, value = 0x128)
// RawByteValue(index = 1)
// Analog
// case class MapFunction[T](index:Int, func: Byte=>T )


sealed trait SinkElement { def name:String }
case class Bitmask(name:String, index:Int, mask:Int ) extends SinkElement
// case class Bit(name:String, index:Int) extends SinkElement
case class BByte(name:String, index:Int) extends SinkElement
case class FFloat(name:String, index:Int) extends SinkElement

sealed trait DeviceType
case object Unknown extends DeviceType 
sealed trait Joystick extends DeviceType 
case object AnalogJoystick extends Joystick
case object DualAnalogJoystick extends Joystick 

// Move name into body -- override in impl instead, of in constructor..
// pass name, type, index to DM getDeviceConnection...decide there
abstract class HidDeviceIO(val index:Int) extends IO {

  import concurrent.ExecutionContext.Implicits.global
  
  val sourceElements:List[SourceElement]
  val sinkElements:List[SinkElement] = List()
  val outputBuffer:Array[Byte] = Array[Byte]()
  
  lazy val name:Option[String] = None
  lazy val deviceType:DeviceType = Unknown

  val device:HidDeviceConnection = DeviceManager.getDeviceConnection(name, deviceType, index)


  override def sources:Map[String,Source[Any,akka.NotUsed]] = {
    sourceElements.map { 
      case Button(name,i,mask) =>
        name -> device.source.map { case bytes => 
          if ((UByte(bytes(i)) & UByte(mask)) > UByte(0)) 1.0f else 0.0f
        }.via(destutter)
      case ButtonEx(name,i,value) =>
        name -> device.source.map { case bytes => 
          if (UByte(bytes(i)) == UByte(value)) 1.0f else 0.0f
        }.via(destutter)
      case Analog(name,i) =>
        name -> device.source.map { case bytes => 
          UByte(bytes(i)).toFloat / 255
        }//.via(destutter)
      case AnalogSigned(name,i) =>
        name -> device.source.map { case bytes => 
          bytes(i).toFloat / 128
        }//.via(destutter)
    }.toMap
  }

  override def sinks:Map[String,Sink[Any,akka.NotUsed]] = {
    sinkElements.map { case e =>
      e.name -> Flow[Any].map((a:Any) => { //(e,_)).to(outputStream)
        a match {
          case f:Float =>
            e match {
              case Bitmask(name, idx, mask) => 
                val b = outputBuffer(idx)
                if(f == 1f) outputBuffer(idx) = (b | mask).toByte
                else outputBuffer(idx) = (b & ~mask).toByte
              case BByte(name, idx) => outputBuffer(idx) = f.toByte
              case FFloat(name, idx) => outputBuffer(idx) = (f*255).toByte
            }
          case _ =>
        }

        outputBuffer
      }).to(device.sink)
    }.toMap
  }

}



