package seer
package flow
package hid

abstract class Joycon(index:Int) extends HidDeviceIO(index){
  override lazy val deviceType = AnalogJoystick


  override val outputBuffer:Array[Byte] = Array(
    1, 0, 254, 0, 254, 0, 0, 0, 0, 0, 0,
    255, 39, 16, 0, 50, 255, 39, 16, 0, 50, 255,
    39, 16, 0, 50, 255, 39, 16, 0, 50, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0
  ).map(_.toByte)

  //send command
  // 1(command) :: data

  //send subcommand
  // 1(command) :: counter&0xF, 0x00, 0x01, 0x40, 0x40, 0x00, 0x01, 0x40, 0x40 :: subcommand :: data

  // enable vib
  // 1 :: _ :: 0x48 :: 0x01

  // enable imu
  // 1 :: _ :: 0x40 :: 0x01

  // Increase data rate for Bluetooth
  // 1 :: _ :: 0x03 :: 0x31

  // leds
  // 1 :: _ :: 0x30 :: 0000(flash) 0001(solid)?
  // 1 :: _ :: 0x38 :: 0xFF (slow pulse home led(

  // fetch input
  // 1 :: _ :: 0x0 

  // rumble
  // 0x10 :: counter?, 1(freq?) 1 1 1,  1(freq?) 1 1 1  <- 8bytes

}

class JoyconR(index:Int) extends Joycon(index) {
  override lazy val name = Some("Joy-Con (R)")
  
  val sourceElements = List(
    Button("A", 1, 1),
    Button("B", 1, 4),
    Button("Y", 1, 8),
    Button("X", 1, 2),
    Button("R", 2, 64),
    Button("ZR", 2, 128),
    Button("SL", 1, 16),
    Button("SR", 1, 32),
    Button("plus", 2, 2),
    Button("home", 2, 16),
    Button("stickClick", 2, 8),
    ButtonEx("stickU", 3, 0),
    ButtonEx("stickUR", 3, 1),
    ButtonEx("stickR", 3, 2),
    ButtonEx("stickDR", 3, 3),
    ButtonEx("stickD", 3, 4),
    ButtonEx("stickDL", 3, 5),
    ButtonEx("stickL", 3, 6),
    ButtonEx("stickUL", 3, 7),
    ButtonEx("stickC", 3, 8)
  )
}

class JoyconL(index:Int) extends Joycon(index) {
  override lazy val name = Some("Joy-Con (L)")
  
  val sourceElements = List(
    Button("down", 1, 1),
    Button("left", 1, 4),    
    Button("up", 1, 8),
    Button("right", 1, 2),
    Button("L", 2, 64),
    Button("ZL", 2, 128),
    Button("SL", 1, 16),
    Button("SR", 1, 32),
    Button("minus", 2, 1),
    Button("circle", 2, 32),
    Button("stickClick", 2, 4),
    ButtonEx("stickU", 3, 0),
    ButtonEx("stickUR", 3, 1),
    ButtonEx("stickR", 3, 2),
    ButtonEx("stickDR", 3, 3),
    ButtonEx("stickD", 3, 4),
    ButtonEx("stickDL", 3, 5),
    ButtonEx("stickL", 3, 6),
    ButtonEx("stickUL", 3, 7),
    ButtonEx("stickC", 3, 8)
  )
}



// val j = Device("Joy-Con (R)")

// //val a = Array(0x01, 0, 0x00, 0x01, 0x40, 0x40, 0x00, 0x01, 0x40, 0x40, 0x48, 0x01).map(_.toByte) // enable buzz
// //val a = Array(0x10, 0, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01).map(_.toByte) //buzz

// //val a = Array(0x1, 0, 0x0, 0x01, 0x40, 0x40, 0x00, 0x01, 0x40, 0x40, 0x3, 0x31).map(_.toByte) // rate

// val a = Array(0x1, 0, 0x0, 0x01, 0x40, 0x40, 0x00, 0x01, 0x40, 0x40, 0x40, 0x00).map(_.toByte) //imu

// j.openDevice.foreach(_.setOutputReport(0, a, a.length))


// //j.byteStream.map(_.drop(10).take(1).map((b) => (((b&0xf) << 4)|((b&0xf0) >> 4)) ).map(spire.math.UByte(_)).mkString(" ")) >> Print
// //j.byteStream.map(_.drop(9).take(4).map(spire.math.UByte(_).toInt.toHexString).mkString(" ")) >> Print


// j.byteStream.map{ case b =>
//   val bs = b.drop(9).take(3)
//   val b1 = bs(0)
//   val b2 = bs(1)
//   val b3 = bs(2)
//   val v = spire.math.UByte(b1).toInt + ((b2&0xf) << 8)
//   val v2 = (spire.math.UByte(b3).toInt << 4) + ((b2&0xf0) >> 4)
//   (v2.toFloat / (0xFFF))
// } >> Print