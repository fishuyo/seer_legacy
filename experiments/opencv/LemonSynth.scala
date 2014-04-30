
package com.fishuyo.seer
package examples.opencv.lemonsynth

import graphics._
import io._
import maths._
import dynamic._
import cv._
import video._
import audio._
import gen.Sine
import util._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object Main extends App with Animatable{

  DesktopApp.loadLibs()

	var capture: VideoCapture = _
  var bgsub:BackgroundSubtract = _
  var colorThresh:ColorThreshold = _
  var blobTracker:BlobTracker = _

  var bytes:Array[Byte] = null
  var pix:Pixmap = null //new Pixmap(1280,720, Pixmap.Format.RGB888)

  var live:Ruby = null

  val cube = Model(Cube())
  Scene.push(cube)
  cube.material.textureMix = 1.f

  val synth = new Sine(new Single(400.f), new Single(0.f))
  Audio.push(synth)

  Scene.push(this)
  DesktopApp()  

  override def init(){
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    capture = new VideoCapture(0)
    bgsub = new BackgroundSubtract
    colorThresh = new ColorThreshold((20,40)) //(140,10),(20,255),(20,255))
    blobTracker = new BlobTracker
    blobTracker.setThreshold(30,30)

    Thread.sleep(2000)

    val w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    val h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)

    println( s"starting capture w: $w $h")

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
    cube.scale.set(1.f, (h/w).toFloat, 1.f)

  	Texture(pix) 

  	live = new Ruby("lemon.rb")
  }
  override def draw(){

    Shader.lightingMix = 0.f
  	Shader.textureMix = 1.f
  	Texture.bind(0)
  	cube.draw()

  }

  override def animate(dt:Float){

  	val img = new Mat()
  	val read = capture.read(img)

  	if( !read ) return

    val rsmall = new Mat()
    val small = new Mat()
    Imgproc.resize(img,rsmall, new Size(), 0.5,0.5,0)
    Core.flip(rsmall,small,1)

    // val diff = bgsub(small)
    val thresh = colorThresh(small, false)

    Imgproc.cvtColor(thresh,small, Imgproc.COLOR_BGR2RGB)   // convert to rgb

  	small.get(0,0,bytes)
  	val bb = pix.getPixels()
  	bb.put(bytes)
  	bb.rewind()

    // blobTracker(bgsub.mask, pix)
    blobTracker(colorThresh.mask, pix)

		Texture(0).draw(pix,0,0)

    live.animate(dt)
  }

}



