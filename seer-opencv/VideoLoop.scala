
package com.fishuyo
package cv

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

}

  