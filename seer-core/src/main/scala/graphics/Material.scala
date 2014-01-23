
package com.fishuyo.seer
package graphics


trait Material {

	var color = RGB.white

	var transparent = false	
	var opacity = 1.f

	var visible = true

	var wireframe = false
	var linewidth = 1
}

class NoMaterial extends Material
class BasicMaterial extends Material

class TexturedMaterial extends Material {
	var texture = None:Option[Texture]
	var normalMap = None:Option[Texture]
	var specularMap = None:Option[Texture]
}

