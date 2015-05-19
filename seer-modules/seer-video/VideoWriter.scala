
package com.fishuyo.seer
package video

import graphics._
import io._
import util._

import java.awt.image.BufferedImage

import java.util.concurrent.TimeUnit._
import com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT
import com.xuggle.mediatool._
import com.xuggle.xuggler.ICodec
import com.xuggle.xuggler.IRational
import com.xuggle.xuggler.IContainer
import com.xuggle.xuggler.IContainerFormat
import com.xuggle.xuggler.IVideoPicture
import com.xuggle.xuggler.IPixelFormat
import com.xuggle.xuggler.IVideoResampler
import com.xuggle.ferry.IBuffer
import com.xuggle.xuggler.IPacket

import com.badlogic.gdx.Gdx

class VideoWriter(val path:String, val w:Int, val h:Int, val scale:Float=1f, val framerate:Int=30, val codec:String = "mpeg4" ) {

  var closing = false

  val dt = DEFAULT_TIME_UNIT.convert( (1000.0/framerate).toInt, MILLISECONDS )
  var t = 0L;

  val sw = if((w*scale).toInt%2 == 1) (w*scale).toInt-1 else (w * scale).toInt
  val sh = if((h*scale).toInt%2 == 1) (h*scale).toInt-1 else (h * scale).toInt

  var name = path
  if( name == ""){
    Gdx.files.external("SeerData/video").file().mkdirs()
    name = "SeerData/video/out-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + "-" + Random.int() + ".mp4" 
  }
  var file = Gdx.files.external(name).file().getPath()
  
  // val container = IContainer.make();
  // val iContainerFormatWriter = IContainerFormat.make();
  // iContainerFormatWriter.setOutputFormat("mov", null, null);

  // container.open(file, IContainer.Type.WRITE, iContainerFormatWriter);
  // val stream = container.addNewStream(0)
  // val streamCoder = stream.getStreamCoder();
  // streamCoder.setTimeBase(IRational.make(1,30)); //No sure you will need this 
  // streamCoder.setCodec(ICodec.findEncodingCodecByName(codec));
  // streamCoder.setWidth(w);
  // streamCoder.setHeight(h);
  // streamCoder.setPixelType(IPixelFormat.Type.RGBA) //here it is
  // streamCoder.open()

  val writer = ToolFactory.makeWriter( file )
  // println( writer.getDefaultPixelType)
  // writer.addVideoStream( 0, 0, ICodec.findEncodingCodecByName(codec), w, h )
  writer.addVideoStream(0, 0, sw, sh)
  val resample = IVideoResampler.make(sw,sh,IPixelFormat.Type.YUV420P,w,h,IPixelFormat.Type.RGBA)


  def addFrame( i: BufferedImage ) = {
    writer.encodeVideo(0, i, t, DEFAULT_TIME_UNIT )
    t += dt
  }

  def addFrame( v: IVideoPicture ) = {
    v.setComplete(true,IPixelFormat.Type.RGBA,w,h,t)
    val out = IVideoPicture.make(IPixelFormat.Type.YUV420P,sw,sh)
    resample.resample(out,v)
    writer.encodeVideo(0, out)
    // val packet = IPacket.make(); 
    // packet.setStreamIndex(0); 
                    
    // if (streamCoder.encodeVideo(packet, v, -1) >= 0) { 
    //   if (packet.isComplete()) { 
    //           container.writePacket(packet, true); 
    //   } 
    // } 
    t += dt
  }

  def close(){
    // streamCoder.close()
    // container.close()
    writer.close()
    closing = false
    println("video writer finished.")
  }
}



object ScreenCapture extends Animatable {

  var writer:VideoWriter = null
  var bi:BufferedImage = null
  var w = 0
  var h = 0
  var skip = false
  var recording = false
  // var closing = false
  var scale = 1f
  var framerate = 15f
  var dtAccum = 0f

  def toggleRecord(){
    if( recording ) stop
    else start
  }
  def start(){
    if( recording ){
      println("already recording..")
      return
    } else if(writer != null && writer.closing){
      println("still closing..")
      return
    }
    w = Gdx.graphics.getWidth()
    h = Gdx.graphics.getHeight()
    if( w % 2 == 1 ){
      println(s"width must be even: $w $h")
      return
    }
    writer = new VideoWriter("", w, h, scale, 15 )
    // bi = new BufferedImage(w,h, BufferedImage.TYPE_3BYTE_BGR)
    // Video.writer ! Open(writer)
    recording = true
    println("Screen capture started.")
    Scene.push(this)
  }

  def stop(){
    if( !recording || (writer != null && writer.closing) ){
      println("still closing..")
      return
    }
    Scene.remove(this)
    // writer.close()
    writer.closing = true
    recording = false
    Video.writer ! Close(writer)
    println("Screen capture stopped.")
  }

  override def animate(dt:Float){

    val timeStep = 1f/framerate
    dtAccum += dt
    if( dtAccum > timeStep ){
      val bytes = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(true)
      // val buffer = IBuffer.make(null, bytes, 0, bytes.length)
      // val pix = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixmap(0,0,w,h)
      // val buffer = IBuffer.make(null, pix.getPixels, 0, w*h*4)
      // val picture = IVideoPicture.make(buffer,IPixelFormat.Type.RGBA,w,h)

      // Video.writer ! Frame(writer,picture)
      Video.writer ! Bytes(writer,bytes,w,h)
      // writer.addFrame(picture)

      dtAccum -= timeStep
    }

    
  }
  
}

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Input.Keys
object ScreenCaptureKey extends InputAdapter {

  def use(){ Inputs.addProcessor(this) }

  override def keyDown(k:Int) = {
    
    k match {
      case Keys.F2 => 
        ScreenCapture.toggleRecord
      case Keys.F3 => 
        // audio.Audio.toggleRecording()
        // ScreenCapture.toggleRecord
      case _ => false
    }
    false
  }
}





import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

case class Frame(writer:VideoWriter, frame:IVideoPicture)
case class Bytes(writer:VideoWriter, bytes:Array[Byte],w:Int,h:Int)
case class Open(writer:VideoWriter)
case class Close(writer:VideoWriter)

object Video {

  val system = ActorSystem("Video")
  val writer = system.actorOf(Props( new VideoWriterActor ), name = "writer")

}

class VideoWriterActor extends Actor {

  // var writer:Option[VideoWriter] = None

  // override def preStart(){
  // }

  def receive = {
    // case Open(w:VideoWriter) => if( writer == None) writer = Some(w) else println("still writing")
    case Frame(writer:VideoWriter, frame:IVideoPicture) => writer.addFrame(frame)
    case Bytes(writer:VideoWriter, bytes:Array[Byte],w:Int,h:Int) => 
      val buffer = IBuffer.make(null, bytes, 0, bytes.length)
      val picture = IVideoPicture.make(buffer,IPixelFormat.Type.RGBA,w,h)
      writer.addFrame(picture)
    case Close(writer:VideoWriter) => writer.close
  }

}



