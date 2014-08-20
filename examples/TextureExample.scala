

package com.fishuyo.seer
package examples.graphics

import com.fishuyo.seer.graphics._

object TextureExample extends SeerApp { 

	var model = Sphere()

	override def init(){
		// load texture
		model.material = Material.basic
		model.material.loadTexture("../res/img/moon/moon.png")
	}

	override def draw(){
		model.rotate(0,0.01f,0)
		model.draw()
	}
}