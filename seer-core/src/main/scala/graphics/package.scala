
package com.fishuyo.seer
package object graphics {


	import com.badlogic.gdx.graphics.GL10

	val Points = GL10.GL_POINTS
	val Lines = GL10.GL_LINES
	val LineLoop = GL10.GL_LINE_LOOP
  val LineStrip = GL10.GL_LINE_STRIP
  val Triangles = GL10.GL_TRIANGLES
  val TriangleStrip = GL10.GL_TRIANGLE_STRIP
  val TriangleFan = GL10.GL_TRIANGLE_FAN

	implicit def RGBA2Float(c:RGBA) = c.toGray
}