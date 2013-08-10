package com.fishuyo
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.Pixmap.Format

import scala.collection.mutable.ListBuffer

object FrameBuffers{

	var fbo = new ListBuffer[FrameBuffer]
	def apply(w:Int, h:Int, format:Format=null, depth:Boolean=true) = {
		var f = format
		if( f == null) f = Format.RGBA8888
		val b = new FrameBuffer(format, w, h, depth)
		fbo += b
		b
	}

  def apply(i:Int) = fbo(i)
}


