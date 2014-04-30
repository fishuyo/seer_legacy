
package com.fishuyo.seer

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory

package object actor {

  implicit val system = ActorSystem("seer", ConfigFactory.load(ActorManager.config))

}

