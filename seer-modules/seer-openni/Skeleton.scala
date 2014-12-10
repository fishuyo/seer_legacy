

package com.fishuyo.seer 
package openni

import spatial._
import graphics._
import util._

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer


object Bone {
  def apply() = new Bone(Vec3(),Quat(),0f)
  def apply(p:Vec3,q:Quat,l:Float) = new Bone(p,q,l)
}
class Bone( var pos:Vec3, var quat:Quat, var length:Float)


class Skeleton(val id:Int) {

  var calibrating = false
  var tracking = false
  var droppedFrames = 0

  var joints = HashMap[String,Vec3]()
  var vel = HashMap[String,Vec3]()

  var bones = ListBuffer[Bone]()
  for( i <- (0 until 8)) bones += Bone()


  def setJoints(s:Skeleton){
    joints = s.joints.clone
    droppedFrames = 0
  }

  def updateJoints() = OpenNI.getJoints(id)


  def updateJoint(s:String,pos:Vec3){
    val oldpos = joints.getOrElseUpdate(s,pos)
    vel(s) = pos - oldpos
    joints(s) = pos
    droppedFrames = 0
  }

  def updateBones(){
    bones(0).pos.set(joints("l_shoulder"))
    var a = joints("l_elbow") - joints("l_shoulder")
    bones(0).length = a.mag()
    bones(0).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(1).pos.set(joints("l_elbow"))
    a = joints("l_hand") - joints("l_elbow")
    bones(1).length = a.mag()
    bones(1).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(2).pos.set(joints("r_shoulder"))
    a = joints("r_elbow") - joints("r_shoulder")
    bones(2).length = a.mag()
    bones(2).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(3).pos.set(joints("r_elbow"))
    a = joints("r_hand") - joints("r_elbow")
    bones(3).length = a.mag()
    bones(3).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(4).pos.set(joints("l_hip"))
    a = joints("l_knee") - joints("l_hip")
    bones(4).length = a.mag()
    bones(4).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(5).pos.set(joints("l_knee"))
    a = joints("l_foot") - joints("l_knee")
    bones(5).length = a.mag()
    bones(5).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(6).pos.set(joints("r_hip"))
    a = joints("r_knee") - joints("r_hip")
    bones(6).length = a.mag()
    bones(6).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(7).pos.set(joints("r_knee"))
    a = joints("r_foot") - joints("r_knee")
    bones(7).length = a.mag()
    bones(7).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)
  }

}


