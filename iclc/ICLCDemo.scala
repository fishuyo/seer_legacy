
package com.fishuyo.seer
package iclc

import dynamic._
import audio._

/**
  * This example sets up a SeerScriptLoader which compiles
  * and runs the scala script on modification.
  * 
  * See "./scripts/live.scala"
  * 
  */
object LiveScript extends SeerApp {
  PortAudio.init
  Audio().start

  val live = ScriptManager.load("scripts/workspace")
  
  // val live = ScriptManager.load("scripts/uitest.scala")
  // val live = ScriptManager.load("scripts/simple.scala")
  // val live = ScriptManager.load("scripts/simpleBody.scala")
  // val live = ScriptManager.load("scripts/simpleTime.scala")
  // val live = ScriptManager.load("scripts/simpleSceneChange")
}