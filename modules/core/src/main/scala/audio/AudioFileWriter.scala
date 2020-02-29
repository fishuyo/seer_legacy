
package com.fishuyo.seer
package audio

import java.io.File

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

import de.sciss.synth.io._

import collection.mutable.ArrayBuffer


case class Open(path:String)
case class Write(samples:Array[Array[Float]])
case class WriteBuffer(io:AudioIOBuffer, writeInput:Boolean, latency:Float)

class AudioFileWriterActor extends Actor {

  val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, Audio().channelsOut, Audio().sampleRate.toDouble, None, 0)
  var outFile:Option[AudioFile] = None

  // val buffer = new ArrayBuffer[AudioIOBuffer]()
  // var firstBuffer = true
  // var offset = 0

  def receive = {
    case Open(path) => open(path)
    case Write(samples) => outFile.foreach( _.write(samples,0,samples(0).length))
    // case WriteBuffer(io,in,l) => writeBufferWithLatencyCorrection(io,in,l)
    case "close" => close()
  }

  def open(path:String){
    if( outFile.isEmpty ){
      var file:File = null
      if(path == "") file = openDefaultFile()
      else file = new java.io.File(path)

      outFile = Some(AudioFile.openWrite(file, outSpec))
      // firstBuffer = true
      // offset = 0
      println("Audio recording started..")

    } else println("Error AudioFileWriter: File already open!")
  }

  // def writeBufferWithLatencyCorrection(io:AudioIOBuffer, writeInput:Boolean, latency:Float){
  //   if(firstBuffer){
  //     offset = (latency * Audio().sampleRate).toInt
  //     firstBuffer = false
  //   }

  //   if(offset < io.bufferSize){
  //     // b.append( in, count-offset, offset )
  //     offset = 0
  //   } else {
  //     offset -= io.bufferSize
  //     buffer += io.samplesOut
  //   }
  // }

  def close(){
    outFile.foreach( _.close )
    outFile = None
    println("Audio recording stopped.")
  }

  def openDefaultFile() = {
    var file:File = null
    try{
      val form = new java.text.SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")
      val filename = form.format(new java.util.Date()) + ".wav" 
      file = new java.io.File(filename)
      // var path = "SeerData/audio/" + filename
      // Gdx.files.external("SeerData/audio").file().mkdirs()
      // file = Gdx.files.external(path).file()
    } catch { case e:Exception => println("Error AudioFileWriter: failed to open default output file!")}
    file
  }
}

