package com.fishuyo.seer
package examples.opencv

import graphics._
import io._
import spatial._
// import dynamic._
import cv._

import scala.collection.mutable.ListBuffer

// import com.badlogic.gdx.graphics.Pixmap
// import com.badlogic.gdx.graphics.{ Texture => GdxTexture }
// import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.imgproc._

object SlitScan extends SeerApp {

  var capture:Webcam = _

  val images = new ListBuffer[Mat]()

  var rows:Array[Byte] = null
  var w = 0.0
  var h = 0.0

  var image:Image = _
  var texture:Texture = _
  val quad = Plane()

  override def init(){
    OpenCV.loadLibrary()

    capture = Webcam(0) //new VideoCapture(0)

    if(!capture.isOpened()) println("Capture device failed to open.")

    w = capture.width //get(Highgui.CV_CAP_PROP_FRAME_WIDTH)
    h = capture.height //get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
    println( s"dim: $w x $h")

    rows = new Array[Byte](w.toInt/10*3)

    image = Image(w.toInt/10,h.toInt/10,3,1)
    texture = Texture(image) 

    quad.material = Material.basic
    quad.material.loadTexture(texture)
    quad.scale.set(-1f, (-h/w).toFloat, 1f)
  }

  override def draw(){
    quad.draw()
  }

  override def animate(dt:Float){

    val img = new Mat()
    val read = capture.read(img)

    if( !read ) return
    
    val rsmall = new Mat()
    val small = new Mat()
    Imgproc.resize(img,small, new Size(), 0.1,0.1,0)   // scale down
    Core.flip(small,rsmall,1)   // flip so mirrored
    Imgproc.cvtColor(rsmall,small, Imgproc.COLOR_BGR2RGB)   // convert to rgb


    images += small //img.clone
    if( images.length > h.toInt/10 ) images.remove(0)

    for( i <- (0 until images.length)){
      images(images.length-1-i).get(i,0,rows)
      image.buffer.put(rows)
    }

    texture.update()
  }

}



