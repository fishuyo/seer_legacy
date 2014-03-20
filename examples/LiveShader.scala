
package com.fishuyo.seer
package examples.graphics.liveshader

import graphics._
import io._
import dynamic._

/**
	* This example sets up a render node to render a custom shader which
	* is loaded and editable through a ruby script.
	* 
	* Saving the ruby script causes it to be evaluated in turn reloading the shader code
	*/
object Main extends SeerApp {

	var live:Ruby = null

	// Render node encapsulates a scene, camera, and shader
	val node = new RenderNode 

	// set to use shader with name liveShader which we will load dynamically from our ruby script
	node.shader = "liveShader"

	// add a screen filling quad to the scene		
	node.scene.push( Plane() )

	// add render node to the Scene graph to have it be rendered
	SceneGraph.addNode(node) 

	// another way is to load shader code from files and call monitor to automatically reload on save
	// val shader = Shader.load("liveShader", File("liveShader.vert"),File("liveShader.frag"))
	// shader.monitor


	// init method called once from the underlying render thread on startup
	override def init(){
		// evaluate and monitor a ruby script
		live = new Ruby("LiveShader.rb")
	}

	// animate called once per frame
	override def animate(dt:Float){
		// call our ruby scripts animate method
		live.animate(dt)
	}
}