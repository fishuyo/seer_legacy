

package com.fishuyo.seer 
package openni

import spatial._
import graphics._
import util._

import org.openni.SkeletonJoint._

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer


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

  var bones = HashMap[String,Bone]()
  for( b <- Bone.strings){
    bones(b) = Bone()
  }

  var velSmooth = 0.2

  def setJoints(s:Skeleton){
    joints = s.joints.clone
    droppedFrames = 0
  }

  def updateJoints() = {
    for( j <- Joint.strings){
      val pos = OpenNI.getJoint(id, j)
      updateJoint(j,pos)
    }
    droppedFrames = 0
  }

  def updateJoint(s:String,pos:Vec3){
    val oldpos = joints(s)
    vel(s) = vel(s).lerp(pos - oldpos, velSmooth)
    joints(s) = pos
  }

  def updateBones(){
    for( b <- Bone.strings){
      val dst = joints(Bone.connections(b)._1)      
      val src = joints(Bone.connections(b)._2)

      bones(b).pos.set(src)
      val dir = dst - src
      bones(b).length = dir.mag()     
      bones(b).quat = Quat().getRotationTo(Vec3(0,0,1), dir.normalized)
    }
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

  val connections = Map(
      "head" -> List("neck"),
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

class Bone( var pos:Vec3, var quat:Quat, var length:Float)
object Bone {
  def apply() = new Bone(Vec3(),Quat(),0f)
  def apply(p:Vec3,q:Quat,l:Float) = new Bone(p,q,l)

  val strings = List("neck","clavicle","pelvis", "spine",
      "l_humerus", "l_radius", "l_femur", "l_tibia",
      "r_humerus", "r_radius", "r_femur", "r_tibia")

  val connections = Map(
    "neck" -> ("head","neck"),
    "clavicle" -> ("l_shoulder","r_shoulder"),
    "pelvis" -> ("l_hip","r_hip"),
    "spine" -> ("neck","torso"),
    "l_humerus" -> ("l_elbow","l_shoulder"),
    "l_radius" -> ("l_hand", "l_elbow"),
    "l_femur" -> ("l_knee", "l_hip"),
    "l_tibia" -> ("l_foot", "l_knee"),
    "r_humerus" -> ("r_elbow","r_shoulder"),
    "r_radius" -> ("r_hand", "r_elbow"),
    "r_femur" -> ("r_knee", "r_hip"),
    "r_tibia" -> ("r_foot", "r_knee")
    )
}



