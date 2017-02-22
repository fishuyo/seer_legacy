
package com.fishuyo.seer
package graphics

// import java.nio.Buffer
import java.nio.ByteBuffer
// import java.nio.FloatBuffer
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

  def floatBuffer = buffer.asFloatBuffer

  def allocate(_w:Int, _h:Int, chan:Int=4, bytes:Int=1){
    w = _w; h = _h; channels = chan; bytesPerChannel = bytes;
    buffer = ByteBuffer.allocateDirect(sizeInBytes);
    buffer.order(ByteOrder.nativeOrder());
  }
  def resize(_w:Int, _h:Int) = allocate(_w, _h, channels, bytesPerChannel)

  def sameDimAs(i:Image) = (w == i.w && h == i.h && bytesPerChannel == i.bytesPerChannel)
  
  def set(i:Image) = {
    if(sameDimAs(i)){
      buffer.rewind
      i.buffer.rewind
      buffer.put(i.buffer)
    } else {
      allocate(i.w, i.h, i.channels, i.bytesPerChannel)
      i.buffer.rewind
      buffer.put(i.buffer)
    }
    // w = i.w; h = i.h; channels = i.channels; bytesPerChannel = i.bytesPerChannel;
    // buffer = i.buffer.duplicate
  }

  def apply(x:Int, y:Int) = buffer.get(bytesPerPixel*(y*w+x))
  def update(x:Int, y:Int, value:Byte) = buffer.put(bytesPerPixel*(y*w+x), value)

  def getRGBA(x:Int, y:Int) = {
    buffer.position(bytesPerPixel*(y*w+x))
    RGBA(buffer.getFloat, buffer.getFloat, buffer.getFloat, buffer.getFloat)
  }
  def getRGB(x:Int, y:Int) = {
    buffer.position(bytesPerPixel*(y*w+x))
    RGB(buffer.getFloat, buffer.getFloat, buffer.getFloat)
  }
  def getFloat(x:Int, y:Int) = {
    buffer.position(bytesPerPixel*(y*w+x))
    buffer.getFloat
  }

  def setRGBA(x:Int, y:Int, v:RGBA) = {
    buffer.position(bytesPerPixel*(y*w+x))
    buffer.putFloat(v.r); buffer.putFloat(v.g); buffer.putFloat(v.b); buffer.putFloat(v.a)
  }
  def setRGB(x:Int, y:Int, v:RGB) = {
    buffer.position(bytesPerPixel*(y*w+x))
    buffer.putFloat(v.r); buffer.putFloat(v.g); buffer.putFloat(v.b)
  }
  def setFloat(x:Int, y:Int, v:Float) = {
    buffer.position(bytesPerPixel*(y*w+x))
    buffer.putFloat(v)
  }


}