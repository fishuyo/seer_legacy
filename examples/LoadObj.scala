

package com.fishuyo.seer
package examples.graphics

import graphics._

object LoadObj extends SeerApp { 

	var model:Model = _

	override def init(){
		// load bunny
		model = Model.loadOBJ("../res/obj/bun.obj")

		// generate normals from vertices (if no normals supplied)
		model.mesh.recalculateNormals()

		// modify material
		model.material = new SpecularMaterial
		model.material.color = RGBA(0.f,.6f,.2f,1.f)
	}

	override def draw(){
		model.draw()
	}
}