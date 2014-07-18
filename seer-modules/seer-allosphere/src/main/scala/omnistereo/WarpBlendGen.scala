
package com.fishuyo.seer
package allosphere

import graphics._
import dynamic._
import spatial._
import spatial._
import io._
import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

import java.nio.FloatBuffer



object WarpBlendGen {
	var fovy = math.Pi
	var aspect = 2.0

	def fillFishEye(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5

			val az = fovy * aspect * sx
			val el = fovy * sy;
			val sel = math.sin(el)
			val cel = math.cos(el)
			val saz = math.sin(az)
			val caz = math.cos(az)

			val v = Vec3(cel*saz,sel,-cel*caz).normalize

			// data.put(y*w+x, Array(v.x,v.y,v.z,1.f))
			data.put(Array(v.x,v.y,v.z,1.f))

		}
		data.rewind
	}

	def fillCylinder(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5
			val y1 = sy*fovy*2.0
			val y0 = 1.0 - math.abs(y1)

			val az = fovy * math.Pi * aspect * sx
			val saz = math.sin(az)
			val caz = math.cos(az)

			val v = Vec3(y0*saz,y1,-y0*caz)
			v.normalize

			// data.put(y*w+x, Array(v.x,v.y,v.z,1.f))
			data.put(Array(v.x,v.y,v.z,1.f), y*w+x, 4)

		}
		data.rewind
	}

	def fillRect(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5
			val f = 1.0/ math.tan(fovy * 0.5)

			val v = Vec3(f*sx*aspect,f*sy,-1)
			v.normalize

			data.put(Array(v.x,v.y,v.z,1.f), y*w+x, 4)

		}
		data.rewind
	}
	def fillSoftEdge(pix:Pixmap) {
		val mult = 20.0;
		val w = pix.getWidth
		val h = pix.getHeight

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble
			// fade out at edges:
			val v = math.sin(.5*Pi * math.min(1., mult*(0.5 - math.abs(normx-0.5)))) * math.sin(.5*Pi * math.min(1., mult*(0.5 - math.abs(normy-0.5))))
			pix.setColor(v,v,v,v)
			pix.drawPixel(x,y)
		}
	}
}