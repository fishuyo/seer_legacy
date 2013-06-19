package com.fishuyo
package examples.loop

import graphics._
import io._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)
  
  var looper = new Looper

  Audio.push( looper )
  GLScene.push( looper )

  val live = new Ruby("src/main/scala/loop/loop.rb")

  SimpleAppRun()  

  override def step(dt:Float){
    live.step(dt)
  }

}



