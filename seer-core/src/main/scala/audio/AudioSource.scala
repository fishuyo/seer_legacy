
package com.fishuyo.seer
package audio


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

  def in(c:Int) = inputSamples(c)(index)
  def out(c:Int) = outputSamples(c)(index)
  def outSet(c:Int)(v:Float) = outputSamples(c)(index) = v
  def outSum(c:Int)(v:Float) = outputSamples(c)(index) += v

}

trait AudioSource {
  def audioIO( io:AudioIOBuffer ){}
  // def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){}
}

object AudioPass extends AudioSource {
  override def audioIO( io:AudioIOBuffer ){
    while( io() ){
      val s = io.in(0)
      io.outSum(0)(s)
      io.outSum(1)(s)
    }
  }
  
  // override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
  //   for( i<-(0 until numOut)){ //Array.copy(in,0,out(i),0,numSamples)
  //     val o = out(i)
  //     var s=0
  //     while(s < numSamples){
  //       o(s) += in(s)
  //       s += 1
  //     }
  //   }
  // }
}

