
package com.fishuyo
package video

import graphics._

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

class VideoWriter(val path:String, val w:Int, val h:Int, val framerate:Int=30, val codec:String = "mpeg4" ) {

  val dt = DEFAULT_TIME_UNIT.convert( (1000.0/framerate).toInt, MILLISECONDS )
  var t = 0L;

  var file = path
  if( file == "default") file = "out-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-').replace(',','-') + ".mp4" 
  
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
  writer.addVideoStream(0, 0, w, h)
  val resample = IVideoResampler.make(w,h,IPixelFormat.Type.YUV420P,w,h,IPixelFormat.Type.RGBA)


  def addFrame( i: BufferedImage ) = {
    writer.encodeVideo(0, i, t, DEFAULT_TIME_UNIT )
    t += dt
  }

  def addFrame( v: IVideoPicture ) = {
    v.setComplete(true,IPixelFormat.Type.RGBA,w,h,t)
    val out = IVideoPicture.make(IPixelFormat.Type.YUV420P,w,h)
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
  }
}



object ScreenCapture extends GLAnimatable {

  var writer:VideoWriter = null
  var bi:BufferedImage = null
  var w = 0
  var h = 0
  var skip = false
  var recording = false

  def toggleRecord(){
    if( recording ) stop
    else start
  }
  def start(){
    w = Gdx.graphics.getWidth()
    h = Gdx.graphics.getHeight()
    if( w % 2 == 1 ){
      println(s"height and width must be even: $w $h")
      return
    }
    writer = new VideoWriter("default", w, h, 30, "mpeg4" )
    // bi = new BufferedImage(w,h, BufferedImage.TYPE_3BYTE_BGR)
    Video.writer ! Open(writer)
    recording = true
    println("Screen capture started.")
    GLScene.push(this)
  }

  def stop(){
    GLScene.remove(this)
    // writer.close()
    Video.writer ! Close
    println("Screen capture stopped.")
    recording = false
  }

  override def step(dt:Float){

    val bytes = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(true)
    val buffer = IBuffer.make(null, bytes, 0, bytes.length)
    // val pix = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixmap(0,0,w,h)
    // val buffer = IBuffer.make(null, pix.getPixels, 0, w*h*4)
    val picture = IVideoPicture.make(buffer,IPixelFormat.Type.RGBA,w,h)

    Video.writer ! Frame(picture)
    // writer.addFrame(picture)
  }
  
}





import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

case class Frame(frame:IVideoPicture)
case class Open(writer:VideoWriter)
case class Close

object Video {

  val system = ActorSystem("Video")
  val writer = system.actorOf(Props( new VideoWriterActor ), name = "writer")

}

class VideoWriterActor extends Actor {

  var writer:Option[VideoWriter] = None

  // override def preStart(){
  // }

  def receive = {
    case Open(w:VideoWriter) => writer = Some(w)
    case Frame(frame:IVideoPicture) => writer.foreach( _.addFrame(frame) )
    case Close => writer.foreach( _.close )
  }

}



