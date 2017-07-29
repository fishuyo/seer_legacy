
package com.fishuyo.seer
package hid

import org.hid4java._

class UnknownDevice(device:HidDevice) extends Device(device) {
  val elements = List()

  override def toString() = s"UnknownDevice: ${device.getProduct}"
}