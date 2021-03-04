
package seer

import graphics._
 
class SeerApp extends Animatable {
  GdxAppDesktop.loadLibs()

  def parseArgs(as:Array[String]){}

  def main(args: Array[String]): Unit = {
    parseArgs(args)
    Scene.push(this)
    GdxAppDesktop.run()
  }
}
