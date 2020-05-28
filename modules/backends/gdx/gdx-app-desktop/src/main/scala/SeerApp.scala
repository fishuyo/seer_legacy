
package com.fishuyo.seer

import graphics._
 
class SeerApp extends Animatable {
  DesktopApp.loadLibs()

  def parseArgs(as:Array[String]) = {}

  def main(args: Array[String]): Unit = {
    // DesktopApp.loadLibs()
    parseArgs(args)
    Scene.push(this)
    DesktopApp.run()
  }
  // Repl.imports += this.getClass.getName.replace("$","")
  // Repl.start()

}

// class SeerAppNode extends RenderNode(null) with App with Animatable {
//   DesktopApp.loadLibs()
//   renderer = Renderer() //super hacky, libraries need to be loaded before Renderer constructor
//   RenderGraph.addNode(this)
//   renderer.scene.push(this)
//   renderer.camera = Camera
//   DesktopApp.run()

//   override def animate(dt:Float) = super[RenderNode].animate(dt)

// }