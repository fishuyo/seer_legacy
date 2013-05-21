package com.fishuyo
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils._
import com.badlogic.gdx.Gdx

import scala.collection.mutable.ListBuffer

object Texture{

	var textures = new ListBuffer[Texture]
	def apply(path:String) = {
		val t = new Texture(Gdx.files.internal(path));
		textures += t
		textures.length - 1
	}
	def apply(w:Int,h:Int) = {
		val t = new Texture(new FloatTextureDataExposed(w,h))
		textures += t
		textures.length - 1
	}
	def apply(p:Pixmap) = {
		val t = new Texture(p)
		textures += t
		textures.length - 1
	}

  def apply(i:Int) = textures(i)

  def getFloatBuffer(i:Int) = textures(i).getTextureData match { case td:FloatTextureDataExposed => td.getBuffer; case _ => null }
}



