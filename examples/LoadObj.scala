

package com.fishuyo.seer
package examples.graphics.obj

import graphics._
import maths._
import dynamic._

object Main extends SeerApp { 

	var model:Model = _

	override def init(){
		// load bunny
		model = Model.loadOBJ("res/obj/bunny.obj")

		// modify material
		model.material = new SpecularMaterial
		model.material.color = RGBA(0.f,.6f,.2f,1.f)
	}

	override def draw(){
		model.draw()
	}
}