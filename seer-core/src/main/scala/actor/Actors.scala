
package com.fishuyo.seer
package actor

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer


object ActorManager {

  // make a Config with just your special setting
  // val config = ConfigFactory.parseString("""
  //     audio {
  //       audio-dispatcher {
  //         type = "PinnedDispatcher"
  //         executor = "thread-pool-executor"
  //       }

  //       old-audio-dispatcher {
  //         # Dispatcher is the name of the event-based dispatcher
  //         type = Dispatcher
  //         # What kind of ExecutionService to use
  //         executor = "fork-join-executor"
  //         # Configuration for the fork join pool
  //         fork-join-executor {
  //           # Min number of threads to cap factor-based parallelism number to
  //           parallelism-min = 2
  //           # Parallelism (threads) ... ceil(available processors * factor)
  //           parallelism-factor = 2.0
  //           # Max number of threads to cap factor-based parallelism number to
  //           parallelism-max = 10
  //         }
  //         # Throughput defines the maximum number of messages to be
  //         # processed per actor before the thread jumps to the next actor.
  //         # Set to 1 for as fair as possible.
  //         throughput = 100
  //       }

  //       akka.actor.deployment {
  //         /audio-main {
  //           dispatcher = audio-dispatcher
  //         }
  //       }
  //     }
  // """)
  val config = ConfigFactory.parseString("""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          hostname = "192.168.0.101"
          port = 2552
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
  """)

  // load the normal config stack (system props, then application.conf, then reference.conf)
  val regularConfig = ConfigFactory.load();
  // override regular stack with config
  val combined = config.withFallback(regularConfig);
  // put the result in between the overrides (system props) and defaults again
  val complete = ConfigFactory.load(combined);

}


import akka.event.Logging
 
class StateActor extends Actor with akka.actor.ActorLogging {
  override def preStart() = {
    log.debug("Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case "test" => log.info("Received test")
    case x => log.warning("Received unknown message: {}", x)
  }
}




