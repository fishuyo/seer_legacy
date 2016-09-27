
package com.fishuyo.seer
package actor

import graphics._
import audio._

import akka.actor._
import akka.event.Logging

object SeerActor {
  def props(c:Class[_]) = Props(c)

  case class Name(n:String)
}

class SeerActor extends Actor with ActorLogging with Animatable with AudioSource {
  import SeerActor._

  implicit var name = "default"

  def receive = {
    case "load" => load()
    case "unload" => unload()
    case Name(n) => name = n
  }

  def load(){
    Scene.push(this)
    Audio().push(this)
  }
  def unload(){
    Scene.remove(this)
    Audio().sources -= this
  }
}

// class SeerActorNode extends RenderNode with Actor with ActorLogging with Animatable with AudioSource {

//   override def animate(dt:Float) = super[RenderNode].animate(dt)

//   def receive = {
//     case "load" => 
//       RenderGraph.addNode(this)
//       renderer.scene.push(this)
//       renderer.camera = Camera

//       // Scene.push(this)
//       Audio().push(this)
//     case "unload" =>
//       RenderGraph.removeNode(this)

//       // Scene.remove(this)
//       Audio().sources -= this
//   }
// }