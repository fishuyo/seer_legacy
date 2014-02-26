
package com.fishuyo.seer
package graphics

import com.badlogic.gdx.graphics.{Texture => GdxTexture}

trait Material

trait Diffuse extends Material 

trait Specular extends Material {
	var shininess = 30
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
}

class NoMaterial extends BasicMaterial

class DiffuseMaterial extends BasicMaterial with Diffuse
class SpecularMaterial extends BasicMaterial with Specular


class ShaderMaterial(var shader:String) extends BasicMaterial





