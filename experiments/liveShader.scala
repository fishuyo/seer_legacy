
package com.fishuyo.seer
package examples.shader

import graphics._
import io._
import dynamic._


object Main extends App with Animatable {

	SimpleAppRun.loadLibs()
	Scene.push(this)
	var live:Ruby = null
	SimpleAppRun()

	override def init(){

		live =  new Ruby("liveShader.rb")

		val node = new RenderNode
		node.shader = "liveShader"
		node.scene.push( Mesh(Primitive2D.quad)) //Plane() )

		SceneGraph.addNode(node) //root = node

		// val shader = Shader.load("liveShader",File("liveShader.vert"),File("liveShader.frag"))
		// shader.monitor

	}
	override def animate(dt:Float){
		live.animate(dt)
	}
}