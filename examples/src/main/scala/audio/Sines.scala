
package com.fishuyo.seer
package examples.audio

import graphics._
import audio._
import util._

import concurrent.duration._
import scala.language.postfixOps

object Sines extends SeerApp {

  // initialize and use PortAudio audio backend
  PortAudio.init()

  // start the audio actor
  Audio().start()

  var i = 0

  // add a sine wave to the audio graph every second
  val event = Schedule.every(1 second){
    i += 1

    val freq = 440f + 4f * Random.int(-100,100)()
    val amp = .4f/(i+1)
    val s = new Sine(freq, amp)
    Audio().push(s)

    val m = Sphere().translate(i*0.1f - 1f,0,0).scale(0.02f) // make new sphere model, translate and scaled
    m.material = Material.basic             // set material to not use lighting
    val hue = map(freq,40f,840f,0f,1f) // map frequency range to hue value
    m.material.color = HSV(hue, 1, 1)
    Scene += m

    println(s"add Sine $i: $freq $amp $hue")
  }

  // after 20 seconds stop adding sine waves
  Schedule.after(20 seconds){
    event.cancel
    println(s"Stopping after adding $i sine waves.")
  }

}