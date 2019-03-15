
package com.fishuyo.seer
package graphics


trait Material

trait Diffuse extends Material 

trait Specular extends Material {
	var shininess = 30
}

object Material{
	def basic = new BasicMaterial
	def diffuse = new DiffuseMaterial
	def specular = new SpecularMaterial

	def apply(m:BasicMaterial) = {
		val ret = new BasicMaterial
		ret.color = m.color
		ret.visible = m.visible
		ret.transparent = m.transparent
		ret.wireframe = m.wireframe
		ret.lineWidth = m.lineWidth
		ret.texture = m.texture
		ret.normalMap = m.normalMap
		ret.specularMap = m.specularMap
		ret.textureMix = m.textureMix
		ret.lightingMix = m.lightingMix
		ret.shininess = m.shininess
		ret
	}

}

class BasicMaterial extends Material {
	var color = RGBA(1,1,1,1)

	var visible = true
	var transparent = false	
	var wireframe = false
	var lineWidth = 1

	var texture = None:Option[Texture]
	var normalMap = None:Option[Texture]
	var specularMap = None:Option[Texture]
	var textureMix = 0f
	var lightingMix = 0f
	var shininess = 0

	def loadTexture(path:String){
		loadTexture(Texture(path))
	}
	def loadTexture(t:Texture){
		texture = Some(t)
		textureMix = 1f
	}

	def diffuse() = lightingMix = 1f; shininess = 0;
	def specular() = lightingMix = 1f; shininess = 30;
}

class NoMaterial extends BasicMaterial

// class DiffuseMaterial extends BasicMaterial with Diffuse
class DiffuseMaterial extends BasicMaterial{
	lightingMix = 1f
}
// class SpecularMaterial extends BasicMaterial with Specular

class SpecularMaterial extends DiffuseMaterial{
	shininess = 30
}


class ShaderMaterial(var shader:String) extends BasicMaterial





