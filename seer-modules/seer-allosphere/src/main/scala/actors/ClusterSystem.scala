
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

  val hostname = java.net.InetAddress.getLocalHost().getHostName()
  val config = ConfigFactory.parseString(s"""
    akka {
      actor {
        #provider = "akka.remote.RemoteActorRefProvider"
        provider = "akka.cluster.ClusterActorRefProvider"
      }
      remote {
        log-remote-lifecycle-events = off
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          hostname = "$hostname"
          port = 2552
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
      }
      
      cluster {
        seed-nodes = [
          "akka.udp://sphere@gr01:2552" #gr01
          "akka.udp://sphere@gr02:2552" #gr02
          "akka.udp://sphere@gr03:2552" #gr03
          "akka.udp://sphere@gr04:2552" #gr04
          "akka.udp://sphere@gr05:2552" #gr05
          "akka.udp://sphere@gr06:2552" #gr06
          "akka.udp://sphere@gr07:2552" #gr07
          "akka.udp://sphere@gr08:2552" #gr08
          "akka.udp://sphere@gr09:2552" #gr09
          "akka.udp://sphere@gr10:2552" #gr10
          "akka.udp://sphere@gr11:2552" #gr11
          "akka.udp://sphere@gr12:2552" #gr12
          "akka.udp://sphere@gr13:2552" #gr13
          "akka.udp://sphere@gr14:2552" #gr14

          #"akka.udp://sphere@127.0.0.1:2552"
          #"akka.udp://sphere@192.168.0.73:2552" #gr02
          #"akka.udp://sphere@192.168.3.104:2552" 
          #"akka.udp://sphere@Thunder.local:2552" 
        ]

        auto-down-unreachable-after = 10s
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





