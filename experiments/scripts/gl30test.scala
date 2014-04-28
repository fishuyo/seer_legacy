
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.math.Matrix4

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL43

import org.lwjgl.opengl._


class FTexture(var w:Int,var h:Int) {
  val data = BufferUtils.newFloatBuffer( 4*w*h )
  val handle = GL11.glGenTextures() //getGLHandle()
  val target = GL20.GL_TEXTURE_2D
  val iformat = GL30.GL_RGBA32F //GL20.GL_RGBA
  val format = GL20.GL_RGBA
  val dtype = GL20.GL_FLOAT
  // println(Gdx.graphics.supportsExtension("texture_float"))

  val filterMin = GL20.GL_NEAREST
  val filterMag = GL20.GL_NEAREST
  val mWrapS = GL20.GL_CLAMP_TO_EDGE
  val mWrapT = GL20.GL_CLAMP_TO_EDGE
  val mWrapR = GL20.GL_CLAMP_TO_EDGE

  // bind(0)
  // update()
  // params()

  def getGLHandle() = {
    val buf = BufferUtils.newIntBuffer(1)
    Gdx.gl.glGenTextures(1,buf)
    buf.get(0)
  }

  def bind(i:Int){
    Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0+i)
    Gdx.gl.glEnable(target)
    Gdx.gl.glBindTexture(target, handle)    
  }

  def params(){
    // Gdx.gl.glBindTexture(target, handle);
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_MAG_FILTER, filterMag)
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_MIN_FILTER, filterMin)
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_WRAP_S, mWrapS);
    Gdx.gl.glTexParameterf(target, GL20.GL_TEXTURE_WRAP_T, mWrapT);
    Gdx.gl.glTexParameterf(target, GL30.GL_TEXTURE_WRAP_R, mWrapR);
    if (filterMin != GL20.GL_LINEAR && filterMin != GL20.GL_NEAREST) {
      Gdx.gl.glTexParameteri(target, GL20.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // automatic mipmap
    }
    // Gdx.gl.glBindTexture(target, 0);
  }

  def update(){
    data.rewind
    // Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
    Gdx.gl.glTexImage2D(target,0,iformat,w,h,0,format,dtype,data)
    Gdx.gl.glGenerateMipmap(GL11.GL_TEXTURE_2D);
    // println(Gdx.gl.glGetError())
  }
}

object Script extends SeerScript {

  var loaded = false
  var ft:FTexture = null
  var t:GdxTexture = null

	override def draw(){

    
    if( loaded == false){
      loaded = true

      ft = new FTexture(256,256)
      val a = Array.fill(4*ft.w*ft.h)(.5f)
      for(i <- 0 until 256*256) ft.data.put(Array(1.f,0.f,0.f,1.f))
      ft.bind(0)
      ft.update
      ft.params

      t = new GdxTexture(Gdx.files.internal("../res/img/moon/moon.png"))

    }

    val s = Sphere().translate(0,0,-10)
    ft.bind(0)
    Shader.shader.get.uniforms("u_texture0") = 0
    Shader.shader.get.setUniforms
    Shader.textureMix = 1.f
    s.material = Material.basic
    s.material.textureMix = 1.f
    s.draw

		// GL11.glClearColor(1.f,1.f,0.f,1.f)
		// GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)
  //   GL11.glBegin(GL11.GL_TRIANGLES);
  //     // Top & Red
  //     GL11.glColor3f(1.0f, 0.0f, 0.0f);
  //     GL11.glVertex2f(0.0f, 1.0f);

  //     // Right & Green
  //     GL11.glColor3f(0.0f, 1.0f, 0.0f);
  //     GL11.glVertex2f(1.0f, 1.0f);

  //     // Left & Blue
  //     GL11.glColor3f(0.0f, 0.0f, 1.0f);
  //     GL11.glVertex2f(-1.0f, -1.0f);
  // 	GL11.glEnd();
	}

	override def animate(dt:Float){
	}

}
Script
