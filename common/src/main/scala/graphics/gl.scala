
package com.fishuyo
package graphics
import maths._
import spatial._


// import scala.collection.mutable.ListBuffer
// import scala.collection.mutable.ArrayBuffer
// import scala.collection.mutable.Queue
import scala.collection.immutable.Stack

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.math.Matrix4

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
  def init(){}
  def draw(){}
  def draw2(){}
}
trait GLAnimatable extends GLDrawable {
  def step( dt: Float){}
}

// class Poseable(val p:GlDrawable) extends GLDrawable {
//   var pose = Pose()
//   var scale = Vec3(1)
//   override def draw(){

//   }
// }

/*
* Model as a collection of primitives with relative transforms, and animation
*/
object Model {
  def apply(pose:Pose=Pose(),scale:Vec3=Vec3(1)) = new Model(pose,scale)
  def apply(prim:GLDrawable) = { val m=new Model(); m.add(prim) }
  def apply(m:Model):Model = {
    val model = new Model(Pose(m.pose),Vec3(m.scale))
    model.color.set(m.color)
    m.nodes.foreach( (n) => model.nodes = model.nodes :+ Model(n) )
    m.primitives.foreach( (p) => model.primitives = model.primitives :+ p )
    model
  }
}
class Model(var pose:Pose=Pose(), var scale:Vec3=Vec3(1)) extends GLAnimatable {
  var color = Vec3(1.f)
  var nodes = Vector[Model]()
  var primitives = Vector[GLDrawable]()

  def transform(p:Pose,s:Vec3=Vec3(1.f)) = {
    val m = new Model(p,s)
    nodes = nodes :+ m
    m
  }
  def add(p:GLDrawable) = {
    primitives = primitives :+ p
    this
  }
  def addNode(m:Model) = {
    nodes = nodes :+ Model(m)
  }
  def getLeaves():Vector[Model] = {
    if( nodes.length == 0) Vector(this)
    else nodes.flatMap( _.getLeaves )
  }

  override def draw(){
    MatrixStack.push()
    MatrixStack.transform(pose,scale)

    Shader.setMatrices()
    Shader.setColor(color,1.f)

    primitives.foreach( _.draw() )
    nodes.foreach( _.draw() )

    MatrixStack.pop()
  }
  override def step(dt:Float){

  }

  override def toString() = {
    var out = pose.pos.toString + " " + scale.toString + " " + nodes.length + " " + primitives.length + "\n"
    nodes.foreach( out += _.toString )
    out
  }

}

// class GLPrimitive(var mesh:Mesh, val drawFunc:()=>Unit) extends GLDrawable {
//   override def draw(){
//     drawFunc()
//   }
// }

class GLPrimitive(var pose:Pose=Pose(), var scale:Vec3=Vec3(1), var mesh:Mesh, val drawFunc:()=>Unit) extends GLDrawable {
  var color = Vec3(1.f)
  override def draw(){
    Shader.setColor(color,1.f)
    val s = scale / 2.f

    MatrixStack.push()
    MatrixStack.transform(pose,s)

    Shader.setMatrices()
    drawFunc()
    
    MatrixStack.pop()
  }
}





