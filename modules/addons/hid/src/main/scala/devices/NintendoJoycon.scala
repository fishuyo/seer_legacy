package com.fishuyo.seer
package hid

import org.hid4java._

class JoyconR(device:HidDevice) extends Device(device) {
  override val deviceType = AnalogJoystick
  
  val elements = List(
    Button("A", 1, 1),
    Button("B", 1, 4),
    Button("Y", 1, 8),
    Button("X", 1, 2),
    Button("R", 2, 64),
    Button("ZR", 2, 128),
    Button("SL", 1, 16),
    Button("SR", 1, 32),
    Button("+", 2, 2),
    Button("home", 2, 16),
    Button("stick", 2, 8),
    ButtonEx("stickL", 3, 0),
    ButtonEx("stickUL", 3, 1),
    ButtonEx("stickU", 3, 2),
    ButtonEx("stickUR", 3, 3),
    ButtonEx("stickR", 3, 4),
    ButtonEx("stickDR", 3, 5),
    ButtonEx("stickD", 3, 6),
    ButtonEx("stickDL", 3, 7),
    ButtonEx("stickC", 3, 8)
  )
}

class JoyconL(device:HidDevice) extends Device(device) {
  override val deviceType = AnalogJoystick
  
  val elements = List(
    Button("left", 1, 1),
    Button("up", 1, 4),
    Button("right", 1, 8),
    Button("down", 1, 2),
    Button("L", 2, 64),
    Button("ZL", 2, 128),
    Button("SL", 1, 16),
    Button("SR", 1, 32),
    Button("-", 2, 1),
    Button("ss", 2, 32),
    Button("stick", 2, 4),
    ButtonEx("stickR", 3, 0),
    ButtonEx("stickDR", 3, 1),
    ButtonEx("stickD", 3, 2),
    ButtonEx("stickDL", 3, 3),
    ButtonEx("stickL", 3, 4),
    ButtonEx("stickUL", 3, 5),
    ButtonEx("stickU", 3, 6),
    ButtonEx("stickUR", 3, 7),
    ButtonEx("stickC", 3, 8)
  )
}