
package com.fishuyo.seer 
package openni

import spatial._
import graphics._
import util._

// import org.openni.SkeletonJoint._
import com.primesense.nite.JointType._

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer


class Skeleton {

  var calibrating = false
  var tracking = false
  var velSmooth = 0.2f

  val joints = HashMap[String,Vec3]()
  val vel = HashMap[String,Vec3]()
  val bones = HashMap[String,Bone]()

  for( j <- Joint.strings){
    joints(j) = Vec3()
    vel(j) = Vec3()
  }
  for( b <- Bone.strings){
    bones(b) = Bone()
  }

  def updateJoint(s:String, pos:Vec3){
    val oldpos = joints(s)
    vel(s) = vel(s).lerp(pos - oldpos, velSmooth)
    joints(s) = pos
  }

  def updateBones(){
    for( b <- Bone.strings){
      val dst = joints(Bone.connections(b)(0))      
      val src = joints(Bone.connections(b)(1))

      bones(b).pos.set(src)
      val dir = dst - src
      bones(b).length = dir.mag()     
      bones(b).quat = Quat().getRotationTo(Vec3(0,0,1), dir.normalized)
    }
  }

  def head = joints("head")
  def neck = joints("neck")
  def torso = joints("torso")
  // def waist = joints("waist")
  // def leftCollar = joints("l_collar")
  def leftShoulder = joints("l_shoulder")
  def leftElbow = joints("l_elbow")
  // def leftWrist = joints("l_wrist")
  def leftHand = joints("l_hand")
  // def leftFingers = joints("l_fingers")
  // def rightCollar = joints("r_collar")
  def rightShoulder = joints("r_shoulder")
  def rightElbow = joints("r_elbow")
  // def rightWrist = joints("r_wrist")
  def rightHand = joints("r_hand")
  // def rightFingers = joints("r_fingers")
  def leftHip = joints("l_hip")
  def leftKnee = joints("l_knee")
  // def leftAnkle = joints("l_ankle")
  def leftFoot = joints("l_foot")
  def rightHip = joints("r_hip")
  def rightKnee = joints("r_knee")
  // def rightAnkle = joints("r_ankle")
  def rightFoot = joints("r_foot")

}

object Joint {
  def apply(s:String) = s match {
    case "head" => HEAD
    case "neck" => NECK
    case "torso" => TORSO
    // case "waist" => WAIST
    // case "l_collar" => LEFT_COLLAR
    case "l_shoulder" => LEFT_SHOULDER
    case "l_elbow" => LEFT_ELBOW
    // case "l_wrist" => LEFT_WRIST
    case "l_hand" => LEFT_HAND
    // case "l_fingers" => LEFT_FINGER_TIP
    // case "r_collar" => RIGHT_COLLAR
    case "r_shoulder" => RIGHT_SHOULDER
    case "r_elbow" => RIGHT_ELBOW
    // case "r_wrist" => RIGHT_WRIST
    case "r_hand" => RIGHT_HAND
    // case "r_fingers" => RIGHT_FINGER_TIP
    case "l_hip" => LEFT_HIP
    case "l_knee" => LEFT_KNEE
    // case "l_ankle" => LEFT_ANKLE
    case "l_foot" => LEFT_FOOT
    case "r_hip" => RIGHT_HIP
    case "r_knee" => RIGHT_KNEE
    // case "r_ankle" => RIGHT_ANKLE
    case "r_foot" => RIGHT_FOOT
    case _ => TORSO
  }

  val joints = List(HEAD,NECK,TORSO,
      LEFT_SHOULDER,LEFT_ELBOW,LEFT_HAND,
      RIGHT_SHOULDER,RIGHT_ELBOW,RIGHT_HAND,
      LEFT_HIP,LEFT_KNEE,LEFT_FOOT,
      RIGHT_HIP,RIGHT_KNEE,RIGHT_FOOT)
  
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
    "neck" -> Seq("head","neck"),
    "clavicle" -> Seq("l_shoulder","r_shoulder"),
    "pelvis" -> Seq("l_hip","r_hip"),
    "spine" -> Seq("neck","torso"),
    "l_humerus" -> Seq("l_elbow","l_shoulder"),
    "l_radius" -> Seq("l_hand", "l_elbow"),
    "l_femur" -> Seq("l_knee", "l_hip"),
    "l_tibia" -> Seq("l_foot", "l_knee"),
    "r_humerus" -> Seq("r_elbow","r_shoulder"),
    "r_radius" -> Seq("r_hand", "r_elbow"),
    "r_femur" -> Seq("r_knee", "r_hip"),
    "r_tibia" -> Seq("r_foot", "r_knee")
    )
}



