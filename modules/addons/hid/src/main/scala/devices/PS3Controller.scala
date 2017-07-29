
package com.fishuyo.seer
package hid

import org.hid4java._

class PS3Controller(device:HidDevice) extends Device(device) {

  override val name = "PLAYSTATION(R)3 Controller"
  override val deviceType = DualAnalogJoystick

  val elements = List(
    Analog("leftX", 6),
    Analog("leftY", 7),
    Analog("rightX", 8),
    Analog("rightY", 9),
    Button("L2", 3, 1),
    Button("R2", 3, 2),
    Button("L1", 3, 4),
    Button("R1", 3, 8),
    Button("triangle", 3, 16),
    Button("circle", 3, 32),
    Button("X", 3, 64),
    Button("square", 3, 128),
    Button("up",2,16),
    Button("right",2,32),
    Button("down",2,64),
    Button("left",2,128),
    Button("select",2,1),
    Button("start",2,8),
    Button("leftClick",2,2),
    Button("rightClick",2,4),
    Button("home",4,1),
    Analog("L2Analog", 18),
    Analog("R2Analog", 19),
    Analog("L1Analog", 20),
    Analog("R1Analog", 21),
    Analog("triangleAnalog", 22),
    Analog("circleAnalog", 23),
    Analog("XAnalog", 24),
    Analog("squareAnalog", 25),
    Analog("upAnalog", 14),
    Analog("rightAnalog", 15),
    Analog("downAnalog", 16),
    Analog("leftAnalog", 17),
    AnalogSigned("accX", 42),
    AnalogSigned("accY", 44),
    AnalogSigned("accZ", 46)

  )


}