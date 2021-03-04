
package seer
package graphics

import java.nio.ByteBuffer
import java.nio.ByteOrder

import java.awt.image.BufferedImage

object Image {
  def apply(w:Int,h:Int,chans:Int=4,bpc:Int=1) = {
    val buffer = ByteBuffer.allocateDirect(chans*bpc*w*h)
    buffer.order(ByteOrder.nativeOrder())
    new Image(buffer,w,h,chans,bpc)
  }

  def load(path:String): Option[Image] = {
    println(s"loading file $path")
    try {
      val image = javax.imageio.ImageIO.read(new java.io.File(path))
      val img = Image(image.getWidth(), image.getHeight(), 3, 1)
      for(x <- 0 until image.getWidth(); y <- 0 until image.getHeight()){
          val c = image.getRGB(x,y)
          img(x,y,0) = ((c >> 16) & 0xFF).toByte
          img(x,y,1) = ((c >> 8) & 0xFF).toByte
          img(x,y,2) = (c & 0xFF).toByte
      }
      return Some(img)
    } catch { case e:Exception => println("error loading file.") }
    return None
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

  def apply(x:Int, y:Int, offset:Int=0) = buffer.get(bytesPerPixel*(y*w+x) + offset)
  def update(x:Int, y:Int, value:Byte) = buffer.put(bytesPerPixel*(y*w+x), value)
  def update(x:Int, y:Int, c:Int, value:Byte) = buffer.put(bytesPerPixel*(y*w+x) + bytesPerChannel*c, value)

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

  def save(path:String, alpha:Boolean=true) = {
    val image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    val file = new java.io.File(path)
    val format = "PNG"

    for(x <- 0 until w){
        for(y <- 0 until h){
            val i = (x + (w * y)) * bytesPerPixel;
            val r = buffer.get(i) & 0xFF;
            val g = buffer.get(i + 1) & 0xFF;
            val b = buffer.get(i + 2) & 0xFF;
            var a = buffer.get(i + 3) & 0xFF;
            if(!alpha) a = 0xFF
            image.setRGB(x, h - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
        }
    }
      
    println(s"writing file ${file.getAbsolutePath}")
    try {
        javax.imageio.ImageIO.write(image, format, file);
    } catch { case e:Exception => e.printStackTrace() }
  }


}