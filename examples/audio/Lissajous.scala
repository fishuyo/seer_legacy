
package com.fishuyo.seer
package examples.audio

import graphics._
import audio._
import spatial._
import util._

object Lissajous extends SeerApp with AudioSource {

	GdxAudio.init

	val mesh = Mesh()
	for(i <- 0 until Audio().bufferSize) mesh.vertices += Vec3()
	mesh.primitive = LineStrip

	val model = Model(mesh)

	val sin1 = new Sine(120, 1f)
	val sin2 = new Sine(120, 1f)

	val buf = new Array[Vec3](Audio().bufferSize)
	for(i <- 0 until Audio().bufferSize) buf(i) = Vec3()

	Audio().push(this)
	Audio().start

	override def draw(){
		model.draw
	}

	override def animate(dt:Float){
		mesh.clear
		mesh.vertices ++= buf
		mesh.update
	}

	override def audioIO(io: AudioIOBuffer){
		while(io()){
			val s1 = sin1()
			val s2 = sin2()

			buf(io.index).set(s1,s2,0)

			io.outSet(0)(s1)
			io.outSet(1)(s2)
		}
	}

	io.Trackpad.connect
	io.Trackpad.bind((touch) => {
		touch.count match{
			case 1 =>
				sin1.f = (440.f * (touch.pos.x-0.5))
				sin2.f = (440.f * (touch.pos.y-0.5))
			case _ => ()
		}
	})

}