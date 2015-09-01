

package com.fishuyo.seer 
package openni

import spatial._
import graphics._
import util._

import org.openni.SkeletonJoint._


import scala.collection.mutable.HashMap
// import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer


object Bone {
  def apply() = new Bone(Vec3(),Quat(),0f)
  def apply(p:Vec3,q:Quat,l:Float) = new Bone(p,q,l)
}
class Bone( var pos:Vec3, var quat:Quat, var length:Float)


object Skeleton{
  def apply(id:Int) = new Skeleton(id)

  def apply(s:Skeleton) = {
    val copy = new Skeleton(s.id)
    copy.calibrating = s.calibrating
    copy.tracking = s.tracking
    copy.droppedFrames = s.droppedFrames
    copy.joints = s.joints.clone
    copy.vel = s.vel.clone
    copy.bones = s.bones.clone
    copy
  }
}

class Skeleton(val id:Int) {

  var calibrating = false
  var tracking = false
  var droppedFrames = 0

  var joints = HashMap[String,Vec3]()
  var vel = HashMap[String,Vec3]()
  for( j <- Joint.strings){
    joints(j) = Vec3()
    vel(j) = Vec3()
  }

  var bones = ArrayBuffer[Bone]()
  for( i <- (0 until 8)) bones += Bone()


  def setJoints(s:Skeleton){
    joints = s.joints.clone
    droppedFrames = 0
  }

  def updateJoints() = OpenNI.getJoints(id)


  def updateJoint(s:String,pos:Vec3){
    val oldpos = joints.getOrElseUpdate(s,pos)
    vel(s) = vel(s).lerp(pos - oldpos, 0.2)
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

object Joint {
  def apply(s:String) = s match {
    case "head" => HEAD
    case "neck" => NECK
    case "torso" => TORSO
    case "waist" => WAIST
    case "l_collar" => LEFT_COLLAR
    case "l_shoulder" => LEFT_SHOULDER
    case "l_elbow" => LEFT_ELBOW
    case "l_wrist" => LEFT_WRIST
    case "l_hand" => LEFT_HAND
    case "l_fingers" => LEFT_FINGER_TIP
    case "r_collar" => RIGHT_COLLAR
    case "r_shoulder" => RIGHT_SHOULDER
    case "r_elbow" => RIGHT_ELBOW
    case "r_wrist" => RIGHT_WRIST
    case "r_hand" => RIGHT_HAND
    case "r_fingers" => RIGHT_FINGER_TIP
    case "l_hip" => LEFT_HIP
    case "l_knee" => LEFT_KNEE
    case "l_ankle" => LEFT_ANKLE
    case "l_foot" => LEFT_FOOT
    case "r_hip" => RIGHT_HIP
    case "r_knee" => RIGHT_KNEE
    case "r_ankle" => RIGHT_ANKLE
    case "r_foot" => RIGHT_FOOT
    case _ => TORSO
  }

  val strings = List("head","neck","torso",
      "l_shoulder","l_elbow","l_hand",
      "r_shoulder","r_elbow","r_hand",
      "l_hip","l_knee","l_foot",
      "r_hip","r_knee","r_foot")

  val connections = Map("head" -> List("neck"),
                        "neck" -> List("head","l_shoulder","r_shoulder","torso"),
                        "torso" -> List("neck","l_shoulder","r_shoulder","l_hip","r_hip"),
                        "l_shoulder" -> List("neck","l_elbow","r_shoulder","torso","l_hip"),
                        "r_shoulder" -> List("neck","r_elbow","l_shoulder","torso","r_hip"),
                        "l_elbow" -> List("l_shoulder","l_hand"),
                        "r_elbow" -> List("r_shoulder","r_hand"),
                        "l_hand" -> List("l_elbow"),
                        "r_hand" -> List("r_elbow"),
                        "l_hip" -> List("l_knee","l_shoulder","torso","r_hip"),
                        "r_hip" -> List("r_knee","r_shoulder","torso","l_hip"),
                        "l_knee" -> List("l_hip","l_foot"),
                        "r_knee" -> List("r_hip","r_foot"),
                        "l_foot" -> List("l_knee"),
                        "r_foot" -> List("r_knee")
                        )
}


