
package seer
package flow
package hid

import spire.math.UByte

class LogitechRumblePad2USB(index:Int) extends HidDeviceIO(index) {
  override lazy val name = Some("Logitech RumblePad 2 USB")
  override lazy val deviceType = DualAnalogJoystick

  val sourceElements = List(
    Analog("leftX", 0),
    Analog("leftY", 1),
    Analog("rightX", 2),
    Analog("rightY", 3),
    Button("b7", 5, 4), //l2
    Button("b8", 5, 8), //r2
    Button("b5", 5, 1),  //l1
    Button("b6", 5, 2),  //r1
    Button("b4", 4, -128), //bU
    Button("b3", 4, 64), // bR
    Button("b2", 4, 32), // bD
    Button("b1", 4, 16), // bL
    // Button("up",2,16), // Hat style.. 0-7 clockwise up is 0, ul is 7, center is 8
    // Button("right",2,32),
    // Button("down",2,64),
    // Button("left",2,128),
    Button("b9",5,16), //select
    Button("b10",5,32), //start
    Button("leftClick",5,64), 
    Button("rightClick",5,-128)
  )

}


// Funky button values.. think controller was broken, need to use different one to verify
// class LogitechCordlessRumblePad2(index:Int) extends HidDeviceIO("Logitech Cordless RumblePad 2", index) {

//   override val deviceType = DualAnalogJoystick

//   val sourceElements = List(
//     Analog("leftX", 1),
//     Analog("leftY", 2),
//     Analog("rightX", 3),
//     Analog("rightY", 4),
//     Button("b7", 6, 4), //l2
//     Button("b8", 6, 8), //r2
//     Button("b5", 6, 1),  //l1
//     Button("b6", 6, 2),  //r1
//     Button("b4", 4, -128), //bU
//     Button("b3", 4, 64), // bR
//     Button("b2", 4, 32), // bD
//     Button("b1", 4, 16), // bL
//     // Button("up",2,16), // Hat style.. 0-7 clockwise up is 0, ul is 7, center is 8
//     // Button("right",2,32),
//     // Button("down",2,64),
//     // Button("left",2,128),
//     Button("b9",5,16), //select
//     Button("b10",5,32), //start
//     Button("leftClick",5,64), 
//     Button("rightClick",5,-128)
//   )

// }

