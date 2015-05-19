package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils


import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

// import org.lwjgl.opengl.GL11
// import org.lwjgl.opengl.GL12
// import org.lwjgl.opengl.GL43

import java.nio.FloatBuffer
import java.io.DataInputStream
import java.io.FileInputStream

import scala.collection.mutable.ListBuffer


object Texture {

	var textures = new ListBuffer[GdxTexture]
	def apply(path:String) = {
		val t = new GdxTexture(Gdx.files.internal(path));
		textures += t
		textures.length - 1
	}
	def apply(w:Int,h:Int, b:FloatBuffer) = {
		val t = new GdxTexture(new DynamicFloatTexture(w,h,b))
		textures += t
		textures.length - 1
	}
	def apply(p:Pixmap) = {
		val t = new GdxTexture(p)
		textures += t
		textures.length - 1
	}

	def update(i:Int, p:Pixmap){
		textures(i).dispose
		textures(i) = new GdxTexture(p)
	}

  def apply(i:Int) = {
  	textures(i)
  }

  def remove(i:Int) = {
  	val t = textures(i)
  	textures.remove(i)
  	t.dispose()
  }

  // def bind(i:Int) = {
  // 	val t = textures(i)
  // 	val id = t.getTextureObjectHandle
  // 	t.bind(id)
  //   Shader().setUniformi("u_texture0", id );
  // }

  // def getFloatBuffer(i:Int) = textures(i).getTextureData match { case td:FloatTextureDataExposed => td.getBuffer; case _ => null }
}

// class FloatTexture(val w:Int,val h:Int) {
// 	val data:FloatBuffer = BufferUtils.newFloatBuffer( 4*w*h )
// 	val td = new DynamicFloatTexture(w,h,data)
// 	val t = new GdxTexture( td )

// }

class FloatTexture(var w:Int,var h:Int) {
  var data = BufferUtils.newFloatBuffer( 4*w*h )
  var handle = getGLHandle()
  val target = GL20.GL_TEXTURE_2D
  val iformat = GL30.GL_RGBA32F //GL20.GL_RGBA
  val format = GL20.GL_RGBA
  val dtype = GL20.GL_FLOAT
  // println(Gdx.graphics.supportsExtension("texture_float"))

  var filterMin = GL20.GL_NEAREST
  var filterMag = GL20.GL_NEAREST
  // val mWrapS = GL20.GL_CLAMP_TO_EDGE
  // val mWrapT = GL20.GL_CLAMP_TO_EDGE
  // val mWrapR = GL20.GL_CLAMP_TO_EDGE
  var mWrapS = GL20.GL_REPEAT
  var mWrapT = GL20.GL_REPEAT
  var mWrapR = GL20.GL_REPEAT

  // bind(0)
  // update()
  // params()

  def getGLHandle() = {
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
    data.rewind
    // Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
    Gdx.gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,data)
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






