
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
import scala.collection.mutable.ArrayBuffer
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

  def sphere(p:Pose=Pose(), s:Vec3=Vec3(1), radius:Float=2.f,bands:Int=30) = {
    val vert = new ListBuffer[Float]
    val indx = new ListBuffer[Short]

    for ( lat <- (0 to bands)){
      var theta = lat * Math.Pi / bands
      var sinTheta = Math.sin(theta)
      var cosTheta = Math.cos(theta)

      for (long <- (0 to bands)){
        var phi = long * 2 * Math.Pi / bands
        var sinPhi = Math.sin(phi)
        var cosPhi = Math.cos(phi)
        var x = cosPhi * sinTheta
        var y = cosTheta
        var z = sinPhi * sinTheta
        var u = 1.0 - (long / bands)
        var v = lat / bands
        vert += x.toFloat
        vert += y.toFloat
        vert += z.toFloat
        vert += u.toFloat
        vert += v.toFloat
        vert += radius * x.toFloat
        vert += radius * y.toFloat
        vert += radius * z.toFloat
      }
    }

    for ( lat <- (0 until bands)){
      for ( long <- (0 until bands)){
       var first = (lat * (bands + 1)) + long
       var second = first + bands + 1
       indx += first.toShort
       indx += second.toShort
       indx += (first + 1).toShort
       indx += second.toShort
       indx += (second + 1).toShort
       indx += (first + 1).toShort
     }
    }

    val mesh = new Mesh(true, vert.size/8, indx.size, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
    mesh.setVertices(vert.toArray)
    mesh.setIndices(indx.toArray)

    val draw = () => {

      // draw the cube
      mesh.render(Shader(), GL10.GL_TRIANGLES)
      //gl10.glPopMatrix()
    }
    new GLPrimitive(p,s,mesh,draw)
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

  def cylinder(p:Pose=Pose(), s:Vec3=Vec3(1), r1:Float=1.f, r2:Float=1.f, vertCount:Int=60) = {
    val vert = new ListBuffer[Float]
    val indx = new ListBuffer[Short]

    val indxCount = vertCount+2
    var theta = 0.0

    for (j <- (0 until vertCount)){
      val r = (if( 3*j % 2 == 0) r1 else r2)
      val x = math.cos(theta).toFloat
      val y = math.sin(theta).toFloat

      vert += x
      vert += y
      vert += (r1-r2) / (s.z) //0.f

      vert += r*x
      vert += r*y
      vert += (if( 3*j % 2 == 0) 0.f else 2.f) //-1.f)
      theta += 2 * math.Pi / (vertCount)
    }

    for ( j <- (0 until indxCount)) {
      indx += (j % vertCount).toShort
    }

    val mesh = new Mesh(true, vert.size/6, indx.size, VertexAttribute.Normal, VertexAttribute.Position)
    mesh.setVertices(vert.toArray)
    mesh.setIndices(indx.toArray)

    val draw = () => {
      mesh.render(Shader(), GL10.GL_TRIANGLE_STRIP)
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


class GLPrimitive(var pose:Pose, var scale:Vec3, var mesh:Mesh, val drawFunc:()=>Unit) extends GLDrawable {
  var color = Vec3(1.f)
  override def draw(){
    Shader.setColor(color,1.f)
    val s = scale / 2.f
    //val sm = new Matrix4().scl(scale.x,scale.y,scale.z)
    val m = new Matrix4().translate(pose.pos.x,pose.pos.y,pose.pos.z).rotate(pose.quat.toQuaternion()).scale(s.x,s.y,s.z)
    //Shader.matrixClear()
    val oldm = new Matrix4(Shader.modelMatrix)
    Shader.matrixTransform(m)
    Shader.setMatrices()
    drawFunc()
    Shader.modelMatrix = oldm
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
    val m = new Matrix4().translate(p.pos.x,p.pos.y,p.pos.z).rotate(p.quat.toQuaternion()).scale(s.x,s.y,s.z)
    Shader.matrixClear()
    Shader.matrixTransform(m)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
  }

}

class Trace3D( var size:Int ) extends GLDrawable {
  var color1 = Vec3(1.f,0.f,0.f)
  var color2 = Vec3(0.f,0.f,1.f)
  var pose = Pose()
  var scale = Vec3(1.f)
  val mesh = new Mesh(false,size,0, VertexAttribute.Position, VertexAttribute.ColorUnpacked)
  var data = Queue[Vec3]()
  for( i<-(0 until size)) data.enqueue(Vec3())
  val vertices = new Array[Float](size*(3+4))
  var dirty = true

  def apply(v:Vec3) = {
    val u = Vec3(v)
    data.enqueue(u)
    data.dequeue()

    for( i<-(0 until size)){
      val c = color1.lerp(color2, i/size.toFloat)
      vertices(7*i) = data(i).x
      vertices(7*i+1) = data(i).y
      vertices(7*i+2) = data(i).z
      vertices(7*i+3) = c.x
      vertices(7*i+4) = c.y
      vertices(7*i+5) = c.z
      vertices(7*i+6) = 1.f
    }
    dirty = true
  }

  override def draw(){ 
    if( dirty ){
      mesh.setVertices( vertices )
      dirty = false
    }
    Shader.setColor(color1,1.f)
    val s = scale // / 2.f
    val p = pose
    val m = new Matrix4().translate(p.pos.x,p.pos.y,p.pos.z).rotate(p.quat.toQuaternion()).scale(s.x,s.y,s.z)
    Shader.matrixClear()
    Shader.matrixTransform(m)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
  }

}

class AudioDisplay(val size:Int) extends GLDrawable {
  var color = Vec3(1)
  var pose = Pose()
  var scale = Vec3(1)
  val mesh = new Mesh(false,size,0, VertexAttribute.Position)
  val vertices = new Array[Float](size*3)
  val cursorMesh = new Mesh(false,6,0,VertexAttribute.Position)
  val cursorVert = new Array[Float](6*(3))
  var dirty = true
  var samples:Array[Float] = _
  var left = 0
  var right = 0

  def setSamples(s:Array[Float], l:Int=0, r:Int=0){
    samples = s
    left = l
    right = if(r == 0) s.size-1 else r
    for( i<-(0 until size)){
      val s = i / (size-1).toFloat * (right-left) + left
      val si = s.toInt
      val si2 = if( si >= right) left else si + 1
      val f = s-si
      vertices(3*i) = (i - size/2) / size.toFloat
      vertices(3*i+1) = samples(si)*(1.f-f) + samples(si2)*f
      vertices(3*i+2) = 0.f
    }
    dirty = true
  }

  def setCursor(i:Int,sample:Int){
    val x = (sample - left).toFloat / (right-left).toFloat - .5f
    cursorVert(6*i) = x
    cursorVert(6*i+1) = 0.5f
    cursorVert(6*i+2) = 0.f
    cursorVert(6*i+3) = x
    cursorVert(6*i+4) = -0.5f
    cursorVert(6*i+5) = 0.f
    dirty = true
  }

  override def draw(){ 
    if( dirty ){
      mesh.setVertices( vertices )
      cursorMesh.setVertices( cursorVert)
      dirty = false
    }
    Shader.setColor(color,1.f)
    val s = scale / 2.f
    val p = pose
    val m = new Matrix4().translate(p.pos.x,p.pos.y,p.pos.z).rotate(p.quat.toQuaternion()).scale(s.x,s.y,s.z)
    Shader.matrixClear()
    Shader.matrixTransform(m)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
    Shader.setColor(Vec3(1.f,1.f,0),1.f)
    cursorMesh.render(Shader(), GL10.GL_LINES)
  }

}



