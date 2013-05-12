package com.fishuyo
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.{FrameBuffer => FB}

import scala.collection.mutable.ListBuffer

object FrameBuffer{

	var fbo = new ListBuffer[FB]
	def apply(w:Int, h:Int, format:Pixmap.Format=Pixmap.Format.RGBA8888, depth:Boolean=true) = {
		val b = new FB(format, w, h, depth)
		fbo += b
		b
	}

  def apply(i:Int) = fbo(i)
}


