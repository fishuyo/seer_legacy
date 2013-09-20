package com.fishuyo
package examples.opencv.loop

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
import org.opencv.imgproc._

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
  GLScene.push(this)

	var capture: VideoCapture = _
  var bgsub = new BackgroundSubtract
	var loop = new VideoLoop
  var subtract = true
  def setSubtract(v:Boolean) = subtract = v

	var bytes:Array[Byte] = null
	var w = 0.0
	var h = 0.0

  val cube = Model(Cube())
  GLScene.push(cube)

  var pix:Pixmap = null
  
  val live = new Ruby("videoLoop.rb")

  SimpleAppRun()  

  override def init(){
    capture = new VideoCapture(0)

    Thread.sleep(2000)

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)

    println( s"starting capture w: $w $h")

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
  	cube.scale.set(1.f, (h/w).toFloat, 1.f)

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

    val rsmall = new Mat()
  	val small = new Mat()
  	Imgproc.resize(img,rsmall, new Size(), 0.5,0.5,0)
    Core.flip(rsmall,small,1)

    var sub = small
    if( subtract ) sub = bgsub(small)

  	val out = new Mat()
  	loop.videoIO( sub, out)
    if( out.empty()) return

    if( subtract ){
      val bgmask = new Mat()
      Core.compare(out, new Scalar(0.0), bgmask, Core.CMP_EQ)
      bgsub.bg.copyTo(out,bgmask)
    }

  	out.get(0,0,bytes)
		val bb = pix.getPixels()
		bb.put(bytes)
		bb.rewind()

		Texture(0).draw(pix,0,0)

    live.step(dt)
  }

}



