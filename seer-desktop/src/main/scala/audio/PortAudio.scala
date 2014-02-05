
package com.fishuyo.seer
package audio

import com.badlogic.gdx.Gdx

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory

import collection.mutable.ListBuffer

import de.sciss.synth.io._

import com.github.rjeschke.jpa._

object PortAudio {
	val sampleRate = 44100
  val bufferSize = 512
  val channelsIn = 1
  val channelsOut = 2

  val sources = new ListBuffer[AudioSource]

  val in = new Array[Float](bufferSize)
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  val outInterleaved = new Array[Float](bufferSize*channelsOut)

  var gain = 1.f
  var playThru = false
  var recordThru = true
  var recording = false

  var outFile:AudioFile = null

  var callback = new PaCallback {
  	def paCallback(paIn:PaBuffer, paOut:PaBuffer, nframes:Int){

        paIn.getFloatBuffer.get(in)

        // zero output buffers
        for( c <- (0 until channelsOut))
          for( i <- (0 until bufferSize)) out(c)(i) = 0.f

        // call audio callbacks
        sources.foreach( _.audioIO(in,out,channelsOut,bufferSize) )

        // if playThru set add input to output
        if( playThru ) AudioPass.audioIO(in,out,channelsOut,bufferSize)

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
          if( recordThru && !playThru ) AudioPass.audioIO(in,out,channelsOut,bufferSize)

          outFile.write(out,0,bufferSize)
        }

  	}
  }

  def initialize(){
  	JPA.initialize()
  	JPA.setCallback( callback )
  	JPA.openDefaultStream(channelsIn, channelsOut, PaSampleFormat.paFloat32, sampleRate, bufferSize)
  }

  def start() = JPA.startStream 
  def stop() = JPA.stopStream

  def toggleRecording() = {
    if( !recording ){
      try{
        val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channelsOut, sampleRate.toDouble, None, 0)
        var path = "SeerData/audio/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" 
        // if( name != "") path = name
        Gdx.files.external("SeerData/audio").file().mkdirs()
        var file = Gdx.files.external(path).file()
        outFile = AudioFile.openWrite(file, outSpec)
        recording = true
        println("Audio recording started..")
      } catch { case e:Exception => println(e) }
    } else {
      recording = false
      outFile.close
      println("Audio recording stopped.")
    }
  }
  
  def push(o:AudioSource) = sources += o

  def gain(f:Float) = {} //actor ! Gain(f)
  def playThru(b:Boolean) = {} //actor ! PlayThru(b)
  def recordThru(b:Boolean) = {} //actor ! RecordThru(b)

}



