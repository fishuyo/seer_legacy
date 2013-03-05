
package com.fishuyo
package audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.audio.AudioRecorder

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.ListBuffer

case class Process
case class Stop
case class Play
case class Gain(g:Float)

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
  def audioIO( in:Array[Float], out:Array[Float], numSamples:Int){}
}

object AudioPass extends AudioSource {
  override def audioIO( in:Array[Float], out:Array[Float], numSamples:Int){
    Array.copy(in,0,out,0,numSamples)
  }
}

object Audio extends SimpleAudio(44100, 512)

class SimpleAudio(val sampleRate:Int=44100, val bufferSize:Int=1024) extends Actor {

  var gain = .5f;
  var playing = true;
  var device:AudioDevice = null
  var record:AudioRecorder = null
  val in = new Array[Float](bufferSize)
  val ins = new Array[Short](bufferSize)
  val out = new Array[Float](bufferSize)

  val sources = new ListBuffer[AudioSource]

  def act(){
    if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, true)
    if( record == null) record = Gdx.audio.newAudioRecorder(sampleRate, true)
    self ! Process
    loop{
      react{
        case Process => if( playing ){

            record.read(ins,0,bufferSize)
            //device.writeSamples(ins,0,bufferSize)
            for( i <-( 0 until bufferSize)) in(i) = ins(i).toFloat / 32767.0f

            sources.foreach( _.audioIO(in,out,bufferSize) )

            for( i<-(0 until bufferSize)) out(i) *= gain
            device.writeSamples(out,0,bufferSize)
            self ! Process
          }
        case Stop => playing = false
        case Play => playing = true; self ! Process
        case Gain(g) => gain = g;

      }
    }
  }

  def dispose(){
    device.dispose
  }

  def push(o:AudioSource) = sources += o
}


