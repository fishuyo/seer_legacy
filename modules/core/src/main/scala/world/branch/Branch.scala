
package com.fishuyo.seer
package world
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

  def apply(parent0:Branch, p:Pose, d:Vec3) = new Branch{
    parent = parent0
    pose.set(p)
    lPose.set(p)
    pose0.set(p)
    if(parent != null){
      relPose.set(parent.pose.inverse * pose)
      depth = parent.depth + 1
      maxLength = parent.maxLength
      length = parent.length
      thick = parent.thick * 0.99f
    }

    dir.set(d)
    dir0.set(d)
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
  var dir0 = Vec3()
  var growCount = 0
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

  def updateConstants(): Unit ={
    mass = 1.0f //+ thick*thick*length * 100f
    k = 10.0f  //+ thick*thick*thick / (length*length*length) * 10f
  }

  /** run a function on branch and all children breadth first */
  def foreach(f:PartialFunction[Branch,Unit]): Unit ={
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
  def visitPre(f:PartialFunction[Branch,Unit]): Unit ={
    var branches = Stack[Branch]()
    branches.push(this)
    while(!branches.isEmpty){
      val b = branches.pop
      f(b)
      branches.pushAll(b.children)
    }
  }
  /** run a function on branch and all children depth first */
  def visitPost(f:PartialFunction[Branch,Unit]): Unit ={
    @tailrec def visit(branches:List[Branch], eval:List[Branch]): Unit ={
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
  def grow(t:Float): Unit ={
    length = t * maxLength
    var tt = 0.001f
    if(t > 0.25f) tt = (t-0.25f)/0.75f
    children.foreach(_.grow(tt))
  }

  def applyForce( f: Vec3 ) : Vec3 = {
    accel += f / mass
    children.foreach( (n) => n.applyForce( f ) )
    f
  }

  /** step physics sim */
  def step(dt:Float): Unit ={
    visitPost { case b => 
      // euler = (pose0.quat.inverse * pose.quat).toEulerVec()
      val w = b.euler - b.lEuler
      val ax = b.accel dot b.pose.quat.toX()
      val ay = b.accel dot b.pose.quat.toY()
      val az = b.accel dot b.pose.quat.toZ()
      var dw = Vec3(-ay, ax, az)
      
      dw -= w * (b.damp / b.mass)
      dw -= b.euler * b.k
      b.lEuler.set(b.euler)
      b.euler = b.euler + w + dw * (.5f*dt*dt)

      b.pose.quat = b.pose0.quat * Quat().fromEuler(b.euler.x,b.euler.y,b.euler.z)

      b.accel.zero()
    }
  }

  /** make sure branches stay connected after physics step */
  def solve(): Unit ={
    visitPre { case b =>
      val pos = b.pose.pos + b.pose.quat.toZ() * b.length
      b.children.foreach { case c =>
        c.pose.pos.set(pos)
        c.pose0.quat = b.pose.quat * c.relPose.quat
        c.pose.quat = c.pose0.quat * Quat().fromEuler(c.euler.x,c.euler.y,c.euler.z)
      }
    }
  }
}


