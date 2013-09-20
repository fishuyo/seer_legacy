package com.fishuyo
package examples.opencv.cap

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

	var capture: VideoCapture = _
  var bgsub:BackgroundSubtract = _
  var blobTracker:BlobTracker = _

  val images = new ListBuffer[Mat]()

  var bytes:Array[Byte] = null

  SimpleAppRun.loadLibs()

  GLScene.push(this)

  val live = new Ruby("videoCapture.rb")

  val cube = Model(Cube())
  GLScene.push(cube)

  var pix:Pixmap = null //new Pixmap(1280,720, Pixmap.Format.RGB888)

  SimpleAppRun()  

  override def init(){
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    capture = new VideoCapture(0)
    bgsub = new BackgroundSubtract
    blobTracker = new BlobTracker

    val w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    val h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)

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

    val diff = bgsub(small)

  	diff.get(0,0,bytes)
  	val bb = pix.getPixels()
  	bb.put(bytes)
  	bb.rewind()

    blobTracker(bgsub.mask, pix)

		Texture(0).draw(pix,0,0)

    live.step(dt)
  }

}



