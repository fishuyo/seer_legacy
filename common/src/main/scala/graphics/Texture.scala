package com.fishuyo
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.Gdx

import scala.collection.mutable.ListBuffer

object Texture{

	var textures = new ListBuffer[Texture]
	def apply(path:String) = {
		val t = new Texture(Gdx.files.internal(path));
		textures += t
		t
	}

  def apply(i:Int) = textures(i)
}


