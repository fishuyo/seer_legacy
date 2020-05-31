package com.fishuyo.seer 
package script 

import graphics._
import audio._

// import scala.language.dynamics

class SeerScript extends Animatable with AudioSource {
// class SeerScript extends scala.Dynamic with Animatable with AudioSource {

  // def selectDynamic(name:String){println(s"$name select")}
  // def applyDynamic(name:String)(args:Any*){println(s"$name called")}
  // def copy[T](from:T){}

  // var node = new RenderNode
  // node.renderer.camera = Camera
  // node.renderer.shader = RootNode.renderer.shader
  // node.renderer.scene.push(this)
  // node.renderer.clear = false

  def load() = {
    Scene.push(this)
    Audio().push(this)
  }
  def unload() = {
    Scene.remove(this)
    Audio().sources -= this
  }
  def preUnload() = {}
}