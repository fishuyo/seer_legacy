
package seer
package graphics

import java.nio.ByteBuffer
import java.nio.ByteOrder

object Image {
  def apply(w:Int,h:Int,chans:Int=4,bpc:Int=1) = {
    val buffer = ByteBuffer.allocateDirect(chans*bpc*w*h)
    buffer.order(ByteOrder.nativeOrder())
    new Image(buffer,w,h,chans,bpc)
  }
}

class Image(val buffer:ByteBuffer, val w:Int, val h:Int, val channels:Int, val bytesPerChannel:Int) {

  def width = w 
  def height = h 
  def aspect = h.toFloat / w.toFloat

  def bytesPerPixel = channels * bytesPerChannel
  def sizeInBytes = bytesPerPixel * w * h

  def floatBuffer = buffer.asFloatBuffer

  def sameDimAs(i:Image) = (w == i.w && h == i.h && bytesPerChannel == i.bytesPerChannel)
  
  def set(i:Image) = {
    if(sameDimAs(i)){
      buffer.rewind
      i.buffer.rewind
      buffer.put(i.buffer)
    } else {
      // TODO error handling..
    }
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