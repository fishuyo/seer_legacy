

package com.fishuyo.seer
package examples.graphics

import graphics._

object LoadObj extends SeerApp { 

	var model:Model = _
	var t = 0f


	def downloadFile(url:String, file:String){
		// see http://alvinalexander.com/scala/scala-how-to-download-url-contents-to-string-file
		import sys.process._
		import java.net.URL
		import java.io.File
		new URL(url) #> new File(file) !!
	}

	override def init(){

		// download a bunny from the internets
		downloadFile("http://fishuyo.com/stuff/bunny.obj", "bunny.obj")

		// load bunny
		model = Model.loadOBJ("bunny.obj")

		// generate normals from vertices (if no normals supplied)
		model.mesh.recalculateNormals()

		// modify material
		model.material = new SpecularMaterial
		model.material.color = RGBA(0f,.6f,.6f,1f)
		// model.mesh.primitive = Lines

	}

	override def draw(){
		model.draw()
	}

}