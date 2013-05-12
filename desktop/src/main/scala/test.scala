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
  //GLScene.push(cube)

  SimpleAppRun()  

  override def init(){
    Shader("res/shaders/sky.vert", "res/shaders/sky.frag")
  	Texture("res/bunny.png");
  	Shader(3)
  	Shader.monitor(3)
  }
  override def draw(){

  	Texture(0).bind()
  	Shader().setUniformi("u_texture", 0);
  	cube.draw()

  }

  override def step(dt:Float){
    live.step(dt)
  }

}



