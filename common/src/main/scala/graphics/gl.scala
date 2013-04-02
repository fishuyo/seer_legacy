
package com.fishuyo
package graphics
import maths._
import spatial._

import javax.swing._

// import javax.media.opengl._
// import javax.media.opengl.awt._
// import javax.media.opengl.glu._
// import com.jogamp.opengl.util._
// import javax.media.opengl.fixedfunc.{GLLightingFunc => L}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.math.Matrix4

/*package object salami {
  trait GL10 extends com.badlogic.gdx.graphics.GL10
  trait GL20 extends com.badlogic.gdx.graphics.GL20
}*/

object GLImmediate {
  val renderer = new ImmediateModeRenderer20(true,true,2)
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

object GLPrimitive extends GLThis {

  def sphere( r:Float =1.0f ) = {

  }
  def cube( p:Pose = new Pose(), s:Vec3=Vec3(1), c:RGB = RGB.green ) = {
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

      // draw the cube
      mesh.render(Shader(), GL10.GL_TRIANGLES)
      //gl10.glPopMatrix()
    }
    new GLPrimitive(p,s,mesh,draw)
  }

  def quad = {
    val mesh = new Mesh(true,4,6, VertexAttribute.Position, VertexAttribute.TexCoords(0))
    mesh.setVertices( Array[Float](
      -1,-1,0,  0,0,
      1,-1,0,   1,0,
      1,1,0,    1,1,
      -1,1,0,   0,1
    ))

    mesh.setIndices( Array[Short](
      0,1,2, 0,2,3
    ))
    val draw = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
    new GLPrimitive(Pose(),Vec3(1.f),mesh,draw)
  }
}


class GLPrimitive(var p:Pose, var s:Vec3, var mesh:Mesh, val drawFunc:()=>Unit) extends GLDrawable {
  var color = Vec3(1.f)
  override def draw(){
    Shader.setColor(color,1.f)
    val scale = s / 2.f
    //val sm = new Matrix4().scl(scale.x,scale.y,scale.z)
    val m = new Matrix4().translate(p.pos.x,p.pos.y,p.pos.z).rotate(p.quat.toQuaternion()).scale(scale.x,scale.y,scale.z)
    Shader.matrixClear()
    Shader.matrixTransform(m)
    Shader.setMatrices()
    drawFunc()
  }
}

class Plot2D( var size:Int, var range:Float ) extends GLDrawable {
  var color = Vec3(1.f)
  var pose = Pose()
  var scale = Vec3(1.f)
  val mesh = new Mesh(false,size,0, VertexAttribute.Position)
  var data = Queue[Float]()
  data.enqueue(new Array[Float](size):_*)
  val vertices = new Array[Float](size*3)
  var dirty = true

  def apply(f:Float) = {
    data.enqueue(f)
    data.dequeue()
    for( i<-(0 until size)){
      vertices(3*i) = (i - size/2) / size.toFloat
      vertices(3*i+1) = data(i) / range
      vertices(3*i+2) = 0.f
    }
    dirty = true
  }

  override def draw(){ 
    if( dirty ){
      mesh.setVertices( vertices )
      dirty = false
    }
    Shader.setColor(color,1.f)
    val s = scale / 2.f
    val p = pose
    //val sm = new Matrix4().scl(scale.x,scale.y,scale.z)
    val m = new Matrix4().translate(p.pos.x,p.pos.y,p.pos.z).rotate(p.quat.toQuaternion()).scale(s.x,s.y,s.z)
    Shader.matrixClear()
    Shader.matrixTransform(m)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
  }

}



