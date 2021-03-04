package seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTex}
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils

import com.badlogic.gdx.files.FileHandle

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.io.DataInputStream
import java.io.FileInputStream

object Texture {

  def load(path:String):Option[Texture] = {
    try{
      val file = Gdx.files.internal(path)
      val t = new GdxTexture(new GdxTex(file))
      return Some(t)
    } catch { case e => println(s"error loading $path")}
    None
  }

  def apply(file:FileHandle) = {
		val t = new GdxTexture(new GdxTex(file))
    t
	}

	def apply(p:Pixmap) = {
		val t = new GdxTexture(new GdxTex(p))
    t
	}

  def apply(w:Int,h:Int,format:Int=Graphics().gl.GL_RGBA) = {
    val t = new Texture(w,h)
    t.format = format
    t.allocate(w,h)
    t.init()
    t
  }

  def apply(image:Image) = {
    val t = new ImageTexture(image)
    t.init
    t
  }

  def apply(node:RenderNode) = {
    val t = new RenderNodeTexture(node)
    t
  }

}


class Texture(var w:Int, var h:Int) {
  // var (width, height) = (w,h)

  var buffer:Buffer = _

  def byteBuffer = buffer.asInstanceOf[ByteBuffer]

  var handle = 0 //getGLHandle()
  val target = Graphics().gl.GL_TEXTURE_2D
  var iformat = Graphics().gl.GL_RGBA
  var format = Graphics().gl.GL_RGBA
  var dtype = Graphics().gl.GL_UNSIGNED_BYTE

  var filterMin = Graphics().gl.GL_NEAREST
  var filterMag = Graphics().gl.GL_NEAREST
  // var mWrapS = Graphics().gl.GL_CLAMP_TO_EDGE
  // var mWrapT = Graphics().gl.GL_CLAMP_TO_EDGE
  // var mWrapR = Graphics().gl.GL_CLAMP_TO_EDGE
  var mWrapS = Graphics().gl.GL_REPEAT
  var mWrapT = Graphics().gl.GL_REPEAT
  var mWrapR = Graphics().gl.GL_REPEAT

  // allocate(w,h)
  // params()
  // update()

  def init(){
    handle = getGLHandle()
    params()
    update()
  }

  def allocate(ww:Int, hh:Int){
    w = ww; h = hh;
    buffer = BufferUtils.newByteBuffer(4*w*h)
  }

  private def getGLHandle() = {
    val buf = BufferUtils.newIntBuffer(1)
    Graphics().gl.glGenTextures(1,buf)
    buf.get(0)
  }

  def bind(i:Int=0){
    Graphics().gl.glActiveTexture(Graphics().gl.GL_TEXTURE0+i)
    Graphics().gl.glEnable(target)
    Graphics().gl.glBindTexture(target, handle)    
  }

  def params(){
    bind()
    // Graphics().gl.glBindTexture(target, handle);
    Graphics().gl.glTexParameterf(target, Graphics().gl.GL_TEXTURE_MAG_FILTER, filterMag)
    Graphics().gl.glTexParameterf(target, Graphics().gl.GL_TEXTURE_MIN_FILTER, filterMin)
    Graphics().gl.glTexParameterf(target, Graphics().gl.GL_TEXTURE_WRAP_S, mWrapS);
    Graphics().gl.glTexParameterf(target, Graphics().gl.GL_TEXTURE_WRAP_T, mWrapT);
    Graphics().gl.glTexParameterf(target, Graphics().gl.GL_TEXTURE_WRAP_R, mWrapR);
    if (filterMin != Graphics().gl.GL_LINEAR && filterMin != Graphics().gl.GL_NEAREST) {
      Graphics().gl.glTexParameteri(target, Graphics().gl.GL_GENERATE_MIPMAP, Graphics().gl.GL_TRUE); // automatic mipmap
    }
    // Graphics().gl.glBindTexture(target, 0);
  }

  def update(){
    buffer.rewind
    bind()
    // Graphics().gl.glPixelStorei(Graphics().gl.GL_UNPACK_ALIGNMENT, 1);
    Graphics().gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,buffer)
    Graphics().gl.glGenerateMipmap(Graphics().gl.GL_TEXTURE_2D);
    // println(Graphics().gl.glGetError())
  }

  def bgr() = format = 0x80E0 //Graphics().gl.GL_BGR

  def dispose(){
    if (handle != 0) {
      val buffer = BufferUtils.newIntBuffer(1)
      buffer.put(0, handle);
      buffer.position(0);
      buffer.limit(1);
      Graphics().gl.glDeleteTextures(1, buffer);
      handle = 0;
    }
  }
}

class FloatTexture(w:Int,h:Int) extends Texture(w,h) {
  // buffer = BufferUtils.newFloatBuffer( 4*w*h )
  iformat = Graphics().gl.GL_RGBA32F //Graphics().gl.GL_RGBA
  dtype = Graphics().gl.GL_FLOAT

  allocate(w,h)
  init()
  
  def floatBuffer = buffer.asInstanceOf[FloatBuffer]

  override def allocate(ww:Int,hh:Int){
    w = ww; h = hh;
    buffer = BufferUtils.newFloatBuffer(4*w*h)
  }
}


class ImageTexture(val image:Image) extends Texture(image.w, image.h) {

  image.channels match {
    case 1 => format = Graphics().gl.GL_LUMINANCE
    case 2 => format = Graphics().gl.GL_LUMINANCE_ALPHA
    case 3 => format = Graphics().gl.GL_RGB
    case 4 => format = Graphics().gl.GL_RGBA
    case _ => ()
  }
  image.bytesPerChannel match {
    case 1 => dtype = Graphics().gl.GL_UNSIGNED_BYTE
    case 2 => dtype = Graphics().gl.GL_SHORT //Graphics().gl.GL_UNSIGNED_SHORT
    case 4 => dtype = Graphics().gl.GL_FLOAT //Graphics().gl.GL_UNSIGNED_INT
    case _ => ()
  }

  override def byteBuffer = image.buffer

  override def update(){
    bind()
    // Graphics().gl.glPixelStorei(Graphics().gl.GL_UNPACK_ALIGNMENT, 1);
    image.buffer.rewind
    Graphics().gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,image.buffer)
    Graphics().gl.glGenerateMipmap(Graphics().gl.GL_TEXTURE_2D);
    // println(Graphics().gl.glGetError())
  }
}


class GdxTexture(val gdxTexture:GdxTex) extends Texture(0,0){
  w = gdxTexture.getWidth()
  h = gdxTexture.getHeight() 

  override def bind(i:Int=0) = gdxTexture.bind(i)
  override def params() = {}
  override def update() = { }
  // override def getGLHandle() = -1 //{}
}

class RenderNodeTexture(val node:RenderNode) extends Texture(0,0){

  override def bind(i:Int=0) = node.buffer.get.getColorBufferTexture().bind(i)
  override def params() = {}
  override def update() = {}
  // override def getGLHandle() = -1 //{}
}




