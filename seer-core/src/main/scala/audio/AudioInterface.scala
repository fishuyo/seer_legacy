
package com.fishuyo.seer
package audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.audio.AudioRecorder

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer

import de.sciss.synth.io._


trait AudioInterface {
  var bufferSize = 2048
  var sampleRate = 44100

  var channelsIn = 1
  var channelsOut = 2

  var gain = 1.f
  var playThru = false
  var recordThru = true
  var recording = false

  var outFile:AudioFile = null

  val sources = new ListBuffer[AudioSource]
  
  // add audio source
  def push(s:AudioSource) = sources += s

  // start audio
  def start{}

  // stop audio
  def stop{}

  // set master gain
  def gain(g:Float){}

  // audio passed through automatically
  def playThru(b:Boolean){}

  // when recording, record input directly
  def recordThru(b:Boolean){}


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
        println("recording started..")
      } catch { case e:Exception => println(e) }
    } else {
      recording = false
      outFile.close
      println("recording stopped.")
    }
  }
}






