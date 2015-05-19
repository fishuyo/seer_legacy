

package com.fishuyo.seer
package examples.graphics

import graphics._

import particle.SpringMesh

object SpringMeshExample extends SeerApp { 

	var model:Model = _
	var spring:SpringMesh = _
	var t = 0f

	def downloadFile(url:String, file:String){
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

		// create a spring simulation from mesh, with maximum stiffness
		spring = new SpringMesh(model.mesh, 1f)

		//  set simulation gravity to zero
		particle.Gravity.set(0,0,0)
	}

	override def draw(){
		model.draw()
	}

	override def animate(dt:Float){
		t += dt 

		// simulate the spring mesh
		// this automatically recalculates normals, and updates the mesh
		spring.animate(dt)

		// every second, snap a random vertex along its normal
		if(t > 1f){
			t = 0f
			val i = util.Random.int(0, model.mesh.vertices.length)()
			model.mesh.vertices(i) += model.mesh.normals(i) * (util.Random.float()*0.1f)
		}
	}
}