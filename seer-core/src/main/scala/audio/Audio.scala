
package com.fishuyo
package audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.audio.AudioRecorder

// import actors.Actor
// import actors.Actor._

import akka.actor._
import akka.actor.Props
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer

import de.sciss.synth.io._

case class Connect
case class Disconnect
case class Process
case class Source(o:AudioSource)
case class Stop
case class Play
case class Gain(g:Float)
case class Record(file:String="")
case class OutBuffer


trait AudioSource {
  //def apply():Float = {0.f}
  def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){}
}

object AudioPass extends AudioSource {
  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    for( i<-(0 until numOut)) Array.copy(in,0,out(i),0,numSamples)
  }
}

trait UnhandledExceptionLogging{
  self: Actor with ActorLogging =>

  override def preRestart(reason:Throwable, message:Option[Any]){
    println(reason + "\n" + message)
  }
}

object Audio {
  var bufferSize = 512
  val sources = new ListBuffer[AudioSource]

  val system = ActorSystem("Audio")
  val actor = system.actorOf(Props( new AudioActor(44100, 512, 2)), name = "main")

  // ewww hack gross temporary while switching to akka actors
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))

  def push(o:AudioSource) = actor ! Source(o)

  def start = { actor ! Connect; actor ! Process }
  def dispose = actor ! Disconnect
  def toggleRecording() = actor ! Record("")
}

class AudioActor(val sampleRate:Int=44100, val bufferSize:Int=512, val channels:Int=2) extends Actor{ //} with ActorLogging with UnhandledExceptionLogging {

  var t0 = 0.f
  var dt = 0.f
  var gain = .5f;
  var playing = true;
  var recording = false;
  var device:AudioDevice = null
  var record:AudioRecorder = null
  val in = new Array[Float](bufferSize)
  val ins = new Array[Short](bufferSize)
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  val out_interleaved = new Array[Float](bufferSize*channels)

  var outFile:AudioFile = null

  val sources = new ListBuffer[AudioSource]

  override def preStart(){}

  override def preRestart(reason: Throwable,message: Option[Any]){println("restarting Audio actor!")}

  def receive = {
    case Connect => 
      if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, false)
      if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    
    case Process =>
      if( playing ){
        val out = Audio.out

        val t1 = System.currentTimeMillis()
        dt = (t1 - t0) / 1000.f
        t0 = t1

        // from input device, convert to float
        record.read(ins, 0, bufferSize)
        for( i <- (0 until bufferSize)) in(i) = ins(i).toFloat / 32767.0f

        // zero output buffers
        for( c <- (0 until channels))
          for( i <- (0 until bufferSize)) out(c)(i) = 0.f

        // call audio callbacks
        sources.foreach( _.audioIO(in,out,channels,bufferSize) )

        // copy output buffers to interleaved
        for( i<-(0 until bufferSize)){
          for( c<-(0 until channels)){
            out_interleaved(channels*i + c) = out(c)(i)*gain
          }
        }
        device.writeSamples(out_interleaved,0,bufferSize*channels)

        if(recording) outFile.write(out,0,bufferSize)

        self ! Process
      }

    case Source(o) => push(o)
    case Stop => playing = false;
    case Play => playing = true; self ! Process
    case Gain(g) => gain = g

    case Record(name) => 
      if( !recording ){
        try{
          val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channels, sampleRate.toDouble, None, 0)
          var path = "SeerData/audio/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" 
          if( name != "") path = name
          var file = Gdx.files.external(path).file()
          file.mkdirs()
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
  }

  def dispose(){
    playing = false
    if( recording ) outFile.close()
    // device.dispose
    // record.dispose
  }

  def push(o:AudioSource) = sources += o
}




// old audio actor
object SimpleAudio extends SimpleAudio(44100,512,false)

class SimpleAudio(val sampleRate:Int=44100, val bufferSize:Int=512, val mono:Boolean=false) extends actors.Actor {

