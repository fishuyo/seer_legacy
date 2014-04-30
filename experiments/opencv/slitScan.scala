package com.fishuyo.seer
package examples.opencv.slitscan

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
  Scene.push(this)

	var capture: VideoCapture = _

  val images = new ListBuffer[Mat]()

	var rows:Array[Byte] = null
	var w = 0.0
	var h = 0.0

  val cube = Model(Cube())
  Scene.push(cube)

  var pix:Pixmap = null

  DesktopApp()  

  override def init(){
    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    capture = new VideoCapture(0)

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    rows = new Array[Byte](w.toInt/2*3)
  	cube.scale.set(1.f, (h/w).toFloat, 1.f)

   //  val sizes = capture.getSupportedPreviewSizes()
   //  for( s <- sizes ){
   //  	println( s"$s.width x $s.height")
  	// }
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
    
    val rsmall = new Mat()
    val small = new Mat()
    Imgproc.resize(img,small, new Size(), 0.5,0.5,0)   // scale down
    Core.flip(small,rsmall,1)   // flip so mirrored
    Imgproc.cvtColor(rsmall,small, Imgproc.COLOR_BGR2RGB)   // convert to rgb


    images += small //img.clone
    if( images.length > h.toInt/2 ) images.remove(0)
    val bb = pix.getPixels()

    for( i <- (0 until images.length)){
      images(images.length-1-i).get(i,0,rows)
      bb.put(rows)
    }
    bb.rewind()


		Texture(0).draw(pix,0,0)

    // live.animate(dt)
  }

}



