
package com.fishuyo.seer
package examples.dynamic

import dynamic._

/**
	* This example sets up a SeerScriptLoader which compiles
	* and runs the scala script on modification.
	* 
	* See "./scripts/live.scala"
	* 
	*/
object LiveScript extends SeerApp {
  val live = new SeerScriptLoader("scripts/live.scala")

  override def draw(){}
  override def animate(dt:Float){}
}