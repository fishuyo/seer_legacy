
package com.fishuyo.seer
package audio

// import akka.actor._
// import akka.actor.Props
// import com.typesafe.config.ConfigFactory

import collection.mutable.ListBuffer

import de.sciss.synth.io._

import org.jrtaudio._

object RtAudio extends AudioInterface {

  var rtaudio:JRtAudio = _

  val in = Array(new Array[Float](bufferSize))

  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  // val outInterleaved = new Array[Float](bufferSize*channelsOut)

  bufferSize = 512
  val ioBuffer = new AudioIOBuffer(channelsIn,channelsOut,bufferSize,in,out)

  // var callback = new PaCallback {
  	// def paCallback(paIn:PaBuffer, paOut:PaBuffer, nframes:Int){
  def callback(outputBuffer:Array[Float], inputBuffer:Array[Float], nBufferFrames:Int, streamTime:Double, status:Int){

    in(0) = inputBuffer

    // zero output buffers
    for( c <- (0 until channelsOut))
      java.util.Arrays.fill(out(c), 0f)

    // call audio callbacks
    ioBuffer.reset
    sources.foreach{ case s => s.audioIO( ioBuffer ); ioBuffer.reset } 

    // if playThru set add input to output
    if( playThru ) AudioPass.audioIO( ioBuffer )

    // copy output buffers to interleaved
    // for( i<-(0 until bufferSize)){
      for( c<-(0 until channelsOut)){
        System.arraycopy(out(c), 0, outputBuffer, c*nBufferFrames, nBufferFrames);

        // outInterleaved(channelsOut*i + c) = out(c)(i)*gain
      }
    // }


    // write samples to audio device
    // paOut.getFloatBuffer.put(outInterleaved)

    if(recording){
      // if recordThru set and not already done add input to output
      if( recordThru && !playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

      outFile.write(out,0,bufferSize)
    }
  }
  // }

  // def callback(outputBuffer:Array[Float], inputBuffer:Array[Float], nBufferFrames:Int, streamTime:Double, status:Int){
  // for(int i=0;i<nBufferFrames;i++){
  // outputBuffer[i] = 0f;
  // for(float period: oscillators)
  // outputBuffer[i] += mysin((i+counter)*period);
  // outputBuffer[i] /= oscillators.length;
  // }
  // System.arraycopy(outputBuffer, 0, outputBuffer, nBufferFrames, nBufferFrames);
  // counter += nBufferFrames;
  // return 0;
  // }

  override def init(){
    rtaudio = new JRtAudio();
    rtaudio.openStreamOut(this);
    super.init
  }

  override def start() = rtaudio.startStream()
  override def stop() = rtaudio.closeStream()

  // override def gain(f:Float){}
  // override def playThru(b:Boolean){}
  // override def recordThru(b:Boolean){}

}



