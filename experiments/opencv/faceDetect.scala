package com.fishuyo.seer
package examples.opencv.faceDetect

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

object Main extends App with Animatable{

  DesktopApp.loadLibs()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)

  Scene.push(this)

	var capture: VideoCapture = _
	implicit var camera = new CalibratedCamera()
	camera.loadParams("thunder_calib.json")
	camera.printParams()

	var faceDetector = new FaceDetector(0.17/2.0)

	// Camera Calibration
	var calcamera = new CalibratedCamera()
  // val images = calcamera.loadImageDirectory("/Users/fishuyo/projects/Catch-Release/Floor/board_webcam")
	// val images = calcamera.loadImageDirectory("/Users/fishuyo/projects/Catch-Release/Floor/thunder_webcam")
  // calcamera.calibrateFromBoardImages(images, new Size(6,9), 0.0235)
	// calcamera.calibrateFromBoardImages(images, new Size(4,5), 0.013)
  // calcamera.writeParams("logitech_calib.json")
	// calcamera.writeParams("thunder_calib.json")
	calcamera.printParams()
  // System.exit(0)

	var bytes:Array[Byte] = null
	var w = 0.0
	var h = 0.0

  val cube = Model(Cube())
  Scene.push(cube)

  var pix:Pixmap = null
  
  val live = new Ruby("faceDetect.rb")

  DesktopApp()  

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

    Shader.lightingMix = 0.f
  	Shader.textureMix = 1.f
  	Texture.bind(0)
  	cube.draw()

  }

  override def animate(dt:Float){

  	val img = new Mat()
  	val read = capture.read(img)

  	if( !read ) return

  	val small = new Mat()
  	Imgproc.resize(img,small, new Size(), 0.5,0.5,0)

  	val count = faceDetector(small)

  	small.get(0,0,bytes)
		val bb = pix.getPixels()
		bb.put(bytes)
		bb.rewind()

  	if( count > 0){
			val x = faceDetector.face.x
			val y = faceDetector.face.y
			val w = faceDetector.face.width
			val h = faceDetector.face.height
			pix.setColor(0.f,1.f,0.f,1.f)
			pix.drawRectangle(x,y,w,h)
		}

		Texture(0).draw(pix,0,0)

    live.animate(dt)
  }

}



