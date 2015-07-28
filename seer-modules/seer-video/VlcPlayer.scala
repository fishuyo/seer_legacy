
package com.fishuyo.seer
package video

import com.badlogic.gdx.graphics.Pixmap

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
// import uk.co.caprica.vlcj.logger._ //Logger
// import uk.co.caprica.vlcj.log.NativeLog

import com.sun.jna.Memory;
import java.nio.ByteBuffer


class VlcPlayer(val filename:String) extends RenderCallback { self =>

  var frame:ByteBuffer = _
  var (width, height) = (0, 0)
  var size = 0

  // Make pixmap to hold texture data
  var pixmap:Pixmap = _

  // Logger.setLevel(Logger.Level.Error)
  // NativeLog.setLevel(uk.co.caprica.vlcj.binding.internal.libvlc_log_level_e.ERROR)
  new NativeDiscovery().discover()

  val bufferFormatCallback = new BufferFormatCallback() {
      override def getBufferFormat(sourceWidth:Int, sourceHeight:Int):BufferFormat = {
          return new RV32BufferFormat(sourceWidth, sourceHeight);
      }
  }

  var mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
      override def onGetRenderCallback():RenderCallback = {
          return self
      }
  }

  mediaPlayerComponent.getMediaPlayer().playMedia(filename)
  mediaPlayerComponent.getMediaPlayer().setRepeat(true)

  override def display( mediaPlayer:DirectMediaPlayer, nativeBuffer:Array[Memory], bufferFormat:BufferFormat ) {
    if( pixmap == null){
      println(s"Making pixmap: $width x $height")
      width = bufferFormat.getWidth()
      height = bufferFormat.getHeight()
      size = width * height * 4
      pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888)
    }
    frame = nativeBuffer(0).getByteBuffer(0L, size) //.asIntBuffer().get(rgbBuffer(), 0, bufferFormat.getHeight() * bufferFormat.getWidth());
    
    if( frame != null){
      val bb = pixmap.getPixels()
      if( bb == null) return

      // println(s"${nativeBuffer(0).size()} | ${frame.capacity()} $size into ${bb.capacity()}")
      // println(s"$width x $height")

      bb.put( frame )
      bb.rewind()
    }
  }

  def setRate(rate:Float) = mediaPlayerComponent.getMediaPlayer().setRate(rate)

  def togglePlaying() = mediaPlayerComponent.getMediaPlayer().pause()
  // def play(b:Boolean){ playing = b }

  def close(){
    mediaPlayerComponent.release()
  }

}



