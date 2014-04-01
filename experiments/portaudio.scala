
package com.fishuyo.seer
package portaudio

import audio._


object Main extends SeerApp {


	override def init(){
 		PortAudio.initialize()
 		PortAudio.start()
	}


}