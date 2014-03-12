package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils


import scala.collection.mutable.ListBuffer

import java.nio.FloatBuffer


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

  def bind(i:Int) = {
  	val t = textures(i)
  	val id = t.getTextureObjectHandle
  	t.bind(id)
    Shader().setUniformi("u_texture0", id );
  }

  // def getFloatBuffer(i:Int) = textures(i).getTextureData match { case td:FloatTextureDataExposed => td.getBuffer; case _ => null }
}

class FloatTexture(val w:Int,val h:Int) {
	val data:FloatBuffer = BufferUtils.newFloatBuffer( 4*w*h )
	val td = new DynamicFloatTexture(w,h,data)
	val t = new GdxTexture( td )

}






