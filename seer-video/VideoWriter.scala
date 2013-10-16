
package com.fishuyo
package video

import graphics._

import java.awt.image.BufferedImage

import java.util.concurrent.TimeUnit._
import com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT
import com.xuggle.mediatool._
import com.xuggle.xuggler.ICodec
import com.xuggle.xuggler.IVideoPicture

import com.badlogic.gdx.Gdx

class VideoWriter(val path:String, val w:Int, val h:Int, val framerate:Int=30, val codec:String = "mpeg4" ) {

  val dt = DEFAULT_TIME_UNIT.convert( (1000.0/framerate).toInt, MILLISECONDS )
  var t = 0L;

  var file = path
  if( file == "default") file = "out-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-').replace(',','-') + ".mov" 
  
  val writer = ToolFactory.makeWriter( file )

  writer.addVideoStream( 0, 0, ICodec.findEncodingCodecByName(codec), w, h )

  def addFrame( i: BufferedImage ) = {
    writer.encodeVideo(0, i, t, DEFAULT_TIME_UNIT )
    t += dt
  }
  def addFrame( v: IVideoPicture ) = {
    writer.encodeVideo(0, v)
    t += dt
  }

  def close() = writer.close()
}



object ScreenCapture extends GLAnimatable {

  var writer:VideoWriter = null
  var bi:BufferedImage = null
  var w = 0
  var h = 0

  def start(){
    w = Gdx.graphics.getWidth()
    h = Gdx.graphics.getHeight()
    writer = new VideoWriter("default", w, h )
    bi = new BufferedImage(w,h, BufferedImage.TYPE_3BYTE_BGR)
    GLScene.push(this)
  }

  def stop(){
    GLScene.remove(this)
    writer.close()
  }

  override def step(dt:Float){
    val bytes = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(false)
    for( x<-(0 until w); y<-(0 until h)){
      val rgb = (bytes(4*(x+y*w)) << 16) + (bytes(4*(x+y*w)+1) << 8) + bytes(4*(x+y*w)+2)
      bi.setRGB(x,y,rgb)
    }
    writer.addFrame(bi)
  }
  
}
