
package com.fishuyo.seer
package examples.dynamic

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
  // GdxAudio.init
  // Audio().start
  
  val live = ScriptLoader("scripts/empty.scala")
  // val live = new SeerScriptLoader("scripts/empty.scala")

  override def draw(){}
  override def animate(dt:Float){}
}