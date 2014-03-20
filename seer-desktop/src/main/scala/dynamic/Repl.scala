
package com.fishuyo.seer
package dynamic

import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings



import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem



object Repl {

  val system = ActorSystem("Repl")
  val actor = system.actorOf(Props( new ReplActor ), name = "repl")

  def repl = new SeerILoop {
    override def loop(): Unit = {
      // intp.bind("e", "Double", 2.71828)
      super.loop()
    }
  }

  def start() = {
  	val settings = new Settings
  	settings.Yreplsync.value = true

  	//use when launching normally outside SBT
  	// settings.usejavacp.value = true      

  	//an alternative to 'usejavacp' setting, when launching from within SBT
  	settings.embeddedDefaults[Repl.type]

  	repl.process(settings)
	}
}

class SeerILoop extends ILoop {
  override def prompt = "seer> "

  addThunk {
    intp.beQuietDuring {
      intp.addImports(
      	"com.fishuyo.seer._",
      	"com.fishuyo.seer.graphics._",
      	"com.fishuyo.seer.maths._",
      	"com.fishuyo.seer.io._",
      	"com.fishuyo.seer.util._")
    }
  }

  override def printWelcome() {
    // echo("\n" +
    //   "         \\,,,/\n" +
    //   "         (o o)\n" +
    //   "-----oOOo-(_)-oOOo-----")
    // echo("\n" +
    // "    ,-\"\"-.    \n" +
    // "   / ,--. \\  \n" +
    // "  | ( () ) |  \n" +
    // "   \\ `--' /  \n" +
    // "    `-..-'    \n")

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

	  	//use when launching normally outside SBT
	  	// settings.usejavacp.value = true      

	  	//an alternative to 'usejavacp' setting, when launching from within SBT
	  	settings.embeddedDefaults[Repl.type]

	  	Repl.repl.process(settings)
		}
  }
}
