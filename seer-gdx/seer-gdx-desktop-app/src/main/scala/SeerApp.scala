
package com.fishuyo.seer

import graphics._

class SeerApp extends App with Animatable {

  DesktopApp.loadLibs()
  Scene.push(this)
  DesktopApp.run()
  // Repl.imports += this.getClass.getName.replace("$","")
  // Repl.start()

}