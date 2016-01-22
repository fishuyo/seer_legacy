
package com.fishuyo.seer
package actor

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer

object System {
  var system:ActorSystem = _
  def apply() = {
    if(system == null) system = ActorSystemManager.default()
    system
  }
  def update(s:ActorSystem) = system = s
}

object ActorSystemManager {

  def apply(address:Address) = {
    address.protocol match {
      case "akka.tcp" => tcp(address.host.get,address.port.get,address.system)
      case "akka.udp" => udp(address.host.get,address.port.get,address.system)
      case _ => default(address.system)
    }
  }

  def default(system:String = "seer") = ActorSystem(system, ConfigFactory.load(config))
  def tcp(hn:String=Hostname(), port:Int=2552, system:String="seer") = ActorSystem(system, ConfigFactory.load(config_tcp(hn,port)))
  def udp(hn:String=Hostname(), port:Int=2552, system:String="seer") = ActorSystem(system, ConfigFactory.load(config_udp(hn,port)))

  def config_tcp(hostname:String=Hostname(), port:Int=2552) = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "$hostname"
          port = $port
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
  """)

  def config_udp(hostname:String=Hostname(), port:Int=2552) = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          hostname = "$hostname"
          port = $port
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
  """)

  val config = ConfigFactory.parseString("""
    seer-dispatcher {
      type = "Dispatcher"
      executor = "com.fishuyo.seer.actor.SeerEventThreadExecutorServiceConfigurator"
      throughput = 1
    }
  """)

  // load the normal config stack (system props, then application.conf, then reference.conf)
  // val regularConfig = ConfigFactory.load();
  // override regular stack with config
  // val combined = config.withFallback(regularConfig);
  // put the result in between the overrides (system props) and defaults again
  // val complete = ConfigFactory.load(combined);

}

