
package com.fishuyo.seer
package audio

import actor._

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

case class Record(file:String="")
case class Execute(f:()=>Unit)


object GdxAudio extends AudioInterface {

  // bufferSize = 64
  bufferSize = 512
  
  var actor:ActorRef = _
  // val system = ActorSystem("Audio", myConfig.getConfig("audio"))
  // val actor = system.actorOf(Props( new GdxAudioActor(sampleRate, bufferSize, 2)), name = "audio-main")
  // val actor = system.actorOf(Props( new GdxAudioActor(sampleRate, bufferSize, 2)).withDispatcher("audio-dispatcher"), name = "audio-main")

  // val out = Array.ofDim[Float](channelsOut, bufferSize)

  // def push(o:AudioSource) = actor ! Source(o)

  override def init(): Unit ={
    actor = System().actorOf(Props( new GdxAudioActor()), name = "audio-main")
    println("this is probably outdated and broken..")
    super.init()
  }

  override def start() = { actor ! "connect"; actor ! "play" }
  override def stop() = actor ! "disconnect"
  
  override def toggleRecording() = actor ! Record("")

}


class GdxAudioActor extends Actor { 

  val sampleRate = GdxAudio.sampleRate
  val bufferSize = GdxAudio.bufferSize
  val channelsIn = GdxAudio.channelsIn
  val channelsOut = GdxAudio.channelsOut

  var running = true
  var recording = false
  var writer:ActorRef = _

  var device:AudioDevice = null
  var record:AudioRecorder = null

  val in = Array.ofDim[Float](channelsIn, bufferSize)
  val inShort = new Array[Short](channelsIn * bufferSize)

  val out = Array.ofDim[Float](channelsOut, bufferSize)
  val outInterleaved = new Array[Float](channelsOut * bufferSize)

  val ioBuffer = new AudioIOBuffer(channelsIn, channelsOut, bufferSize, in, out)


  override def preStart(): Unit ={}
  override def preRestart(reason: Throwable,message: Option[Any]): Unit ={println("restarting Audio actor!")}

  def receive = {
    case "connect" => 
      if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, channelsOut == 1 )
      if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, channelsIn == 1 )
    
    case "process" => processLoop()

    case "pause" => running = false;
    case "play" => running = true; self ! "process"

    case Record(name) => toggleRecord(name)

    case "getBuffer" => //sender ! ioBuffer
    case "disconnect" => dispose(); 
    case Execute(f) => f();
  }

  def processLoop(): Unit ={
    if( running ){

      // val out = GdxAudio.out

      // read from input device, convert to float
      record.read(inShort, 0, bufferSize * channelsIn)
      
      var i = 0
      while( i < bufferSize ){
        in(0)(i) = inShort(i).toFloat / 32767.0f
        i += 1
      }

      // zero output buffers
      for( c <- (0 until channelsOut); i <- (0 until bufferSize))
        out(c)(i) = 0f

      // call audio callbacks
      ioBuffer.reset()
      GdxAudio.sources.foreach{ case s => s.audioIO(ioBuffer); ioBuffer.reset() }

      // if playThru set add input to output
      if( GdxAudio.playThru ) AudioPass.audioIO( ioBuffer )

      // copy output buffers to interleaved
      for( i <- 0 until bufferSize; c <- 0 until channelsOut)
        outInterleaved(channelsOut*i + c) = out(c)(i) * GdxAudio.gain
      
      // write samples to audio device
      device.writeSamples(outInterleaved, 0, bufferSize * channelsOut)

      if(recording){
        // if recordThru set and not already done add input to output
        if( GdxAudio.recordThru && !GdxAudio.playThru ) AudioPassInputLatencyCorrection.audioIO( ioBuffer )

        writer ! Write(ioBuffer.outputSamples.map(_.clone)) 
        // outFile.write(out,0,bufferSize)
      }

      self ! "process"
    }
  }

  def toggleRecord(path:String): Unit ={
    if( !recording ){
      writer = System().actorOf(Props( new AudioFileWriterActor()))
      writer ! Open(path)
      AudioPassInputLatencyCorrection.resetLatency(0.03f)
      recording = true
    } else {
      writer ! "close"
      recording = false
    }

    // if( !recording ){
    //   try{
    //     val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channelsOut, sampleRate.toDouble, None, 0)
    //     var path = "SeerData/audio/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" 
    //     if( name != "") path = name
    //     Gdx.files.external("SeerData/audio").file().mkdirs()
    //     var file = Gdx.files.external(path).file()
    //     outFile = AudioFile.openWrite(file, outSpec)
    //     recording = true;
    //     println("Audio recording started..")
    //   } catch { case e:Exception => println(e) }
    // } else {
    //   outFile.close
    //   recording = false
    //   println("Audio recording stopped.")
    // }
  }

  def dispose(): Unit ={
    running = false
    // if( recording ) outFile.close()
    record.dispose
    device.dispose
    record = null
    device = null 
  }

}