  var t0 = 0.f
  var dt = 0.f
  var gain = .5f;
  var playing = true;
  var recording = false;
  var device:AudioDevice = null
  var record:AudioRecorder = null
  val in = new Array[Float](bufferSize)
  val ins = new Array[Short](bufferSize)
  val channels = if(mono) 1 else 2
  val out = Array(new Array[Float](bufferSize), new Array[Float](bufferSize))
  val out_interleaved = new Array[Float](bufferSize*channels)

  var outFile:AudioFile = null

  val sources = new ListBuffer[AudioSource]

  // override def preStart(){
  // }

  def act(){
    if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, mono)
    if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    actors.Actor.self ! Process
    loop{
      react{
        case Process => if( playing ){
          val t1 = System.currentTimeMillis()
          dt = (t1 - t0) / 1000.f
          t0 = t1
          // from input device, convert to float
          record.read(ins,0,bufferSize)
          for( i <-( 0 until bufferSize)) in(i) = ins(i).toFloat / 32767.0f
          // zero output buffers
          for( c<-(0 until channels))
            for( i<-(0 until bufferSize)) out(c)(i) = 0.f

          // call audio callbacks
          sources.foreach( _.audioIO(in,out,channels,bufferSize) )

          // copy output buffers to interleaved
          for( i<-(0 until bufferSize)){
            for( c<-(0 until channels)){
              out_interleaved(channels*i + c) = out(c)(i)*gain
            }
          }
          device.writeSamples(out_interleaved,0,bufferSize*channels)

          if(recording) outFile.write(out,0,bufferSize)

          actors.Actor.self ! Process
        }
  // def receive = {
        case Connect => 
          if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, mono)
          if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    
    // case Process => if( playing ){

    //     val out = Audio.out

    //     val t1 = System.currentTimeMillis()
    //     dt = (t1 - t0) / 1000.f
    //     t0 = t1
    //     // from input device, convert to float
    //     record.read(ins,0,bufferSize)
    //     for( i <-( 0 until bufferSize)) in(i) = ins(i).toFloat / 32767.0f

    //     // zero output buffers
    //     for( c<-(0 until channels))
    //       for( i<-(0 until bufferSize)) out(c)(i) = 0.f

    //     // call audio callbacks
    //     Audio.sources.foreach( _.audioIO(in,out,channels,bufferSize) )

    //     // copy output buffers to interleaved
    //     for( i<-(0 until bufferSize)){
    //       for( c<-(0 until channels)){
    //         out_interleaved(channels*i + c) = out(c)(i)*gain
    //       }
    //     }
    //     device.writeSamples(out_interleaved,0,bufferSize*channels)

    //     if(recording) outFile.write(out,0,bufferSize)

    //     self ! Process
    //   }
    //case Push(a) => push(a)
        case Stop => playing = false; stopRecording()
        case Play => playing = true; stopRecording(); actors.Actor.self ! Process
        case Gain(g) => gain = g;
        case Record => if( !recording ){
          try{
            val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channels, sampleRate.toDouble, None, 0)
            val file = Gdx.files.external("loopFiles/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" ).file()
            file.mkdirs()
            outFile = AudioFile.openWrite(file, outSpec)
            recording = true; }
          catch { case e:Exception => println(e) }
        } else {
          outFile.close
          recording = false
        }
        //case OutBuffer => sender ! out
        case "dispose" => dispose(); //context.stop(self)
      }
    }
  }

  def startRecording() = actors.Actor.self ! Record
  def stopRecording() = {
    if(recording){
      recording = false
      Thread.sleep(100)
      outFile.close
    }
  }
  def toggleRecording() = {
    if(recording){
      recording = false
      Thread.sleep(100)
      outFile.close
    } else actors.Actor.self ! Record
  }

  def dispose(){
    playing = false
    device.dispose
    record.dispose
  }

  def push(o:AudioSource) = sources += o
}



