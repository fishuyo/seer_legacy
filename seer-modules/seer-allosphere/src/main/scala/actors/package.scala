
package com.fishuyo.seer.allosphere

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory

package object actor {

  implicit val system = ActorSystem("sphere", ConfigFactory.load(ClusterConfig.config))
  
}

