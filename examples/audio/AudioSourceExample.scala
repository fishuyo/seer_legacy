

package com.fishuyo.seer
package examples.audio

import audio._
import graphics._
import util._
import io._

import concurrent.duration._

object AudioSourceExample extends SeerApp {

	GdxAudio.init
	Audio().start

	println("press spacebar...")

	var doClick = false

	val click = new AudioSource {
  	override def audioIO( io:AudioIOBuffer ){ 
  		if(doClick){
  			// set the first sample of each channel to 1.0
  			io.outputSamples(0)(0) = 1f
  			io.outputSamples(1)(0) = 1f
  			doClick = false
  		}
  	}
	}
	Audio().push(click)

	// click on spacebar
	Keyboard.use
	Keyboard.bind(" ", ()=>{ 
		println("click"); doClick = true 
	})

  override def draw() = if(doClick) Plane().scale(0.01f,1,1).draw()

}