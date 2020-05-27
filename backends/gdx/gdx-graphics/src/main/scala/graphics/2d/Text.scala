

package com.fishuyo.seer
package graphics

import spatial.Vec3
import spatial.Quat

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
// import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.{ Texture => GdxTex }
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
// import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
// import com.badlogic.gdx.tests.utils.GdxTest


// class SpriteBatchRenderer extends Renderer {
//   val sb = new SpriteBatch 
// }

// object Text extends RenderNode() {

// }

object Text {

  var texture:GdxTex = null
  var font:BitmapFont = null
  var smoothing = 1f/16f
  var sb:SpriteBatch = null
  var s:Shader = null
  var camera = Camera //new OrthographicCamera(800,800)
  var scale = 0.01f

  def setSmoothing(v:Float): Unit ={ smoothing = v }

  def loadFont(path:String="../res/fonts/arial"): Unit ={
    texture = new GdxTex(Gdx.files.internal(path + ".png"), true); // true enables mipmaps
    texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear); // linear filtering in nearest mipmap image
    font = new BitmapFont(Gdx.files.internal(path + ".fnt"), new TextureRegion(texture), false);
    sb = new SpriteBatch
    s = Shader.load(DefaultShaders.text._1,DefaultShaders.text._2)
  }

  def begin(): Unit ={
    sb.begin()
    sb.setShader(s());
  }
  def end(): Unit ={
    // sb.setShader(null);
    sb.end()
  }
  def render(text:String, pos:Vec3): Unit ={
    if(font == null) return
    // val s = Shader.shader.get.program.get
    MatrixStack.push()

    // MatrixStack.rotate(quat)
    MatrixStack.scale(scale)
    // MatrixStack.translate(x,y,0);   

    font.draw(sb, text, pos.x*0.01f/scale, pos.y*0.01f/scale);
    // font.draw(sb, text, x, y);

    MatrixStack(camera)
    s().setUniformMatrix("u_projTrans", MatrixStack.projectionModelViewMatrix())
    s().setUniformf("smoothing", smoothing)
    MatrixStack.pop()
  }
}

