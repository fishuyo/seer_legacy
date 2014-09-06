
package com.fishuyo.seer
package audio

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory

import collection.mutable.ListBuffer

import de.sciss.synth.io._

import com.github.rjeschke.jpa._

object PortAudio extends AudioInterface {

  val in = Array(new Array[Float](bufferSize))

  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  val outInterleaved = new Array[Float](bufferSize*channelsOut)

  val ioBuffer = new AudioIOBuffer(channelsIn,channelsOut,bufferSize,in,out)

  var callback = new PaCallback {
  	def paCallback(paIn:PaBuffer, paOut:PaBuffer, nframes:Int){

        paIn.getFloatBuffer.get(in(0))

        // zero output buffers
        for( c <- (0 until channelsOut))
          for( i <- (0 until bufferSize)) out(c)(i) = 0.f

        // call audio callbacks
        ioBuffer.reset
        sources.foreach{ case s=> s.audioIO( ioBuffer ); ioBuffer.reset } //in,out,channelsOut,bufferSize) )

        // if playThru set add input to output
        if( playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

        // copy output buffers to interleaved
        for( i<-(0 until bufferSize)){
          for( c<-(0 until channelsOut)){
            outInterleaved(channelsOut*i + c) = out(c)(i)*gain
          }
        }

        // write samples to audio device
        paOut.getFloatBuffer.put(outInterleaved)

        if(recording){
          // if recordThru set and not already done add input to output
          if( recordThru && !playThru ) AudioPass.audioIO( ioBuffer ) //in,out,channelsOut,bufferSize)

          outFile.write(out,0,bufferSize)
        }

  	}
  }

  override def init(){
  	try{ JPA.initialize() }
    catch { case e:Exception => println(e) }
    
  	JPA.setCallback( callback )
  	JPA.openDefaultStream(channelsIn, channelsOut, PaSampleFormat.paFloat32, sampleRate, bufferSize)
    super.init
  }

  override def start() = JPA.startStream 
  override def stop() = JPA.stopStream

  // override def gain(f:Float){}
  // override def playThru(b:Boolean){}
  // override def recordThru(b:Boolean){}

}



