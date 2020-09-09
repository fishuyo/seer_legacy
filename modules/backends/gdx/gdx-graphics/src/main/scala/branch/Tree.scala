
package seer
package world
package branch

import graphics._
import spatial._

import collection.mutable.ListBuffer
import collection.mutable.Stack
import scala.annotation.tailrec

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

  def clear() = trunk.children.clear

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
