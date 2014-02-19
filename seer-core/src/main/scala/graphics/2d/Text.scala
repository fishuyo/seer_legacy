

package com.fishuyo.seer
package graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL10
// import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.{ Texture => GdxTexture }
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
// import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
// import com.badlogic.gdx.tests.utils.GdxTest

object Text {

	var texture:GdxTexture = null
	var font:BitmapFont = null
	var smoothing = 1.f/16.f

	def setSmoothing(v:Float){ smoothing = v }

	def loadFont(path:String="../res/fonts/arial"){
		texture = new GdxTexture(Gdx.files.internal(path + ".png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
		font = new BitmapFont(Gdx.files.internal(path + ".fnt"), new TextureRegion(texture), false);
	}

	def render(text:String, x:Float, y:Float){
		if(font == null) return
		Shader("text")
		val s = Shader.shader.get.program.get
		SpriteBatch.begin()
		SpriteBatch.setShader(s);
		font.drawMultiLine(SpriteBatch, text, x, y);
			MatrixStack.push()
			MatrixStack.scale(0.01f)
			MatrixStack(Camera)
			s.setUniformMatrix("u_projTrans", MatrixStack.projectionModelViewMatrix())
			s.setUniformf("smoothing", smoothing)
			MatrixStack.pop()
		SpriteBatch.setShader(null);
		SpriteBatch.end()

	}
}

