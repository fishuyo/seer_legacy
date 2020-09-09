package seer
package graphics

import com.badlogic.gdx.graphics._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.{ FrameBuffer => GdxFrameBuffer }
import com.badlogic.gdx.graphics.Pixmap.Format

import scala.collection.mutable.ListBuffer

import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.math.Matrix4

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
// import com.badlogic.gdx.graphics.{Texture => GdxTex}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }



trait RenderTarget {
}

// class FrameBuffer(builder:FrameBufferBuilder) extends GdxFrameBuffer(f,w,h,depth) with RenderTarget
class FrameBuffer(var w:Int, var h:Int,f:Format,depth:Boolean) extends GdxFrameBuffer(f,w,h,depth) with RenderTarget

object FrameBuffer {

	// var fbos = new ListBuffer[GdxFrameBuffer]

	def apply(w:Int, h:Int, format:Format=Format.RGBA8888, depth:Boolean=true) = {
		val b = new FrameBuffer(w, h, format, depth)
		// fbos += b
		b
	}

  // def apply(i:Int) = fbos(i)
}



class FloatFrameBuffer(var w:Int,var h:Int,depth:Boolean=true) extends RenderTarget {

	var colorTexture: FloatTexture = _
	setupTexture();

	val handle = BufferUtils.newIntBuffer(1);
	gl.glGenFramebuffers(1, handle);
	val framebufferHandle = handle.get(0);
	var depthbufferHandle = 0

	if (depth) {
		handle.clear();
		gl.glGenRenderbuffers(1, handle);
		depthbufferHandle = handle.get(0);
	}

	gl.glBindTexture(GL20.GL_TEXTURE_2D, colorTexture.handle);

	if (depth) {
		gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthbufferHandle);
		gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, colorTexture.w,
			colorTexture.h);
	}

	gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle);
	gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
		colorTexture.handle, 0);
	
	if (depth) {
		gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER, depthbufferHandle);
	}
	val result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);

	gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
	gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
	gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

	if (result != GL20.GL_FRAMEBUFFER_COMPLETE) {
		colorTexture.dispose();
		if (depth) {
			handle.clear();
			handle.put(depthbufferHandle);
			handle.flip();
			gl.glDeleteRenderbuffers(1, handle);
		}

		handle.clear();
		handle.put(framebufferHandle);
		handle.flip();
		gl.glDeleteFramebuffers(1, handle);

		if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
			throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment");
		if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS)
			throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions");
		if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT)
			throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment");
		if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED)
			throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats");
		throw new IllegalStateException("frame buffer couldn't be constructed: unknown error " + result);
	}

	def setupTexture(){
		colorTexture = new FloatTexture(w,h)
		colorTexture.bind(0)
		colorTexture.update
		colorTexture.params
	}

	def getColorBufferTexture() = colorTexture

	def bind(){
		Gdx.graphics.getGL20().glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle);
	}
	def unbind(){
		Gdx.graphics.getGL20().glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);
	}
	def begin(){
		bind()
		setFrameBufferViewport()
	}

	def setFrameBufferViewport(){
		Gdx.graphics.getGL20().glViewport(0, 0, colorTexture.w, colorTexture.h);
	}

	def end(){
		unbind();
		setDefaultFrameBufferViewport();
	}

	def setDefaultFrameBufferViewport(){
		// Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
	}

	def dispose(){
		val handle = BufferUtils.newIntBuffer(1);

		colorTexture.dispose();
		if (depth) {
			handle.put(depthbufferHandle);
			handle.flip();
			gl.glDeleteRenderbuffers(1, handle);
		}

		handle.clear();
		handle.put(framebufferHandle);
		handle.flip();
		gl.glDeleteFramebuffers(1, handle);	}
}