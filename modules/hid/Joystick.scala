
package com.fishuyo.seer
package hid

import spire.math.UByte

case class Button(name:String, pin:Int, value:Int)
case class Analog(name:String, pin:Int)
case class AnalogS(name:String, pin:Int)


class Joystick(product:String){

  val device = DeviceManager.getDevices(product).head
  read()

  def read() = {
    device.open
    val bytes = new Array[Byte](1024)
    var len = device.read(bytes)
    while(len > 0){
      println(bytes.take(len).mkString(" "))

      PS3Controller.descriptor.foreach { 
        case Button(name,pin,value) =>
          println( name + ": " + ((UByte(bytes(pin)) & UByte(value)) > UByte(0)) )
        case Analog(name,pin) =>
          println( name + ": " + UByte(bytes(pin)) )
        case AnalogS(name,pin) =>
          println( name + ": " + bytes(pin) )
      }

      len = device.read(bytes)
    }
    device.close
  }

}


object PS3Controller {
  val product = "PLAYSTATION(R)3 Controller"

  val descriptor = List(
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
    AnalogS("accX", 42),
    AnalogS("accY", 44),
    AnalogS("accZ", 46)
  )


}