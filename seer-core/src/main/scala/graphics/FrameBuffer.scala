package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.{ FrameBuffer => GdxFrameBuffer }
import com.badlogic.gdx.graphics.Pixmap.Format

import scala.collection.mutable.ListBuffer

class FrameBuffer(w:Int,h:Int,f:Format,depth:Boolean) extends GdxFrameBuffer(f,w,h,depth)

object FrameBuffer {

	// var fbos = new ListBuffer[GdxFrameBuffer]

	def apply(w:Int, h:Int, format:Format=Format.RGBA8888, depth:Boolean=true) = {
		val b = new FrameBuffer(w, h, format, depth)
		// fbos += b
		b
	}

  // def apply(i:Int) = fbos(i)
}



