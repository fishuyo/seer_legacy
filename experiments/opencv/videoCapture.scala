package com.fishuyo.seer
package examples.opencv.cap

import graphics._
import io._
import maths._
import dynamic._
import cv._
import video._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object Main extends App with Animatable{

	var capture: VideoCapture = _
  var bgsub:BackgroundSubtract = _
  var colorThresh:ColorThreshold = _
  var blobTracker:BlobTracker = _

  val images = new ListBuffer[Mat]()

  var bytes:Array[Byte] = null

  DesktopApp.loadLibs()

  Scene.push(this)

  val live = new Ruby("videoCapture.rb")

  val cube = Model(Cube())
  Scene.push(cube)

  var pix:Pixmap = null //new Pixmap(1280,720, Pixmap.Format.RGB888)

  var player:VideoPlayer = null

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

    // player = new VideoPlayer("/Users/fishuyo/Desktop/thereisaplace.mov")
    player = new VideoPlayer("/Users/fishuyo/Desktop/out.mov")
    // player = new VideoPlayer("/Users/fishuyo/projects/Documentation/feedback_puddle/1.mov")
    // player = new VideoPlayer("/Users/fishuyo/SeerData/video/out-Feb-9,-2014-11-35-36-PM.mp4")
    // player = new VideoPlayer("/Users/fishuyo/projects/drone/fish.mp4")

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    bytes = new Array[Byte](h.toInt/2*w.toInt/2*3)
    cube.scale.set(1.f, (h/w).toFloat, 1.f)

  	Texture(player.pixmap) //pix) 
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

  	thresh.get(0,0,bytes)
  	val bb = pix.getPixels()
  	bb.put(bytes)
  	bb.rewind()

    // blobTracker(bgsub.mask, pix)
    blobTracker(colorThresh.mask, pix)

    player.animate(dt)

		Texture(0).draw(player.pixmap,0,0) //pix,0,0)

    live.animate(dt)
  }

}



