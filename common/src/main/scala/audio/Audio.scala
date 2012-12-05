
package com.fishuyo
package audio

import com.badlogic.gdx._
import com.badlogic.gdx.audio.AudioDevice

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

trait AudioSource{
  def audioCallback( in:Array[Float], out:Array[Float], numSamples:Int){}
}

class TriangleWave(var frequency:Float = 440.f) extends AudioSource {
  var v = 0.f
  var inc:Float = 1.f/(1.f/frequency * 44100.f / 4.f)

  def f(f:Float) = {
    frequency = f
    var i = 1.f/(1.f/frequency * 44100.f / 4.f)
    if (inc > 0.f) inc = i
    else inc = -i
  }

  override def audioCallback(in:Array[Float], out:Array[Float], numSamples:Int){
    for( i<- 0 until numSamples){
      out(i) += v;
      v += inc;
      if( v >= 1.f || v <= -1.f) inc *= -1.f
    }
  }
}

object Audio extends SimpleAudio(44100, 1024)

class SimpleAudio(val sampleRate:Int=44100, val bufferSize:Int=1024) extends Actor {

  var gain = .5f;
  var playing = true;
  var device:AudioDevice = null
  val in = new Array[Float](bufferSize)
  val out = new Array[Float](bufferSize)

  val sources = new ListBuffer[AudioSource]

  def act(){
    if( device == null) device = Gdx.audio.newAudioDevice(sampleRate, true)
    self ! Process
    loop{
      react{
        case Process => if( playing ){

            //device.readSamples(in,0,bufferSize)

            sources.foreach( _.audioCallback(in,out,bufferSize) )

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


