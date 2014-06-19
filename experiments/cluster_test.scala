package com.fishuyo.seer
package cluster

import dynamic._ 

object Main extends SeerApp {	
  val live = new SeerScriptLoader("scripts/cluster_test.scala")
}