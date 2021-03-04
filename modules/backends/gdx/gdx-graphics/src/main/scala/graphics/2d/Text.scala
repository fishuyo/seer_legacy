package seer
package graphics

import spatial.Vec2
import spatial.Quat

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.{Texture => GdxTex}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
// import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
// import com.badlogic.gdx.tests.utils.GdxTest

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

object Text {
  val Center = 1
  val Left = 8
  val Right = 16
  var sb: SpriteBatch = _
  var s: Shader = _
  var camera = Camera

  def begin() {
    if (sb == null) {
      sb = new SpriteBatch
      s = Shader.loadCode("text", DefaultShaders.text)
    }
    sb.begin()
    sb.setShader(s()); //??
  }

  def end() {
    sb.setShader(null);
    sb.end()
  }

  def draw(
      font: Font,
      text: String,
      pos: Vec2,
      width: Float,
      halign: Int = Center
  ) = {
    MatrixStack.push()

    // MatrixStack.rotate(quat)
    MatrixStack.scale(font.scale)
    // MatrixStack.translate(pos);

    val res = font.font.draw(
      sb,
      text,
      pos.x / font.scale,
      pos.y / font.scale,
      width / font.scale,
      halign,
      true
    )
    // font.draw(sb, text, pos.x * 0.01f / scale, pos.y * 0.01f / scale);
    // font.draw(sb, text, x, y);

    MatrixStack(camera)
    s().setUniformMatrix("u_projTrans", MatrixStack.projectionModelViewMatrix())
    s().setUniformf("smoothing", font.smoothing)
    s().setUniformf("color", font.color.r, font.color.g, font.color.b)
    MatrixStack.pop()
    res
  }

  def draw(font: Font, text: String, pos: Vec2) = {
    MatrixStack.push()

    // MatrixStack.rotate(quat)
    MatrixStack.scale(font.scale)
    // MatrixStack.translate(pos);

    val res = font.font.draw(sb, text, pos.x / font.scale, pos.y / font.scale)
    // font.draw(sb, text, pos.x * 0.01f / scale, pos.y * 0.01f / scale);
    // font.draw(sb, text, x, y);

    MatrixStack(camera)
    s().setUniformMatrix("u_projTrans", MatrixStack.projectionModelViewMatrix())
    s().setUniformf("smoothing", font.smoothing)
    s().setUniformf("color", font.color.r, font.color.g, font.color.b)
    MatrixStack.pop()
    res
  }
}

class Font(path: String = "/Library/Fonts/Arial Unicode.ttf") {
  var font: BitmapFont = _
  var scale = 0.001f
  var smoothing = 8f / 16f
  var color = RGB(1)

  loadFont(path)

  def loadFont(path: String) {
    val generator = new FreeTypeFontGenerator(
      Gdx.files.internal(path)
    )
    val parameter = new FreeTypeFontParameter()
    parameter.color = Color.WHITE
    parameter.size = 100
    parameter.genMipMaps = true
    parameter.minFilter = TextureFilter.Linear //MipMapLinearNearest
    parameter.magFilter = TextureFilter.Linear
    font = generator.generateFont(parameter)
    font.getData().setLineHeight(100f * 1.25f)
    generator.dispose()
  }

  def lineHeight(h: Float) {
    font.getData().setLineHeight(100f * h)
  }
  def size(s: Float) {
    font.getData().setScale(s)
  }

}
