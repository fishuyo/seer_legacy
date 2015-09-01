
// based on Code from Viktor Klang: https://gist.github.com/viktorklang/2422443

package com.fishuyo.seer.actor

import akka.actor._
import akka.event.Logging

object GraphicsActor {
  // val actor = System().actorOf(Props[GraphicsActor].withDispatcher("seer-dispatcher"), "graphics-actor")
  val actor = System().actorOf(Props[GraphicsActor].withDispatcher("seer-dispatcher"), "graphics-actor")
}

class GraphicsActor extends Actor with ActorLogging {

  def receive = {
    case f:Function0[Unit] => f()
    case _ => ()
  }
     
  override def preStart() = {
    log.debug("Graphics actor Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Graphics actor Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }
}

