
package com.fishuyo
package graphics
import maths._

import javax.swing._

// import javax.media.opengl._
// import javax.media.opengl.awt._
// import javax.media.opengl.glu._
// import com.jogamp.opengl.util._
// import javax.media.opengl.fixedfunc.{GLLightingFunc => L}

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10
import com.badlogic.gdx.graphics.glutils.ShaderProgram

package object salami {
  trait GL10 extends com.badlogic.gdx.graphics.GL10
  trait GL20 extends com.badlogic.gdx.graphics.GL20
}

object Shader {
  var dirty = false
  var shader: ShaderProgram = null
  var vertPath = ""
  var fragPath = ""
  def apply(v:String,f:String) = {
    vertPath = v
    fragPath = f
    val s = new ShaderProgram( Gdx.files.internal(v), Gdx.files.internal(f))
    if( s.isCompiled() ){
      shader = s
    }else{
      println( s.getLog() )
    }
  }
  def apply(s:ShaderProgram) = shader = s
  def apply() = shader
  def reload() = dirty = true
  def update() = {
    if( dirty ){
      this.apply(vertPath,fragPath)
      dirty = false
    }
  }
}

object GLImmediate {
  val renderer = new ImmediateModeRenderer10
}

trait GLThis {
  def gli = GLImmediate.renderer
  def gl = Gdx.gl
  def gl10 = Gdx.gl10
  def gl11 = Gdx.gl11
  def gl20 = Gdx.gl20
}

trait GLDrawable extends GLThis {
  def draw(){}
}
trait GLAnimatable extends GLDrawable {
  def step( dt: Float){}
}
class GLLight {}

object GLPrimitive extends GLThis {

  def sphere( r:Float =1.0f ) = {

  }
  def cube( p:Vec3 = Vec3(0), s:Vec3=Vec3(1), c:RGB = RGB.green, wire:Boolean=false ) = {
    val mesh = new Mesh(true,24,36, VertexAttribute.Position, VertexAttribute.Normal)
    mesh.setVertices( Array[Float](
      -1,1,1,   0,0,1,
      1,1,1,    0,0,1,
      -1,-1,1,  0,0,1,
      1,-1,1,   0,0,1,

      -1,1,-1,   0,0,-1,
      1,1,-1,    0,0,-1,
      -1,-1,-1,  0,0,-1,
      1,-1,-1,   0,0,-1,

      -1,1,-1,  -1,0,0,
      -1,1,1,   -1,0,0,
      -1,-1,-1, -1,0,0,
      -1,-1,1,  -1,0,0,

      1,1,1,    1,0,0,
      1,1,-1,   1,0,0,
      1,-1,1,   1,0,0,
      1,-1,-1,  1,0,0,

      -1,1,-1,  0,1,0,
      1,1,-1,   0,1,0,
      -1,1,1,   0,1,0,
      1,1,1,    0,1,0,

      -1,-1,-1, 0,-1,0,
      1,-1,-1,  0,-1,0,
      -1,-1,1,  0,-1,0,
      1,-1,1,   0,-1,0
    ))

    mesh.setIndices( Array[Short](
      0,1,2, 1,2,3, //f
      4,5,6, 5,6,7, //b
      8,9,10, 9,10,11, //l
      12,13,14, 13,14,15, //r
      16,17,18, 17,18,19, //t
      20,21,22, 21,22,23 //b
    ))

    val draw = () => {
      //gl.glLineWidth(2.0f);
      //if( wire ) gl10.glPolygonMode(GL10.GL_FRONT_AND_BACK, GL10.GL_LINE);
      //else gl10.glPolygonMode(GL10.GL_FRONT_AND_BACK, GL10.GL_FILL );

      //gl10.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, Array(c.r, c.g, c.b, 0.f), 0 );
      //gl.glEnable( GL10.GL_LIGHTING )

      //gl10.glPushMatrix()
      //gl10.glTranslatef (p.x, p.y, p.z); // viewing transformation
      //val scale = s / 2.0f;
      //gl10.glScalef (scale.x, scale.y, scale.z);      // modeling transformation

      // draw the cube
      mesh.render(Shader(), GL10.GL_TRIANGLES)
      //gl10.glPopMatrix()
    }
    new GLPrimitive(mesh,draw)
  }

  def quad = {

  }
}
class GLPrimitive(var mesh:Mesh, val drawFunc:()=>Unit) extends GLDrawable {
  override def draw(){
    drawFunc()
  }
}

