
package com.fishuyo.seer
package video

import scala.collection.mutable.Queue

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

import com.badlogic.gdx.graphics.Pixmap

class VideoPlayer(val filename:String) {

  var playing = false
	var queue = new Queue[IVideoPicture]

  // Create a Xuggler container object
  var container = IContainer.make();

  // Open up the container
  if (container.open(filename, IContainer.Type.READ, null) < 0){
  	println("failed to load file " + filename)
  }

  // query how many streams the call to open found
  val numStreams = container.getNumStreams();

  // and iterate through the streams to find the first video stream
  var videoStreamId = -1;
  var videoCoder:IStreamCoder = null;
  for( i <- (0 until numStreams)){
    // Find the stream object
    val stream:IStream = container.getStream(i);
    // Get the pre-configured decoder that can decode this stream;
    val coder:IStreamCoder = stream.getStreamCoder();

    if (videoCoder == null && coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO){
      videoStreamId = i;
      videoCoder = coder;
    }
  }
  if (videoStreamId == -1)
    throw new RuntimeException("could not find video stream in container: "
        +filename);

  /*
   * Now we have found the video stream in this file.  Let's open up our decoder so it can
   * do work.
   */
  if (videoCoder.open() < 0)
    throw new RuntimeException("could not open video decoder for container: "
        +filename);

  var resampler:IVideoResampler = null;
  if (videoCoder.getPixelType() != IPixelFormat.Type.RGB24){
    // if this stream is not in BGR24, we're going to need to
    // convert it.  The VideoResampler does that for us.
    resampler = IVideoResampler.make(videoCoder.getWidth(), 
        videoCoder.getHeight(), IPixelFormat.Type.RGB24,
        videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
    if (resampler == null)
      throw new RuntimeException("could not create color space " +
      		"resampler for: " + filename);
  }

  /*
   * Now, we start walking through the container looking at each packet.
   */
  var packet:IPacket = IPacket.make();
  val firstTimestampInStream:Long = Global.NO_PTS;
  var systemClockStartTime:Long = 0;


  var pixmap = new Pixmap(videoCoder.getWidth(), videoCoder.getHeight(), Pixmap.Format.RGB888)
  var dtAccum = 0.0

  val reader = Video.system.actorOf(Props( new VideoBufferingActor(this) ), name = "reader")
  reader ! "decodeFrame"

  def togglePlaying(){ playing = !playing }
  def playing(b:Boolean){ playing = b }

  def animate(dt:Float){

    if( !playing ) return

  	val framerate = videoCoder.getFrameRate().getDouble()
  	val timeStep = 1f/framerate
    dtAccum += dt.toDouble
    if( dtAccum > timeStep ){
    	dtAccum -= timeStep

    	if( !queue.isEmpty){
    		val v = queue.dequeue
		  	val bb = pixmap.getPixels()
        if( v == null) return
        if( bb == null) return
		  	bb.put(v.getByteBuffer())
		  	bb.rewind()
			}
    
    }
  }

 	def close(){
	  if (videoCoder != null)
	  {
	    videoCoder.close();
	    videoCoder = null;
	  }
	  if (container !=null)
	  {
	    container.close();
	    container = null;
	  }
	}

}

class VideoBufferingActor(val player:VideoPlayer) extends Actor {

	import player._

  // override def preStart(){
  // }

  def receive = {
  	case "decodeFrame" => {
      if( queue.size < 60){
  		if( container.readNextPacket(packet) >= 0){

	      if (packet.getStreamIndex() == videoStreamId){

	        val picture = IVideoPicture.make(videoCoder.getPixelType(),
	            videoCoder.getWidth(), videoCoder.getHeight());

	        var offset = 0;
	        while(offset < packet.getSize()){

	          val bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
	          if (bytesDecoded < 0)
	            throw new RuntimeException("got error decoding video in: "
	                + filename);
	          offset += bytesDecoded;

	          if (picture.isComplete()){
	            var newPic:IVideoPicture = picture;

	            if (resampler != null){
	              // we must resample
	              newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
	                  picture.getWidth(), picture.getHeight());
	              if (resampler.resample(newPic, picture) < 0)
	                throw new RuntimeException("could not resample video from: "
	                    + filename);
	            }
	            if (newPic.getPixelType() != IPixelFormat.Type.RGB24)
	              throw new RuntimeException("could not decode video" +
	              		" as BGR 24 bit data in: " + filename);

	            queue += newPic
	          }
	        }
	      }
	    } else { container.seekKeyFrame(videoStreamId,0,0)}
      }
	    if( queue.size > 60) Thread.sleep(10)
	    self ! "decodeFrame"
  	}
    case "close" => ()
  }

}