
package com.fishuyo.seer
package hid

object Test extends App {

  val joy = new Joystick("PLAYSTATION(R)3 Controller")

  DeviceManager.shutdown()
}