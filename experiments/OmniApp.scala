
package com.fishuyo.seer
package allosphere

import graphics._

object Main extends SeerApp with OmniDrawable {


	val omni = new OmniStereo
	var omniEnabled = true

	val cube = Cube()

	val lens = new Lens()
	lens.near = 0.01
	lens.far = 40.0
	lens.eyeSep = 0.03

	var omniShader:Shader = _

	override def init(){
		// omni.onCreate();
		
		omniShader = Shader.load("omni", OmniStereo.glsl + DefaultShaders.basic._1, DefaultShaders.basic._2 )
	}

	override def draw(){
	
		val vp = Viewport(Window.width, Window.height)
	
		if (omniEnabled) {
			omni.onFrame(this, lens, Camera.nav, vp);
		} else {
			omni.onFrameFront(this, lens, Camera.nav, vp);
		}
	}

	override def onDrawOmni(){
		omniShader.begin();
		// mOmni.uniforms(omniShader);
		
		cube.draw		
		omniShader.end();
	}




}