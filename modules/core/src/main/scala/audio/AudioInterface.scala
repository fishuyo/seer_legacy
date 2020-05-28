
package com.fishuyo.seer
package audio

import collection.mutable.ListBuffer

import de.sciss.synth.io._


object Audio {
  var interface:Option[AudioInterface] = None
  def apply() = interface.getOrElse({ interface = Some(new NullAudioInterface()); interface.get })

  lazy val out = new Gen {
    def apply() = 0f
  } 
}

class NullAudioInterface extends AudioInterface {
  println( "Warning: No AudioInterface initialized.")
}

case class AudioConfig(bufferSize:Int, sampleRate:Int, channelsIn:Int, channelsOut:Int)

trait AudioInterface {

  var bufferSize = 512 //2048 //256
  var sampleRate = 44100

  var channelsIn = 1
  var channelsOut = 2

  var gain = 1f

  var playThru = false
  var recordThru = true
  var recording = false

  var outFile:AudioFile = null

  val sources = new ListBuffer[AudioSource]

  val in = Array.ofDim[Float](channelsIn, bufferSize)
  val out = Array.ofDim[Float](channelsOut, bufferSize)
  val outInterleaved = new Array[Float](channelsOut * bufferSize)
  val ioBuffer = new AudioIOBuffer(channelsIn, channelsOut, bufferSize, in, out)

  
  // add audio source
  def push(s:AudioSource) = sources += s

  // do any initialization
  def init() = Audio.interface = Some(this)
  
  // start audio
  def start(): Unit ={}

  // stop audio
  def stop(): Unit ={}

  // set master gain
  def setGain(g:Float) =  gain = g 

  // audio passed through automatically
  def playThrough(b:Boolean) = playThru = b

  // when recording, record input directly
  def recordThrough(b:Boolean) = recordThru = b

  def toggleRecording() = {
    if( !recording ){
      try{
        val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channelsOut, sampleRate.toDouble, None, 0)
        var path = "recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" 
        // if( name != "") path = name
        // Gdx.files.external("SeerData/audio").file().mkdirs()
        var file = new java.io.File(path) //Gdx.files.external(path).file()
        outFile = AudioFile.openWrite(file, outSpec)
        recording = true
        println("recording started..")
      } catch { case e:Exception => println(e) }
    } else {
      recording = false
      outFile.close
      println("recording stopped.")
    }
  }
}






