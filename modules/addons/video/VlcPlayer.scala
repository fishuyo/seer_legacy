
package seer
package video

import graphics.Texture
import graphics.Plane
import graphics.Material 
import graphics.Animatable 

import com.badlogic.gdx.graphics.Pixmap

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
// import uk.co.caprica.vlcj.logger._ //Logger
// import uk.co.caprica.vlcj.log.NativeLog

import uk.co.caprica.vlcj.binding.LibVlc;

import com.sun.jna.Memory;
import java.nio.ByteBuffer

// object VLC {
//   def loadNatives() = new NativeDiscovery().discover()
// }

class VlcPlayer(val uri:String) extends RenderCallback { self =>

  var frame:ByteBuffer = _
  var (width, height) = (0, 0)
  var size = 0
  var loaded = false
  var playing = true

  // Logger.setLevel(Logger.Level.Error)
  // NativeLog.setLevel(uk.co.caprica.vlcj.binding.internal.libvlc_log_level_e.ERROR)
  val found = new NativeDiscovery().discover()
  if(found) println(s"VLC natives found version ${LibVlc.INSTANCE.libvlc_get_version()}")
  else println("Error: VLC natives not found.")

  val bufferFormatCallback = new BufferFormatCallback() {
      override def getBufferFormat(sourceWidth:Int, sourceHeight:Int):BufferFormat = {
          return new RV32BufferFormat(sourceWidth, sourceHeight);
      }
  }

  var mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
      override def onGetRenderCallback():RenderCallback = {
        return self
      }
      override def newMedia(mediaPlayer:MediaPlayer){
        loaded = true
        // mediaPlayer.stop()
        mediaPlayer.setVolume(0)
      }
  }

  mediaPlayerComponent.getMediaPlayer().setRepeat(true)
  mediaPlayerComponent.getMediaPlayer().setPlaySubItems(true);
  // mediaPlayerComponent.getMediaPlayer().playMedia(uri)
  mediaPlayerComponent.getMediaPlayer().prepareMedia(uri)

  override def display( mediaPlayer:DirectMediaPlayer, nativeBuffer:Array[Memory], bufferFormat:BufferFormat ) {
    // if( texture == null){
      // println(s"Making texture: $width x $height")
      width = bufferFormat.getWidth()
      height = bufferFormat.getHeight()
      size = width * height * 4
      // texture = new Texture(width, height)
    // }
    frame = nativeBuffer(0).getByteBuffer(0L, size) 
    // texture.data = nativeBuffer(0).getByteBuffer(0L, size) 
    
  }

  def load(uri:String) = mediaPlayerComponent.getMediaPlayer().prepareMedia(uri)
  
  def isLoaded() = ( width != 0 )
  def play() = mediaPlayerComponent.getMediaPlayer().play()
  def stop() = mediaPlayerComponent.getMediaPlayer().stop()
  def setPosition(pos:Float) = mediaPlayerComponent.getMediaPlayer().setPosition(pos)
  def setRate(rate:Float) = mediaPlayerComponent.getMediaPlayer().setRate(rate)
  def setVolume(volume:Float) = mediaPlayerComponent.getMediaPlayer().setVolume((volume*100f).toInt)
  def setAudioChannel(channel:Int) = mediaPlayerComponent.getMediaPlayer().setAudioChannel(channel)

  def togglePlaying() = mediaPlayerComponent.getMediaPlayer().pause()
  def setPause(b:Boolean) = mediaPlayerComponent.getMediaPlayer().setPause(b)

  def dispose(){
    mediaPlayerComponent.release()
  }

}

class VideoTexture(uri:String) extends VlcPlayer(uri) with Animatable {

  var texture:Texture = _
  var quad = Plane()
  var initd = false
  var scale = 1f

  override def init(){
    var wait = 0
    while(!isLoaded && wait < 10){
      Thread.sleep(100)
      wait += 1
    }
    texture = new Texture(width,height)
    texture.format = org.lwjgl.opengl.GL12.GL_BGRA
    texture.allocate(width,height)
    texture.init()
    quad.material = Material.basic
    quad.material.loadTexture(texture)
    quad.scale(1,-height*1f/width,1)
    initd = true
  }

  override def draw(){ quad.draw }
  override def animate(dt:Float){
    update()
    quad.scale.set(scale, -height*scale/width, 1)
  }
  def update(){
    if(!initd) init()
    if(frame == null) return
    texture.buffer = frame
    texture.update
  }

}



