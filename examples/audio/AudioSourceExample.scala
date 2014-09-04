

package com.fishuyo.seer
package examples.audio

import audio._
import util._

import concurrent.duration._

object AudioSourceExample extends SeerApp {

	GdxAudio.init
	Audio().start

	println("press spacebar...")

	var doClick = false

	var time1,time2 = 0L
	var avg = 0L

	val click = new AudioSource {
  	override def audioIO( io ){ 
  		if(doClick){
  			// set the first sample of each channel to 1.0
  			io.outputSamples(0)(0) = 1.f
  			io.outputSamples(1)(0) = 1.f
  			doClick = false
  			time1 = System.nanoTime
  		}
  	}
	}
	Audio().push(click)

	Schedule.every(1 second){
		doClick = true
	}

	// click on spacebar
	io.Keyboard.use
	io.Keyboard.bind(" ", ()=>{ 
		// println("click"); doClick = true 
		time2 = System.nanoTime
		val dt = (time2-time1) / 1000000.f 
		println(s"latency(ms): $dt")
	})

}