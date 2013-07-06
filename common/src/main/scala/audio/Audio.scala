
package com.fishuyo
package audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.audio.AudioRecorder

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.ListBuffer

import de.sciss.synth.io._

case class Process
case class Stop
case class Play
case class Gain(g:Float)
case class Record

object Scale {
  var root = 440.f
  var ratios = Array(1.f, 1.1f, 1.3f, 1.67f, 1.8f)
  def note( idx: Int) : Float = {
    var i = idx % ratios.length
    var s = idx.toFloat / ratios.length + 1.f
    root * ratios(i)*s
  }
}

trait AudioSource {
  //def apply():Float = {0.f}
  def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){}
}

object AudioPass extends AudioSource {
  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    for( i<-(0 until numOut)) Array.copy(in,0,out(i),0,numSamples)
  }
}

object Audio extends SimpleAudio(44100, 512, false)

class SimpleAudio(val sampleRate:Int=44100, val bufferSize:Int=512, val mono:Boolean=false) extends Actor {

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

  def act(){
    if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, mono)
    if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    self ! Process
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

            self ! Process
          }
        case Stop => playing = false; stopRecording()
        case Play => playing = true; stopRecording(); self ! Process
        case Gain(g) => gain = g;
        case Record => if( !recording ){
          try{
          val outSpec = new AudioFileSpec(fileType = AudioFileType.Wave, sampleFormat = SampleFormat.Int16, channels, sampleRate.toDouble, None, 0)
          val file = Gdx.files.external("loopFiles/recording-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-') + ".wav" ).file()
          file.mkdirs()
          outFile = AudioFile.openWrite(file, outSpec)
          recording = true; }
          catch { case e:Exception => println(e) }
        }

      }
    }
  }

  def startRecording() = this ! Record
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
    } else this ! Record
  }

  def dispose(){
    playing = false
    device.dispose
    record.dispose
  }

  def push(o:AudioSource) = sources += o
}


