
package seer

import graphics._
import script._
import audio._
import actor._

object ScriptLoader extends SeerApp {
  PortAudio.init()
  PortAudio.start()

  override def parseArgs(args:Array[String]){
    if(args.length == 0){
      println("""Please provide path of script or script directory to load.""")
      java.lang.System.exit(0)
    }

    if(args.length >= 3){
      val address = args(1)
      val port = args(2).toInt
      val a = akka.actor.Address("akka","seer", address, port)
      System() = ActorSystemManager(a)
    }

    val path = args.head
    println(s"Loading ${path}")
    var actor = ScriptManager.load(path)
  }
  
}