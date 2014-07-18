
package com.fishuyo.seer
package graphics
import spatial._
import spatial._


import scala.collection.immutable.Stack

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

/**
 * Model is a recursive collection of primitives with relative transforms
 */
object Model {
  def apply() = new Model
  def apply(pos:Vec3) = new Model{ pose = Pose(pos,Quat()) }
  def apply(p:Pose=Pose(),s:Vec3=Vec3(1)) = new Model{pose=p;scale=s}

  def apply(prim:Mesh) = { val m = new Model(); m.mesh = prim; m }
  def apply(prim:Drawable) = { val m = new Model(); m.addPrimitive(prim) }

  /** Makes recursive copy of Model */
  def apply(m:Model):Model = {
    val model = new Model{ pose = Pose(m.pose); scale = Vec3(m.scale) }
    model.color = m.color
    model.material = m.material
    m.children.foreach( (n) => model.children = model.children :+ Model(n) )
    m.primitives.foreach( (p) => model.primitives = model.primitives :+ p )
    model
  }

  def loadOBJ(s:String) = Obj(s)
}

class Model extends Drawable { // with geometry.Pickable {
	var pose = Pose()
	var scale = Vec3(1)
  var color = RGBA(1,1,1,1)
  var colorTransform = HSV(0,1,1)

  var material:BasicMaterial = new NoMaterial
  var shader = ""

  var children = Vector[Model]()
  var mesh = Mesh() // simple
  var primitives = Vector[Drawable]()

  // var pickable = Vector[geometry.Pickable]()

  var worldTransform = new Matrix4

  def material(m:BasicMaterial){ material = m }

  def translate(x:Float,y:Float,z:Float):Model = translate(Vec3(x,y,z))
  def translate(p:Vec3):Model = { pose.pos += p; this }

  def rotate(x:Float, y:Float, z:Float):Model = rotate(Quat(x,y,z))
  def rotate(q:Quat):Model = { pose.quat *= q; this }

  def scale(x:Float,y:Float,z:Float):Model = scale(Vec3(x,y,z))
  def scale(s:Float):Model = { scale *= s; this}
  def scale(s:Vec3):Model = { scale *= s; this}

  def transform(p:Pose,s:Vec3=Vec3(1)) = {
  	pose *= p
  	scale *= s 
    // val m = new Model(p,s)
    // children = children :+ m
    this
  }

  def addPrimitive(p:Drawable) = {
    primitives = primitives :+ p
    this
  }
  // def add(p:geometry.Pickable) = {
  //   pickable = pickable :+ p
  //   this
  // }
  def addChild(m:Model) = {
    // if(m.material.isInstanceOf[NoMaterial]) m.material = this.material
    children = children :+ m
    m
  }

  def foreach[T](f:(Model)=>T){
    f(this)
    children.foreach( _.foreach(f))
  }
  def getLeaves():Vector[Model] = {
    if( children.length == 0) Vector(this)
    else children.flatMap( _.getLeaves )
  }

  def applyColorTransform():Model = {
    ColorStack.push()
    ColorStack.transform(colorTransform)

    material.color = ColorStack.hsv
    children.foreach( _.applyColorTransform() )

    ColorStack.pop()
    this
  }

  override def draw(){
    MatrixStack.push()

    MatrixStack.transform(pose,scale)
    worldTransform.set(MatrixStack.model)

    // Shader.setColor(color)
    val old = Shader.shader.get.name
    if( shader != "" ){
      Shader().end()
      Shader(shader).begin()
    }
    Shader.setMaterial(material)
    Shader.setMatrices()

    mesh.draw()
    primitives.foreach( _.draw() )
    children.foreach( _.draw() )

    if( shader != ""){
      Shader().end()
      Shader(old).begin()
    }

    MatrixStack.pop()
  }


  // override def intersect(ray:Ray):Option[geometry.Hit] = {
    
  //   val (pos,scale) = (new Vector3(),new Vector3())
  //   worldTransform.getTranslation(pos)
  //   worldTransform.getScale(scale)

  //   var hits:Vector[geometry.Hit] = primitives.collect {
  //     case q:Quad => ray.intersectQuad(Vec3(pos.x,pos.y,pos.z), scale.x, scale.y ) match {
  //         case Some(t) => new geometry.Hit(this, ray, t)
  //         case None => null
  //     }
  //   }

  //   hits = (hits ++ children.map( _.intersect(ray).getOrElse(null) )).filterNot( _ == null).sorted
  //   if( hits.isEmpty ) None
  //   else Some(hits(0))
  // }

  override def toString() = {
    var out = pose.pos.toString + " " + scale.toString + " " + children.length + " " + primitives.length + "\n"
    children.foreach( out += _.toString )
    out
  }

}








