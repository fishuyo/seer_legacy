
package com.fishuyo
package graphics

import maths._
import spatial._

import scala.collection.mutable.Queue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._

/* 
* Plot stream of size data points scaled by range
*/
class Plot2D( var size:Int, var range:Float=1.f) extends GLDrawable {
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
    MatrixStack.push()
    MatrixStack.transform(pose,s)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
    MatrixStack.pop()
  }

}


/* 
* Display Audio Samples and boundary/playback cursors
*/
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
    MatrixStack.push()
    MatrixStack.transform(pose,s)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
    Shader.setColor(Vec3(1.f,1.f,0),1.f)
    cursorMesh.render(Shader(), GL10.GL_LINES)
    MatrixStack.pop()
  }

}



