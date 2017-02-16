
package com.fishuyo.seer
package examples.graphics

import graphics._
import spatial._
import io._

/**
	* This example sets up a render node to render a custom shader
	* which is reloaded on modification.
	* 
	* See "./shaders/live.vert" and "./shaders/live.frag"
	* 
	*/
object LiveShader extends SeerApp {

	// to use as uniforms
	var t = 0f 
	var zoom = 1f 
	var mouse = Vec2()
	var shader:Shader = _

	override def init(){
		// Render node encapsulates a scene, camera, and shader
		val node = new RenderNode 

		// load and automatically reload shader live.vert / live.frag
		shader = Shader.load("live", "shaders/live")
		shader.monitor()
		node.renderer.shader = shader

		// add a screen filling quad to the scene		
		node.renderer.scene.push( Plane() )

		// add render node to the Scene graph to have it be rendered
		RenderGraph.addNode(node) 

	}

	// since the RenderNode has been added to the Scene Graph
	// we don't need to draw anything here
	override def draw(){ FPS.print }

	override def animate(dt:Float){
		t += dt
		shader.uniforms("time") = t
		shader.uniforms("zoom") = zoom
		shader.uniforms("mouse") = mouse
	}

	// OSX trackpad control
	// Trackpad.connect()
	// Trackpad.bind( (touch) => {
	// 	val i = touch.count
	// 	val vel = touch.vel
	// 	if(i == 1){
	// 		mouse += vel * 0.05 * math.pow(zoom,8)
	// 	} else if(i == 2){
	// 		zoom += vel.y * -0.001
	// 	}
	// })
}