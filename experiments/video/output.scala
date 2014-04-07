package com.fishuyo.seer
package examples.video

import video._

import java.awt.image.BufferedImage

import java.util.concurrent.TimeUnit._
import com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT
import com.xuggle.mediatool._
import com.xuggle.xuggler.ICodec
import com.xuggle.ferry.IBuffer
import com.xuggle.xuggler.IVideoPicture
import com.xuggle.xuggler.IPixelFormat

object Main extends App {

	val w = 100
	val h = 100
	val path = "test.mov"
	val codec = "mpeg4"

	val bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR)
  val writer = new VideoWriter(path, w, h, 30, codec)

  // val buffer = IBuffer.make(null, w*h*3)
  // val v = IVideoPicture.make(buffer, IPixelFormat.Type.RGB24, w,h)
  // val bytes = new Array[Byte](w*h*3)
  
  for( i<-(0 until 240)){
  	for( x<-(0 until w); y<-(0 until h)){
  		// bytes(x+y*w) = x.toByte
  		// bytes(x+y*w+1) = y.toByte
  		// bytes(x+y*w+2) = i.toByte
  		bi.setRGB(x,y, (i << 16) + (y << 8) + x )
  	}
  	// buffer.put(bytes,0,0,w*h*3)
  	// writer.addFrame(v)
  	writer.addFrame(bi)
  }
  
  writer.close

}



