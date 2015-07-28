
package com.fishuyo.seer
package examples.actor

import actor._
import dynamic._
import io._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import concurrent.duration._
import concurrent.Await

object AkkaConfig {
  def config(host:String="localhost", port:String="2552") = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "${host}"
          port = ${port}
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
  """)
}

object RemoteScript extends SeerApp {
  var myhost = ""
  var myport = "2552"
  var desthost = ""
  var destport = "2553"
  if(args.length > 0) myport = args(0)
  if(args.length > 1) destport = args(1)
  if(args.length > 2) myhost = args(2)
  if(args.length > 3) desthost = args(3)
  if(myhost == "") myhost = Hostname()
  if(desthost == "") desthost = Hostname()

  val sys = ActorSystem("seer", ConfigFactory.load(AkkaConfig.config(myhost,myport)))
  System() = sys

  val remote = System().actorFor(s"akka.tcp://seer@${desthost}:${destport}/user/ScriptManager")
  var loader:ActorRef = _

  import ScriptLoader._
  ScriptLoader()

  def load(){
    try{
      implicit val timeout = Timeout(10 seconds)  
      val f = remote ? "create"
      loader = Await.result(f, 10 seconds).asInstanceOf[ActorRef]
      // loader ! RunFile("../scripts/live.scala", true)
      loader !  RunCode(s"""
        object Script extends SeerScript { 
          val s = Sphere()
          s.material = Material.specular
          s.material.color = HSV(${util.Random.float()},1,1)
          override def draw(){ s.draw }
        }
        Script
      """)
    } catch { case e:Exception => ()}
  }
  
  def reload(){
    try{
      loader !  Reload(s"""
        object Script extends SeerScript { 
          val s = Sphere()
          s.material = Material.specular
          s.material.color = HSV(${util.Random.float()},1,1)
          override def draw(){ s.draw }
        }
        Script
      """)
    } catch { case e:Exception => ()}
  }

  Keyboard.clear
  Keyboard.use 
  Keyboard.bind("l", load)
  Keyboard.bind("r", reload)
}