

package com.fishuyo.seer
package examples.graphics.texture

import graphics._
import spatial._
import dynamic._

object Main extends SeerApp { 

	var model = Sphere()

	override def init(){
		// load texture
		model.material = new DiffuseMaterial
		model.material.loadTexture("../res/img/moon/moon.png")
	}

	override def draw(){
		model.rotate(0,0.01f,0)
		model.draw()
	}
}