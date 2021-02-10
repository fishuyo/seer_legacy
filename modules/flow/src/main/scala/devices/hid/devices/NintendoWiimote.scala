package seer
package flow
package hid

import spire.math.UByte

class Wiimote(index:Int) extends HidDeviceIO(index) {

  override lazy val name = Some("Nintendo RVL-CNT-01")

  val sourceElements = List(
    Button("A", 2, 8),
    Button("B", 2, 4),
    Button("minus", 2, 16),
    Button("home", 2, -128),
    Button("plus", 1, 16),
    Button("b1", 2, 2),
    Button("b2", 2, 1),
    Button("up",1,8),
    Button("right",1,2),
    Button("down",1,4),
    Button("left",1,1)
    // AnalogSigned("accX", 42),
    // AnalogSigned("accY", 44),
    // AnalogSigned("accZ", 46)
  )
}

