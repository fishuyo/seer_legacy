
package com.fishuyo.seer
package dynamic

import collection.mutable.ListBuffer
import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings
import java.io.CharArrayWriter
import java.io.PrintWriter

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem



object Repl {

  val system = ActorSystem("Repl")
  val actor = system.actorOf(Props( new ReplActor ), name = "repl")
  
  var imports = ListBuffer("com.fishuyo.seer", "com.fishuyo.seer._", "com.fishuyo.seer.graphics._",
                    "com.fishuyo.seer.spatial._", "com.fishuyo.seer.io._", "com.fishuyo.seer.util._" 
                )

  def repl = new SeerILoop(imports) {
    override def loop(): Unit = {
      // intp.bind("e", "Double", 2.71828)
      super.loop()
    }
  }

  var inSBT = isRunFromSBT

  def start(){ actor ! "start"}

  def startNoActor() = {
  	val settings = new Settings
  	settings.Yreplsync.value = true

   // Different settings needed when running from SBT or normally
    if (isRunFromSBT) {
      settings.embeddedDefaults[Repl.type]
    } else {
      settings.usejavacp.value = true
    }

  	repl.process(settings)
	}

  def isRunFromSBT = {
    val c = new CharArrayWriter()
    new Exception().printStackTrace(new PrintWriter(c))
    val ret = c.toString().contains("at sbt.")
    // println(s"Repl starting from sbt? $ret")
    ret
  }
}

class SeerILoop(imports:Seq[String]) extends ILoop {

  override def prompt = "seer> "

  addThunk {
    intp.beQuietDuring {
      intp.addImports(imports : _*)
    }
  }

  override def printWelcome() {

    echo("\n"+
    " ___  ___  ___ _ __ \n"+
		"/ __|/ _ \\/ _ \\ '__|\n"+
		"\\__ \\  __/  __/ |   \n"+
		"|___/\\___|\\___|_|   \n")
                    
  }

}

class ReplActor extends Actor {
  def receive = {
    case "start" => {
      val settings = new Settings
      settings.Yreplsync.value = true

     // Different settings needed when running from SBT or normally
      if (Repl.inSBT) {
        settings.embeddedDefaults[Repl.type]
      } else {
        settings.usejavacp.value = true
      }

      Repl.repl.process(settings)
    }
  }

}
