
package com.fishuyo.seer
package allosphere
package actor

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer

object ClusterConfig {

  val udp_test = ConfigFactory.parseString(s"""
    akka {
      #log-dead-letters = off

      actor {
        provider = "akka.remote.RemoteActorRefProvider"
        #provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          hostname = "${Hostname()}"
          port = 2552
        }
        #compression-scheme = "zlib"
        #zlib-compression-level = 1
      }
      
    }

  """)

  val zmq_test = ConfigFactory.parseString(s"""
    akka {
      #log-dead-letters = off

      actor {
        provider = "akka.remote.RemoteActorRefProvider"
        #provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "${Hostname()}"
          port = 2552
        }
        #compression-scheme = "zlib"
        #zlib-compression-level = 1
      }
      
    }

  """)

  val config = ConfigFactory.parseString(s"""
    akka {
      log-dead-letters = off

      actor {
        #provider = "akka.remote.RemoteActorRefProvider"
        provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "${Hostname()}"
          port = 2552
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
      }
      
      cluster {
        seed-nodes = [
          "akka.tcp://sphere@gr01:2552" #gr01
          "akka.tcp://sphere@gr02:2552" #gr02
          "akka.tcp://sphere@gr03:2552" #gr03
          "akka.tcp://sphere@gr04:2552" #gr04
          "akka.tcp://sphere@gr05:2552" #gr05
          "akka.tcp://sphere@gr06:2552" #gr06
          "akka.tcp://sphere@gr07:2552" #gr07
          "akka.tcp://sphere@gr08:2552" #gr08
          "akka.tcp://sphere@gr09:2552" #gr09
          "akka.tcp://sphere@gr10:2552" #gr10
          "akka.tcp://sphere@gr11:2552" #gr11
          "akka.tcp://sphere@gr12:2552" #gr12
          "akka.tcp://sphere@gr13:2552" #gr13
          "akka.tcp://sphere@gr14:2552" #gr14
        ]

        auto-down-unreachable-after = 10s
      }
    }

    akka.extensions = ["akka.contrib.pattern.DistributedPubSubExtension"]
  """)

  val config10g = ConfigFactory.parseString(s"""
    akka {
      log-dead-letters = off

      actor {
        #provider = "akka.remote.RemoteActorRefProvider"
        provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "${Hostname()+"-10g"}"
          port = 2553
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
      }
      
      cluster {
        seed-nodes = [
          "akka.tcp://sphere@gr01-10g:2552" #gr01
          "akka.tcp://sphere@gr02-10g:2552" #gr02
          "akka.tcp://sphere@gr03-10g:2552" #gr03
          "akka.tcp://sphere@gr04-10g:2552" #gr04
          "akka.tcp://sphere@gr05-10g:2552" #gr05
          "akka.tcp://sphere@gr06-10g:2552" #gr06
          "akka.tcp://sphere@gr07-10g:2552" #gr07
          "akka.tcp://sphere@gr08-10g:2552" #gr08
          "akka.tcp://sphere@gr09-10g:2552" #gr09
          "akka.tcp://sphere@gr10-10g:2552" #gr10
          "akka.tcp://sphere@gr11-10g:2552" #gr11
          "akka.tcp://sphere@gr12-10g:2552" #gr12
          "akka.tcp://sphere@gr13-10g:2552" #gr13
          "akka.tcp://sphere@gr14-10g:2552" #gr14
        ]

        auto-down-unreachable-after = 10s
      }
    }

    akka.extensions = ["akka.contrib.pattern.DistributedPubSubExtension"]
  """)

  val udp10g = ConfigFactory.parseString(s"""
    akka {
      #log-dead-letters = off

      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          maximum-frame-size = 100 MiB
          hostname = "${Hostname()+"-10g"}"
          port = 2553
        }
        #compression-scheme = "zlib"
        #zlib-compression-level = 1
      }
    }
  """)

  val test_config1 = ConfigFactory.parseString(s"""
    akka {
      log-dead-letters = off

      actor {
        provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "$Hostname()"
          port = 2552
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1

      }
      
      cluster {
        seed-nodes = [
          #"akka.tcp://sphere@127.0.0.1:2552"
          #"akka.tcp://sphere@127.0.0.1:2553"
          "akka.tcp://sphere@boom.local:2552"
          "akka.tcp://sphere@Thunder.local:2552"
        ]

        auto-down-unreachable-after = 10s
      }
    }
    akka.extensions = ["akka.contrib.pattern.DistributedPubSubExtension"]
  """)

  def renderers(implicit system:ActorSystem) = {
    List(
      system.actorSelection("akka.udp://state@gr02-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr03-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr04-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr05-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr06-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr07-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr08-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr09-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr10-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr11-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr12-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr13-10g:2553/user/renderer"),
      system.actorSelection("akka.udp://state@gr14-10g:2553/user/renderer")
    )
  }

  // // load the normal config stack (system props, then application.conf, then reference.conf)
  // val regularConfig = ConfigFactory.load();
  // // override regular stack with config
  // val combined = config.withFallback(regularConfig);
  // // put the result in between the overrides (system props) and defaults again
  // val complete = ConfigFactory.load(combined);

}






