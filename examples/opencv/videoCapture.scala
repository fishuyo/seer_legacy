package com.fishuyo
package examples.opencv

import graphics._
import io._
import maths._
import dynamic._
import cv._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._

object Main extends App with GLAnimatable{

	var capture: VideoCapture = _
  var bgsub:BackgroundSubtract = _


	val bytes = new Array[Byte](1280*720*3)

  SimpleAppRun.loadLibs()

  GLScene.push(this)

  val live = new Ruby("videoCapture.rb")

  val cube = Model(Cube())
  cube.scale.set(1.f, (720.f)/1280.f, 1.f)
  GLScene.push(cube)

  val pix = new Pixmap(1280,720, Pixmap.Format.RGB888)
  pix.setColor(1.f,1.f,1.f,0)
  pix.fill()

  SimpleAppRun()  

  override def init(){
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    capture = new VideoCapture(0)
    bgsub = new BackgroundSubtract

   //  val sizes = capture.getSupportedPreviewSizes()
   //  for( s <- sizes ){
   //  	println( s"$s.width x $s.height")
  	// }
  	Texture(pix) 
  }
  override def draw(){

    Shader.lighting = 0.f
  	Shader.texture = 1.f
  	Texture.bind(0)
  	cube.draw()

  }

  override def step(dt:Float){

  	val img = new Mat()
  	val read = capture.read(img)

  	if( !read ) return

    val diff = bgsub(img)


  	diff.get(0,0,bytes)

  	val bb = pix.getPixels()
  	bb.put(bytes)
  	bb.rewind()

		Texture(0).draw(pix,0,0)

    live.step(dt)
  }

}



