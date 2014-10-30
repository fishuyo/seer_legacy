
package com.fishuyo.seer
package allosphere

import graphics._
import dynamic._
import spatial._
import spatial._
import io._
import util._

import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.math.Matrix4

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL43

import java.nio._
import java.io.DataInputStream
import java.io.FileInputStream

import javax.imageio.ImageIO


/// Encapsulate the trio of fractional viewport, warp & blend maps:
class Projection {
	class Parameters {
		var projnum = 0f			// ID of the projector
		var (width, height) = (0f,0f)	// width/height in pixels
		var (projector_position, screen_center, normal_unit, x_vec, y_vec) = (Vec3(),Vec3(),Vec3(),Vec3(),Vec3())
		var (screen_radius, bridge_radius, unused0) = (0f,0f,0f)

		def fromList(l:List[Float]){
			projnum = l(0)
			width = l(1)
			height = l(2)
			projector_position = Vec3(l(3),l(4),l(5))
			screen_center = Vec3(l(6),l(7),l(8))
			normal_unit = Vec3(l(9),l(10),l(11))
			x_vec = Vec3(l(12),l(13),l(14))
			y_vec = Vec3(l(15),l(16),l(17))
			screen_radius = l(18)
			bridge_radius = l(19)
			unused0 = l(20)
		}
	};

	var mViewport = Viewport(0, 0, 1, 1)
	def viewport() = mViewport

	// allocate blend map:
	var pBlend:Pixmap = null
	var mBlend:GdxTexture = null
	var pWarp:Pixmap = null
	var mWarp:FloatTexture = null

	def blend() = mBlend
	def warp() = mWarp

	// allocate warp map:
	// mWarp.resize(256, 256)
	// 	.target(Texture.TEXTURE_2D)
	// 	.format(Graphics.RGBA)
	// 	.type(Graphics.FLOAT)
	// 	.filterMin(Texture.LINEAR)
	// 	.allocate();

	var t:Array[Float] = _
	var u:Array[Float] = _
	var v:Array[Float] = _

	val params = new Parameters()

	// derived:
	var (x_unit, y_unit) = (Vec3(),Vec3())
	var (x_pixel, y_pixel, x_offset, y_offset) = (0f,0f,0f,0f)
	var position = Vec3() //Vec3d

	// TODO: remove this
	var (warpwidth, warpheight) = (0,0)

	// the position/orientation of the raw map data relative to the real world
	var mRegistration = Pose()


	def onCreate(){
		//allocate warp and blend textures
		// pWarp = new Pixmap(256,256,Pixmap.Format.RGBA8888)
		mWarp = new FloatTexture(256,256) //GdxTexture(pWarp, true)
		// pBlend = new Pixmap(256,256,Pixmap.Format.Intensity)
		pBlend = new Pixmap(256,256,Pixmap.Format.RGBA8888)
		mBlend = new GdxTexture(pBlend, true)


		// mWarp.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.Linear)
		// mWarp.setFilter( GdxTexture.TextureFilter.Linear, GdxTexture.TextureFilter.Linear)
		// mWarp.texelFormat(GL_RGB32F_ARB);
		// mWarp.dirty();

		// mBlend.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.Linear)
		mBlend.setFilter( GdxTexture.TextureFilter.Linear, GdxTexture.TextureFilter.Linear)
		// mBlend.dirty();
	}

