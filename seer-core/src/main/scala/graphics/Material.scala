
package com.fishuyo.seer
package graphics


trait Material {
	var texture = None:Option[Texture]
	var normalMap = None:Option[Texture]
	var specularMap = None:Option[Texture]
	var textureMix = 0.f

	var color = RGBA(1,1,1,1)

	var visible = true
	var transparent = false	
	var wireframe = false
	var linewidth = 1
}

class NoMaterial extends Material
class BasicMaterial extends Material

class DiffuseMaterial extends Material {

}

class SpecularMaterial extends Material {

}

class TextureMaterial extends Material {

}

