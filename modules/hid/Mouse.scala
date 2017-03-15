
package com.fishuyo.seer
package hid


class Dump(name:String){

  val device = DeviceManager.getDevices(name).head
  read()

  def read() = {
    device.open
    val bytes = new Array[Byte](8)
    while(device.read(bytes) > 0){
      println(bytes.mkString(" "))
    }
    device.close
  }

}