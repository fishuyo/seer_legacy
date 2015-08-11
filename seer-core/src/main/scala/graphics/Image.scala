
package com.fishuyo.seer
package graphics

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ByteOrder

object Image {
  def apply(w:Int,h:Int,chans:Int=4,bpc:Int=1) = new Image(w,h,chans,bpc)
}

class Image(var w:Int,var h:Int, var channels:Int=4, var bytesPerChannel:Int=1) {

  var buffer:ByteBuffer = _

  allocate(w,h,channels,bytesPerChannel)

  def width = w 
  def height = h 

  def bytesPerPixel = channels * bytesPerChannel
  def sizeInBytes = bytesPerPixel * w * h

  def allocate(_w:Int, _h:Int, chan:Int=4, bytes:Int=1){
    w = _w; h = _h; channels = chan; bytesPerChannel = bytes;
    buffer = ByteBuffer.allocateDirect(sizeInBytes);
    buffer.order(ByteOrder.nativeOrder());
  }


}