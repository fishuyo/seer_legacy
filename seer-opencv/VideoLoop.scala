
package com.fishuyo
package cv

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

import java.awt.image.BufferedImage

trait VideoSource {
	def videoIO(in:Mat, out:Mat)
}

class VideoLoop extends VideoSource {

  var (recording,playing,stacking,reversing,undoing) = (false,false,false,false,false)
  val images = new ListBuffer[Mat]()
  var frame = 0.f
  var speed = 1.f
  var alpha = 0.1f
  var beta = 1.f-alpha

  def play(){ playing = true; }
  def togglePlay() = playing = !playing
  def stop(){ playing = false; recording = false}
  def rewind(){ frame = 0.f }
  def record(){ recording = true; }
  def toggleRecord() = {
    if(!recording){
    	record()
		}else{
			stop()
      play()
		}
		recording
	}

  def stack() = { stacking = !stacking }
  def reverse() = reversing = !reversing
  def reverse(b:Boolean) = reversing = b
  def clear() = {
  	images.clear()
  	frame = 0.f
  }

  def setSpeed(v:Float) = speed = v
  def setAlpha(a:Float) = {alpha = a; beta = 1.f-alpha}
  def setAlphaBeta(a:Float,b:Float) = {alpha = a; beta = b }

  override def videoIO(in:Mat, out:Mat){

  	if( recording ){
    	images += in.clone
  	}

  	if(playing){
  		if( reversing ){
  			frame -= speed
  		} else {
  			frame += speed
  		}
  	}
  	
  	if(frame < 0.f) frame = images.length-1
  	else if(frame > images.length-1) frame = 0.f

    if(stacking){
      var from = frame
      var to = (if(reversing) frame-speed else frame+speed)
      if( from > to){
        val tmp = from
        from = to
        to = tmp
      }
      for( i<-(from.toInt until to.toInt)){
        var idx = i
        if(i < 0.f) idx = images.length + i
        else if(i > images.length-1) idx = i - images.length

        if( images.length > 0){
          val dest = new Mat()
          Core.addWeighted(in, alpha, images(idx), beta, 0.0, dest)
          images(idx) = dest
        }
      }
    }

  	if( images.length > 0) images(frame.toInt).copyTo(out)
  }



  def writeToFile(path:String="default", scale:Float=1.f, codec:String="mpeg4"){

    if( images.length == 0) return

    var file = path
    if( file == "default") file = "out-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".mov" 

    val w = images(0).width
    val ww = (images(0).width * scale).toInt
    val h = images(0).height
    val hh = (images(0).height * scale).toInt

    val bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR)
    val buf = new Array[Byte](w*h*4)

    val writer = new VideoWriter(path, ww, hh, 30, codec)
    for( i<-(0 until images.length)){

      val mat = images(i)
      mat.get(0,0,buf)
      for( x<-(0 until w); y<-(0 until h)){
        val rgb = (buf(3*(x+y*w)) << 16) + (buf(3*(x+y*w)+1) << 8) + buf(3*(x+y*w)+2)
        bi.setRGB(x,y,rgb)
        // bi.setRGB(0,0,w,h,buf,0,w)
      }

      writer.addFrame(bi)
      // println("add frame")
    }

    writer.close
  }
}

  