
package com.fishuyo.seer
package examples.audio

import graphics._
import audio._
import spatial._
import util._
import io._

object Lissajous extends SeerApp with AudioSource {

	// PortAudio.bufferSize = 1024
	PortAudio.init
	Audio().push(this)
	Audio().start

	val mesh = Mesh()
	mesh.primitive = LineStrip
	mesh.maxVertices = Audio().bufferSize

	val model = Model(mesh)

	val sin1 = new Sine(120, 1f)
	val sin2 = new Sine(120, 1f)

	val buf = new Array[Vec3](Audio().bufferSize)
	for(i <- 0 until Audio().bufferSize) buf(i) = Vec3()


	override def draw(){
		mesh.clear
		mesh.vertices ++= buf
		mesh.update

		model.draw
	}

	override def animate(dt:Float){}

	override def audioIO(io: AudioIOBuffer){
		while(io()){
			sin1.f = (Mouse.x.now - 0.5f) * 440f
			sin2.f = (Mouse.y.now - 0.5f) * 440f

			val s1 = sin1()
			val s2 = sin2()

			buf(io.index).set(s1,s2,0)

			io.outSet(0)(s1)
			io.outSet(1)(s2)
		}
	}

	// io.Trackpad.connect
	// io.Trackpad.bind((touch) => {
	// 	touch.count match{
	// 		case 1 =>
	// 			sin1.f = (440f * (touch.pos.x-0.5))
	// 			sin2.f = (440f * (touch.pos.y-0.5))
	// 		case _ => ()
	// 	}
	// })
	

}