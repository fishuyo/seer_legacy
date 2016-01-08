
package com.fishuyo.seer
package actor

import graphics._
import audio._

import akka.actor._
import akka.event.Logging

// object SeerActor {
//   def actorOf(a:SeerActor, name:String) = System().actorOf(Props(a), name)
//   def props = Props(new SeerActor())
//   def props(a:SeerActor) = Props(a)
// }

class SeerActor extends Actor with ActorLogging with Animatable with AudioSource {
  def receive = {
    case "load" => 
      Scene.push(this)
      Audio().push(this)
    case "unload" =>
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