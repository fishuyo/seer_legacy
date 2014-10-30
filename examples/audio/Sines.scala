
package com.fishuyo.seer
package examples.audio

import graphics._
import audio._
import util._

object Sines extends SeerApp {

	// initialize the GdxAudio driver
	GdxAudio.init

	// start the audio actor
	Audio().start

	var i = 0
	var t = 0f

	override def animate(dt:Float){
		t += dt 

		// every second add a Sine generator with random frequency
		if( t > 1f){
			t = 0f
			i += 1

			val freq = 440f + 4f * Random.int(-100,100)() 
			val amp = .4f/(i+1)

			val s = new Sine(freq, amp)

		  Audio().push(s) 
			println(s"add Sine $i: $freq $amp")
		}
	}

}