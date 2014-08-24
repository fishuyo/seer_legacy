

package com.fishuyo.seer
package examples.audio

import audio._

object AudioSourceExample extends SeerApp {

	GdxAudio.init
	Audio().start

	println("press spacebar...")

	var doClick = false

	val click = new AudioSource {
  	override def audioIO( io ){ 
  		if(doClick){
  			// set the first sample of each channel to 1.0
  			io.outputSamples(0)(0) = 1.f
  			io.outputSamples(1)(0) = 1.f
  			doClick = false
  		}
  	}
	}
	Audio().push(click)

	// click on spacebar
	io.Keyboard.use
	io.Keyboard.bind(" ", ()=>{ println("click"); doClick = true })

}