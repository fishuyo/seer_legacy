package seer
package examples.audio

import audio._
import script._


object SimpleSynth extends SeerApp {
  PortAudio.init()
  PortAudio.start()

  val script = ScriptManager.load("scripts/audio/synth.scala")
}