	// load warp/blend from disk:
	def readBlend(path:String){

		val img = ImageIO.read(new java.io.File(path))
		val w = img.getWidth
		val h = img.getHeight

		println(s"blend dim: $w x $h")

		val pix:Array[Int] = img.getData.getPixels(0,0,w,h,null)

		pBlend = new Pixmap(w,h, Pixmap.Format.RGB888)
		val bb = pBlend.getPixels()
		var i = 0
		var len = w*h
		while( i < len){
			val c = pix(i).toByte
			bb.put(Array(c,c,c))
			i += 1
		}
		// bb.put(pix.map( f => {val i=f.toByte; Seq(i,i,i)}).flatten)
		bb.rewind()
		// mBlend.draw(pBlend,0,0)

		mBlend = new GdxTexture(pBlend, true)

		// mBlend = new GdxTexture(Gdx.files.internal(path), Pixmap.Format.RGBA8888, true)
		mBlend.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.Linear)
		// mBlend.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.MipMap)
		// mBlend.setFilter( GdxTexture.TextureFilter.Linear, GdxTexture.TextureFilter.Linear)
	}

	def readWarp(path:String){

		try{
			// val ds = new DataInputStream( new FileInputStream(path))
			val fis = new FileInputStream(path)
			val channel = fis.getChannel
			var bb = ByteBuffer.allocateDirect(8)
			bb.clear
			bb.order(ByteOrder.LITTLE_ENDIAN)

			channel.read(bb)
			bb.flip

			val h = bb.asIntBuffer.get(0)/3
			val w = bb.asIntBuffer.get(1)
			// val h = readInt(ds)/3
			// val w = readInt(ds)

			println(s"warp dim $w x $h\n")

			t = new Array[Float](w*h)
			u = new Array[Float](w*h)
			v = new Array[Float](w*h)

			bb = ByteBuffer.allocateDirect(w*h*4)
			bb.clear
			bb.order(ByteOrder.LITTLE_ENDIAN)

			channel.read(bb)
			bb.flip
			bb.asFloatBuffer.get(t)
			channel.read(bb)
			bb.flip
			bb.asFloatBuffer.get(u)
			channel.read(bb)
			bb.flip
			bb.asFloatBuffer.get(v)

			// val bytes = new Array[Byte](w*h*4)
			// ds.read(bytes)
			// t = parseFloats(bytes).toArray
			// ds.read(bytes)
			// u = parseFloats(bytes).toArray
			// ds.read(bytes)
			// v = parseFloats(bytes).toArray

			// for( i <- (0 until w*h)) t(i) = readFloat(ds)
			// for( i <- (0 until w*h)) u(i) = readFloat(ds)
			// for( i <- (0 until w*h)) v(i) = readFloat(ds)
			
			println(s"read $path\n")

			// pWarp = new Pixmap(w,h,Pixmap.Format.RGBA8888)
			mWarp = new FloatTexture(w,h) //new GdxTexture(pWarp, true)
			mWarp.filterMin = GL20.GL_LINEAR_MIPMAP_LINEAR
			mWarp.filterMag = GL20.GL_LINEAR
			mWarp.mWrapS = GL20.GL_CLAMP_TO_EDGE
			mWarp.mWrapT = GL20.GL_CLAMP_TO_EDGE
			mWarp.mWrapR = GL20.GL_CLAMP_TO_EDGE

			updatedWarp()

			// ds.close()

		} catch {
			case e: Exception => println("failed to open Projector configuration file " + path + "\n")
		}

	}
	def parseFloats(a:Array[Byte]) = {
		a.grouped(4).map(bytesToFloat(_))
	}

	def bytesToFloat(b:Seq[Byte]) = {
		val i = ((b(3)&0xff)<< 24)+((b(2)&0xff)<< 16)+((b(1)&0xff)<< 8)+(b(0)&0xff)
		java.lang.Float.intBitsToFloat(i)
	}

	def readInt(ds:DataInputStream) = {
		var b = Array[Byte](0,0,0,0)
		for( i <- (0 until 4)) b(i) = ds.readByte
		val i = ((b(3)&0xff)<< 24)+((b(2)&0xff)<< 16)+((b(1)&0xff)<< 8)+(b(0)&0xff)
		i
	}
	def readFloat(ds:DataInputStream) = {
		java.lang.Float.intBitsToFloat(readInt(ds))
	}

	def readParameters(path:String, verbose:Boolean=true){

		import java.io._
		import java.lang.Float.intBitsToFloat

		try{
			val ds = new DataInputStream( new FileInputStream(path))
			var list = List[Float]()

			while(ds.available >= 4) list = readFloat(ds) :: list
			params.fromList(list)
			ds.close()

			initParameters(verbose)
			println("read " + path + "\n" )

		} catch {
			case e: Exception => println("failed to open Projector configuration file " + path + "\n")
		}
	}

	def initParameters( verbose:Boolean=true){
		val v = params.screen_center - params.projector_position
		val screen_perpendicular_dist = params.normal_unit.dot(v)
		val compensated_center = (v) / screen_perpendicular_dist + params.projector_position

		// calculate uv parameters
		val x_dist = params.x_vec.mag();
		x_unit = params.x_vec / x_dist;
		x_pixel = x_dist / params.width;
		x_offset = x_unit.dot(compensated_center - params.projector_position);

		val y_dist = params.y_vec.mag();
		y_unit = params.y_vec / y_dist;
		y_pixel = y_dist / params.height;
		y_offset = y_unit.dot(compensated_center - params.projector_position);
	}

	// adjust the registration position:
	def registrationPosition(pos: Vec3 /*double*/){}

	def updatedWarp(){
		val w = mWarp.w
		val h = mWarp.h
		for( y <- (0 until h); x <- (0 until w)){
			val y1 = h-y-1
			val idx = y1*w+x

	    // mWarp.data.position(4*idx)
	    mWarp.data.put( Array(t(idx),u(idx),v(idx),1f)) //,0, 4)

		}

		mWarp.bind(0)
		mWarp.params
	  mWarp.update
	}

}