
package com.fishuyo.seer
package audio

import types.RingBuffer

import collection.mutable.ListBuffer

class AudioIOBuffer(
  val channelsIn:Int,
  val channelsOut:Int,
  val bufferSize:Int,
  val inputSamples:Array[Array[Float]],
  val outputSamples:Array[Array[Float]]
){

  var index = -1

  def apply() = {
    index += 1
    index < bufferSize
  }
  def reset() = index = -1
  // def zero()

  def in(c:Int=0) = inputSamples(c)(index)
  def out(c:Int) = outputSamples(c)(index)
  def outSet(c:Int)(v:Float) = outputSamples(c)(index) = v
  def outSum(c:Int)(v:Float) = outputSamples(c)(index) += v

  def *=(gain:Gen) = for(s <- 0 until bufferSize){
    val g = gain()
    outputSamples(0)(s) *= g
    outputSamples(1)(s) *= g
  }

  def +=(buffer:AudioIOBuffer) = for(s <- 0 until bufferSize){
    outputSamples(0)(s) += buffer.outputSamples(0)(s)
    outputSamples(1)(s) += buffer.outputSamples(1)(s)
  }

  override def clone() = new AudioIOBuffer(channelsIn,channelsOut,bufferSize, inputSamples.map(_.clone), outputSamples.map(_.clone))
  

}

trait AudioSource {
  def audioIO( io:AudioIOBuffer ){}
  // def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){}
}

object AudioScene {
  def apply() = new AudioScene
}
class AudioScene extends AudioSource {
  val sources = ListBuffer[AudioSource]()
  var gain:Gen = 1f
  var buffer = Audio().ioBuffer.clone

  def +=(s:AudioSource) = sources += s
  def -=(s:AudioSource) = sources -= s

  override def audioIO( io:AudioIOBuffer){
    buffer.reset
    sources.foreach { case s => s.audioIO(buffer); buffer.reset }
    buffer *= gain
    io += buffer
  }
}

object AudioPass extends AudioSource {
  override def audioIO( io:AudioIOBuffer ){
    while( io() ){
      val s = io.in(0)
      io.outSum(0)(s)
      io.outSum(1)(s)
    }
  }
}

object AudioPassInputLatencyCorrection extends AudioSource {
  val ms = (Audio().sampleRate * .200f).toInt
  val b = Array(new RingBuffer[Float](ms), new RingBuffer[Float](ms))

  var latency = 0.06f
  var offset = 0

  def resetLatency(f:Float){
    latency = f
    offset = (Audio().sampleRate * latency).toInt
  }

  override def audioIO( io:AudioIOBuffer ){
    while( io() ){
      b(0) += io.out(0)
      b(1) += io.out(1)

      if( offset == 0){
        val s = io.in(0) * .5f
        io.outSet(0)(s + b(0).next)
        io.outSet(1)(s + b(1).next)
      
      } else {
        offset -= 1
        io.outSet(0)(0f)
        io.outSet(1)(0f)
      }
    }
  }
}

