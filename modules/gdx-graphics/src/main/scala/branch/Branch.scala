
package com.fishuyo.seer
package branch

import graphics._
import spatial._

import collection.mutable.ListBuffer
import collection.mutable.Stack
import scala.annotation.tailrec

/**
  * Branch companion object for creating branches and initializing their state correctly
  */
object Branch {
  def apply(parent0:Branch, p:Pose, ratio:Float=1f, relative:Boolean=true) = new Branch { 
    parent = parent0
    if(relative) relPose = p
    if(parent != null){
      if(relative) pose = parent.pose * p
      else {
        pose = Pose(p)
        relPose = parent.pose.inverse * pose
      }
      depth = parent.depth + 1
      maxLength = parent.maxLength * ratio
      length = parent.length * ratio
      thick = parent.thick * ratio
    } else pose = Pose(p)
    lPose = Pose(pose)
    pose0 = Pose(pose)
    updateConstants()
  }
}

/**
  * Branch class as nodes in a tree
  */
class Branch {
  var parent:Branch = null
  val children = ListBuffer[Branch]()
  var pose = Pose()     //current pose
  var lPose = Pose()    // last pose for rotational verlet integration
  var pose0 = Pose()    // equilibrium pose (pose of no restoring force)
  var relPose = Pose()  // relative pose from parent
  var dir = Vec3()
  var age = 0f
  var depth = 0
  var length = 1f
  var minLength = 0.0001f
  var maxLength = 1f

  var accel = Vec3(0)
  var euler = Vec3(0.0f,0.0f,0.0f)
  var lEuler = Vec3(0.0f,0.0f,0.0f)
  var mass = 1.0f
  var damp = 50.0f

  var thick = 10.2f
  var taper = .5f
  var k = 10.0f // * thick*thick*thick / (length*length*length)

  var dna = ""

  def updateConstants(){
    mass = 1.0f //+ thick*thick*length * 100f
    k = 10.0f  //+ thick*thick*thick / (length*length*length) * 10f
  }

  /** run a function on branch and all children breadth first */
  def foreach(f:PartialFunction[Branch,Unit]){
    var branches = ListBuffer[Branch]()
    branches += this
    while(!branches.isEmpty){
      val b = branches.head
      f(b)
      branches.remove(0)
      branches ++= b.children
    }
  }  
  /** run a function on branch and all children depth first */
  def visitPre(f:PartialFunction[Branch,Unit]){
    var branches = Stack[Branch]()
    branches.push(this)
    while(!branches.isEmpty){
      val b = branches.pop
      f(b)
      branches.pushAll(b.children)
    }
  }
  /** run a function on branch and all children depth first */
  def visitPost(f:PartialFunction[Branch,Unit]){
    @tailrec def visit(branches:List[Branch], eval:List[Branch]){
      branches match {
        case b :: bs => visit(b.children ++: bs, b :: eval)
        case Nil => eval match {
          case e :: es => f(e); visit(Nil, es)
          case Nil => ()
        }
      }
    }
    visit(List(this),List())
  }

  /** step growth */
  def grow(dt:Float){
    age += dt
    // if(depth == 2) println(s"$length $maxLength")
    length += 0.01 * dt
    if(length > maxLength) length = maxLength
    else if(length < minLength) length = minLength
    if(dt > 0 && length > maxLength/4){
      children.foreach(_.grow(dt))
    } else if(dt < 0 && length < maxLength/4) {
      children.foreach(_.grow(dt))
    }
  }

  def applyForce( f: Vec3 ) : Vec3 = {
    accel += f / mass
    children.foreach( (n) => n.applyForce( f ) )
    f
  }

  /** step physics sim */
  def step(dt:Float){
    visitPost { case b => 
      // euler = (pose0.quat.inverse * pose.quat).toEulerVec()
      val w = b.euler - b.lEuler
      val ax = b.accel dot b.pose.quat.toX
      val ay = b.accel dot b.pose.quat.toY
      val az = b.accel dot b.pose.quat.toZ
      var dw = Vec3(-ay, ax, az)
      
      dw -= w * (b.damp / b.mass)
      dw -= b.euler * b.k
      b.lEuler.set(b.euler)
      b.euler = b.euler + w + dw * (.5f*dt*dt)

      b.pose.quat = b.pose0.quat * Quat().fromEuler(b.euler.x,b.euler.y,b.euler.z)

      b.accel.zero
    }
  }

  /** make sure branches stay connected after physics step */
  def solve(){
    visitPre { case b =>
      val pos = b.pose.pos + b.pose.quat.toZ * b.length
      b.children.foreach { case c =>
        c.pose.pos.set(pos)
        c.pose0.quat = b.pose.quat * c.relPose.quat
        c.pose.quat = c.pose0.quat * Quat().fromEuler(c.euler.x,c.euler.y,c.euler.z)
      }
    }
  }
}



/**
  * Tree class for growing and animating tree like structures
  */
class Tree(pose:Pose = Pose(Vec3(),Quat.up)) {

  var trunk = Branch(null,pose)
  // trunk.length = 1f

  def generate(depth:Int)(f:PartialFunction[Branch,Unit]){
    trunk.visitPre {
      case b if b.depth == depth => ()
      case b => f(b)
    }
  }


  // def generate(depth:Int){
  //   trunk.foreach { 
  //     case b if b.depth == depth => ()
  //     case b =>
  //       val pos = b.pose.quat.toZ * b.length
  //       val child = Branch(b, Pose(pos,Quat().fromEuler(0,0,0))) //Random.vec3())))
  //       child.maxLength = b.maxLength * 0.95
  //       child.length = b.length * 0.95
  //       val child2 = Branch(b, Pose(pos,Quat().fromEuler(0,-0.2,0))) //Random.vec3())))
  //       child2.maxLength = b.maxLength * 0.95
  //       child2.length = b.length * 0.95
  //       b.children += child
  //       // b.children += child2
  //   }
  // }

  def lineMesh(mesh:Mesh){
    mesh.primitive = Lines 
    trunk.foreach { case b => 
      b.children.foreach { case c =>
        mesh.vertices += b.pose.pos
        mesh.vertices += c.pose.pos
      }
    }
  }

  def draw(model:Model){
    // trunk.foreach { case b =>
    // trunk.visitPost { case b =>
    trunk.visitPre { case b =>
      model.pose = b.pose
      model.scale.set(b.length*b.thick,b.length*b.thick,b.length) 
      model.draw
    }
  }


  def print(){ trunk.foreach{ case b => println(b.pose.pos) } }

}
