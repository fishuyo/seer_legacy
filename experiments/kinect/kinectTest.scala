package com.fishuyo.seer
package examples.kinect

import graphics._
import io._
import maths._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

object Main extends App with Animatable{

  DesktopApp.loadLibs()

  Scene.push(this)

  val live = new Ruby("kinectTest.rb")

  val cube = Model(Cube())
  cube.scale.set(1.f, (2*480.f)/640.f, 1.f)
  Scene.push(cube)

  val pix = new Pixmap(640,2*480, Pixmap.Format.RGBA8888)
  pix.setColor(1.f,1.f,1.f,0)
  pix.fill()

  DesktopApp()  

  override def init(){
    //Shader("res/shaders/sky.vert", "res/shaders/sky.frag")
  	Texture(pix) //"res/bunny.png");
  	//Shader(3)
  	// Shader.monitor(3)
  }
  override def draw(){

  	Texture(0).bind()
  	Shader().setUniformi("u_texture0", 0);
  	cube.draw()

  }

  override def animate(dt:Float){
    live.animate(dt)
  }

}



