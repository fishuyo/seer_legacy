
package com.fishuyo.seer
package examples.leap

import graphics._
import spatial._
import leap._

import com.leapmotion.leap.Frame

object LeapTest extends SeerApp{


  Leap.connect
  Leap.bind( (frame:Frame) => {

    if( !frame.hands().isEmpty() ){

    var hand = frame.hands().get(0)
    // if( hand.fingers().isEmpty() ) return
    var count = hand.fingers().count()


    // var finger = hand.fingers().get(0)
    // var pos = finger.tipPosition()
    println(count)
    }

  })





}