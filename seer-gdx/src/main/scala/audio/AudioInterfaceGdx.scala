
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

case class Connect
case class Disconnect
case class Process
case class Read
case class Source(o:AudioSource)
case class Stop
case class Play
case class Gain(g:Float)
case class PlayThru(b:Boolean)
case class RecordThru(b:Boolean)
case class Record(file:String="")
case class OutBuffer
case class Execute(f:()=>Unit)


object GdxAudio extends AudioInterface {
  
  // create ActorSystem
  // val system = ActorSystem("Audio", myConfig.getConfig("audio"))
  val actor = system.actorOf(Props( new GdxAudioActor(sampleRate, bufferSize, 2)).withDispatcher("audio-dispatcher"), name = "audio-main")

  // ewww hack gross temporary while switching to akka actors
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))

  // def push(o:AudioSource) = actor ! Source(o)

  override def start = { actor ! Connect; actor ! Read; actor ! Process }
  override def stop = actor ! Disconnect
  
  def toggleRecording() = actor ! Record("")

  override def gain(f:Float) = actor ! Gain(f)
  override def playThru(b:Boolean) = actor ! PlayThru(b)
  override def recordThru(b:Boolean) = actor ! RecordThru(b)
}
// object Audio extends Audio

class GdxAudioActor(val sampleRate:Int=44100, val bufferSize:Int=512, val channels:Int=2) extends Actor{ //} with ActorLogging with UnhandledExceptionLogging {

  var t0 = 0.f
  var dt = 0.f
  var gain = 1.f;
  var (playing, recording, playThru, recordThru) = (true,false,false,false)

  var device:AudioDevice = null
  var record:AudioRecorder = null
  val in = Array(new Array[Float](bufferSize), new Array[Float](bufferSize), new Array[Float](bufferSize))
  var inRead = 0
  var inWrite = 0
  val ins = new Array[Short](bufferSize)
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  val out_interleaved = new Array[Float](bufferSize*channels)

  var outFile:AudioFile = null

  // val sources = new ListBuffer[AudioSource]

  override def preStart(){}

  override def preRestart(reason: Throwable,message: Option[Any]){println("restarting Audio actor!")}

  def receive = {
    case Connect => 
      if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, false)
      if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    
    case Process =>
      if( playing ){
        val out = GdxAudio.out

        val t1 = System.currentTimeMillis()
        dt = (t1 - t0) / 1000.f
        t0 = t1

        // from input device, convert to float
        // record.read(ins, 0, bufferSize)
        // for( i <- (0 until bufferSize)) in(i) = ins(i).toFloat / 32767.0f

        // zero output buffers
        for( c <- (0 until channels))
          for( i <- (0 until bufferSize)) out(c)(i) = 0.f

        // call audio callbacks
        GdxAudio.sources.foreach( _.audioIO(in(inRead),out,channels,bufferSize) )

        // if playThru set add input to output
        if( playThru ) AudioPass.audioIO(in(inRead),out,channels,bufferSize)

        // copy output buffers to interleaved
        for( i<-(0 until bufferSize)){
          for( c<-(0 until channels)){
            out_interleaved(channels*i + c) = out(c)(i)*gain
          }
        }

        // write samples to audio device
        device.writeSamples(out_interleaved,0,bufferSize*channels)

        if(recording){
          // if recordThru set and not already done add input to output
          if( recordThru && !playThru ) AudioPass.audioIO(in(inRead),out,channels,bufferSize)

          outFile.write(out,0,bufferSize)
        }

        inRead = (inRead+1) % 3

        self ! Process
      }

    case Read =>
      record.read(ins,0,bufferSize)
      var i=0
      val b = in(inWrite)
      while( i < bufferSize ){
        b(i) = ins(i).toFloat / 32767.0f
        i += 1
      }
      inWrite = (inWrite+1) % 3
      self ! Read

    // case Source(o) => push(o)
    case Stop => playing = false;
    case Play => playing = true; self ! Process
    case Gain(g) => gain = g
    case PlayThru(b) => playThru = b
    case RecordThru(b) => recordThru = b

    case Record(name) => 
      if( !recording ){
        try{
          val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channels, sampleRate.toDouble, None, 0)
          var path = "SeerData/audio/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" 
          if( name != "") path = name
          Gdx.files.external("SeerData/audio").file().mkdirs()
          var file = Gdx.files.external(path).file()
          outFile = AudioFile.openWrite(file, outSpec)
          recording = true;
          println("Audio recording started..")
        } catch { case e:Exception => println(e) }
      } else {
        outFile.close
        recording = false
        println("Audio recording stopped.")
      }

    case OutBuffer => //sender ! out
    case Disconnect => dispose(); //context.stop(self)
    case Execute(f) => f();
  }

  def dispose(){
    playing = false
    if( recording ) outFile.close()
    record.dispose
    device.dispose
    record = null
    device = null 
  }

  // def push(o:AudioSource) = GdxAudio.sources += o
}


