

package com.fishuyo.seer
package examples.graphics

import com.fishuyo.seer.graphics._


object TextureExample extends SeerApp { 

	var model = Sphere()

	def downloadFile(url:String, file:String){
		import sys.process._
		import java.net.URL
		import java.io.File
		new URL(url) #> new File(file) !!
	}

	override def init(){
		
		// download an image to use
		downloadFile("https://c1.staticflickr.com/3/2684/4500364279_f1a98e0042.jpg", "texture.jpg")

		// load texture
		model.material = Material.basic
		model.material.loadTexture("texture.jpg")
	}

	override def draw(){
		model.rotate(0,0.01f,0)
		model.draw()
	}
}