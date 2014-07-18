
package com.fishuyo.seer
package audio


trait AudioIOBufferLike {
  val numIn:Int
  val numOut:Int
  val numSamples:Int
  val in:Array[Array[Float]]
  val out:Array[Array[Float]]
}

trait AudioSource {
  def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){}
}

object AudioPass extends AudioSource {
  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    for( i<-(0 until numOut)){ //Array.copy(in,0,out(i),0,numSamples)
      val o = out(i)
      var s=0
      while(s < numSamples){
        o(s) += in(s)
        s += 1
      }
    }
  }
}

