package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTex}
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils

import com.badlogic.gdx.files.FileHandle

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

// import org.lwjgl.opengl.GL11
// import org.lwjgl.opengl.GL12
// import org.lwjgl.opengl.GL43

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.io.DataInputStream
import java.io.FileInputStream

object Texture {

  def apply(path:String) = {
    val file = Gdx.files.internal(path)
    println(file.path())
    val t = new GdxTexture(new GdxTex(file))
    t
  }
  def apply(file:FileHandle) = {
		val t = new GdxTexture(new GdxTex(file))
    t
	}

	def apply(p:Pixmap) = {
		val t = new GdxTexture(new GdxTex(p))
    t
	}

  def apply(w:Int,h:Int) = {
    val t = new Texture(w,h)
    t.allocate(w,h)
    t.init()
    t
  }

  def apply(image:Image) = {
    val t = new ImageTexture(image)
    t.init
    t
  }

}


class Texture(var w:Int,var h:Int) {
  var (width, height) = (w,h)

  var buffer:Buffer = _

  def byteBuffer = buffer.asInstanceOf[ByteBuffer]

  var handle = 0 //getGLHandle()
  val target = GL20.GL_TEXTURE_2D
  var iformat = GL20.GL_RGBA
  var format = GL20.GL_RGBA
  var dtype = GL20.GL_UNSIGNED_BYTE

  var filterMin = GL20.GL_NEAREST
  var filterMag = GL20.GL_NEAREST
  // var mWrapS = GL20.GL_CLAMP_TO_EDGE
  // var mWrapT = GL20.GL_CLAMP_TO_EDGE
  // var mWrapR = GL20.GL_CLAMP_TO_EDGE
  var mWrapS = GL20.GL_REPEAT
  var mWrapT = GL20.GL_REPEAT
  var mWrapR = GL20.GL_REPEAT

  // allocate(w,h)
  // params()
  // update()

  def init(){
    handle = getGLHandle()
    params()
    update()
  }

  def allocate(ww:Int, hh:Int){
    width = ww; height = hh;
    buffer = BufferUtils.newByteBuffer(4*w*h)
  }

  private def getGLHandle() = {
    val buf = BufferUtils.newIntBuffer(1)
    Gdx.gl.glGenTextures(1,buf)
    buf.get(0)
  }

  def bind(i:Int=0){
    Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0+i)
    Gdx.gl.glEnable(target)
    Gdx.gl.glBindTexture(target, handle)    
  }

  def params(){
    bind()
    // Gdx.gl.glBindTexture(target, handle);
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_MAG_FILTER, filterMag)
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_MIN_FILTER, filterMin)
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_WRAP_S, mWrapS);
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_WRAP_T, mWrapT);
    Gdx.gl.glTexParameterf(target, GL30.GL_TEXTURE_WRAP_R, mWrapR);
    if (filterMin != GL20.GL_LINEAR && filterMin != GL20.GL_NEAREST) {
      Gdx.gl.glTexParameteri(target, GL20.GL_GENERATE_MIPMAP, GL20.GL_TRUE); // automatic mipmap
    }
    // Gdx.gl.glBindTexture(target, 0);
  }

  def update(){
    buffer.rewind
    bind()
    // Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
    Gdx.gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,buffer)
    Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);
    // println(Gdx.gl.glGetError())
  }

  def dispose(){
    if (handle != 0) {
      val buffer = BufferUtils.newIntBuffer(1)
      buffer.put(0, handle);
      buffer.position(0);
      buffer.limit(1);
      Gdx.gl.glDeleteTextures(1, buffer);
      handle = 0;
    }
  }
}

class FloatTexture(w:Int,h:Int) extends Texture(w,h) {
  // buffer = BufferUtils.newFloatBuffer( 4*w*h )
  iformat = GL30.GL_RGBA32F //GL20.GL_RGBA
  dtype = GL20.GL_FLOAT

  allocate(w,h)
  init()
  
  def floatBuffer = buffer.asInstanceOf[FloatBuffer]

  override def allocate(ww:Int,hh:Int){
    width = ww; height = hh;
    buffer = BufferUtils.newFloatBuffer(4*w*h)
  }
}


class ImageTexture(val image:Image) extends Texture(image.w, image.h) {

  image.channels match {
    case 1 => format = GL20.GL_LUMINANCE
    case 2 => format = GL20.GL_LUMINANCE_ALPHA
    case 3 => format = GL20.GL_RGB
    case 4 => format = GL20.GL_RGBA
    case _ => ()
  }
  image.bytesPerChannel match {
    case 1 => dtype = GL20.GL_UNSIGNED_BYTE
    case 2 => dtype = GL20.GL_SHORT //GL20.GL_UNSIGNED_SHORT
    case 4 => dtype = GL20.GL_UNSIGNED_INT
    case _ => ()
  }

  override def update(){
    bind()
    // Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
    image.buffer.rewind
    Gdx.gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,image.buffer)
    Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);
    // println(Gdx.gl.glGetError())
  }
}


class GdxTexture(val gdxTexture:GdxTex) extends Texture(0,0){

  override def bind(i:Int=0) = gdxTexture.bind(i)
  override def params() = {}
  override def update() = { }
  // override def getGLHandle() = -1 //{}
}




