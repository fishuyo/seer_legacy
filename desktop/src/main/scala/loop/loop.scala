package com.fishuyo
package examples.loop

import graphics._
import io._
import dynamic._
import audio._

import monido._

object Main extends App{

  SimpleAppRun.loadLibs()
  
  var looper = new Looper //List[Loop]()
  //for( i<-(0 until 8)) loops = new Loop(10.f) :: loops

  //loops.foreach( s => Audio.push( s ))
  Audio.push( looper )

  val live = new Ruby("src/main/scala/loop/loop.rb")
  OSC.listen()

  val monitor = FileMonido("src/main/scala/loop/loop.rb"){
    case ModifiedOrCreated(f) => Main.live.reload;
    case _ => None
  }
  FileMonido("res/shaders/firstPass.vert"){
    case ModifiedOrCreated(f) => Shader.reload;
    case _ => None
  }
  FileMonido("res/shaders/firstPass.frag"){
    case ModifiedOrCreated(f) => Shader.reload;
    case _ => None
  }
  SimpleAppRun()  

}



