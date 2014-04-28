
package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.Gdx

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
		ret.linewidth = m.linewidth
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
	var linewidth = 1

	var texture = None:Option[GdxTexture]
	var normalMap = None:Option[GdxTexture]
	var specularMap = None:Option[GdxTexture]
	var textureMix = 0.f
	var lightingMix = 0.f
	var shininess = 0

	def loadTexture(path:String){
		loadTexture(new GdxTexture(Gdx.files.internal(path)))
	}
	def loadTexture(t:GdxTexture){
		texture = Some(t)
		textureMix = 1.f
	}
}

class NoMaterial extends BasicMaterial

// class DiffuseMaterial extends BasicMaterial with Diffuse
class DiffuseMaterial extends BasicMaterial{
	lightingMix = 1.f
}
// class SpecularMaterial extends BasicMaterial with Specular

class SpecularMaterial extends DiffuseMaterial{
	shininess = 30
}


class ShaderMaterial(var shader:String) extends BasicMaterial





