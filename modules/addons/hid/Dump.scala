
package com.fishuyo.seer
package hid


class Dump(name:String){

  val device = DeviceManager.getHidDevices(name).head
  read()

  def read() = {
    device.open
    val bytes = new Array[Byte](1024)
    var len = device.read(bytes)
    while(len > 0){
      println(bytes.take(len).mkString(" "))
      len = device.read(bytes)
    }
    device.close
  }

}