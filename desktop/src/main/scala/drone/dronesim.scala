package com.fishuyo
package dronesim

import maths._
import graphics._
import spatial._
import io._
import dynamic._
import audio._
import drone._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.glutils._
//import com.badlogic.gdx.graphics.GL10

object Main extends App with GLAnimatable {

  SimpleAppRun.loadLibs()
  GLScene.push( this )

  var drone = new FakeDrone
  GLScene.push( drone )

  var ground = GLPrimitive.cube(Pose(), Vec3(.1f,.1f,.1f))
  GLScene.push(ground)


  val live = new Ruby("src/main/scala/drone/dronesim.rb")

  Trackpad.connect()

  SimpleAppRun() 

  override def step(dt:Float){
  	live.step(dt)
  } 

}



