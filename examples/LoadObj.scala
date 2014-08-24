

package com.fishuyo.seer
package examples.graphics

import graphics._

object LoadObj extends SeerApp { 

	var model:Model = _

	def downloadFile(url:String, file:String){
		import sys.process._
		import java.net.URL
		import java.io.File
		new URL(url) #> new File(file) !!
	}

	override def init(){

		// download bunny
		downloadFile("http://graphics.stanford.edu/~mdfisher/Data/Meshes/bunny.obj", "bunny.obj")

		// load bunny
		model = Model.loadOBJ("bunny.obj")

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