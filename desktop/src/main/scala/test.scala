package com.fishuyo
package test

import graphics._
import io._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()

  GLScene.push(this)

  val live = new Ruby("src/main/scala/test.rb")

  val cube = GLPrimitive.cube()
  GLScene.push(cube)

  SimpleAppRun()  

  override def step(dt:Float){
    live.step(dt)
  }

}



