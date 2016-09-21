package com.fishuyo.seer
package examples.opencv

import graphics._
import io._
import spatial._
// import dynamic._
import cv._

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{ Texture => GdxTexture }
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

object SlitScan extends SeerApp {

  var capture: VideoCapture = _

  val images = new ListBuffer[Mat]()

  var rows:Array[Byte] = null
  var w = 0.0
  var h = 0.0

  val quad = Plane()

  var pix:Pixmap = null
  var texture:GdxTexture = null
  var inited = false

  override def init(){
    OpenCV.loadLibrary()

    capture = new VideoCapture(0)

    if(!capture.isOpened()) println("Capture device failed to open.")

    w = capture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
    println( s"dim: $w x $h")

    pix = new Pixmap(w.toInt/2,h.toInt/2, Pixmap.Format.RGB888)
    rows = new Array[Byte](w.toInt/2*3)

    quad.scale.set(-1f, (-h/w).toFloat, 1f)

   //  val sizes = capture.getSupportedPreviewSizes()
   //  for( s <- sizes ){
   //   println( s"$s.width x $s.height")
    // }
    texture = new GdxTexture(pix) 

    quad.material = Material.basic
    quad.material.texture = Some(texture)
    quad.material.textureMix = 1f
    inited = true
  }
  override def draw(){
    quad.draw()
  }

  override def animate(dt:Float){

    if(!inited) init()

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


    texture.draw(pix,0,0)

    // live.animate(dt)
  }

}



