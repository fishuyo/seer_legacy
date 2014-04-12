package com.fishuyo.seer
package skelOSC

import dynamic._ 

object Main extends SeerApp {
  val live = new SeerScriptLoader("experiments/scripts/skeletonOsc.scala")
}