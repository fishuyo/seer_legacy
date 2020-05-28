
package com.fishuyo.seer
package openni

import graphics.Image
import spatial.Vec3

import collection.mutable.ArrayBuffer
import collection.mutable.ListBuffer

object User{
  // def apply(id:Int) = new User(id)
  // def apply(u:User) = {
  //   val copy = new User(u.id)
  //   copy.tracking = u.tracking
  //   copy.skeleton = Skeleton(u.skeleton)
  //   copy.points = u.points.clone 
  //   copy       
  // }
}

class User(val id:Int) {

  var tracking = false
  val skeleton = new Skeleton
  val points = ArrayBuffer[Vec3]()

  var mask:Option[Image] = None
  // val maskBufferSafe = mask.buffer.duplicate

  def head = skeleton.joints("head")
  def neck = skeleton.joints("neck")
  def torso = skeleton.joints("torso")
  def waist = skeleton.joints("waist")
  def l_collar = skeleton.joints("l_collar")
  def l_shoulder = skeleton.joints("l_shoulder")
  def l_elbow = skeleton.joints("l_elbow")
  def l_wrist = skeleton.joints("l_wrist")
  def l_hand = skeleton.joints("l_hand")
  def l_fingers = skeleton.joints("l_fingers")
  def r_collar = skeleton.joints("r_collar")
  def r_shoulder = skeleton.joints("r_shoulder")
  def r_elbow = skeleton.joints("r_elbow")
  def r_wrist = skeleton.joints("r_wrist")
  def r_hand = skeleton.joints("r_hand")
  def r_fingers = skeleton.joints("r_fingers")
  def l_hip = skeleton.joints("l_hip")
  def l_knee = skeleton.joints("l_knee")
  def l_ankle = skeleton.joints("l_ankle")
  def l_foot = skeleton.joints("l_foot")
  def r_hip = skeleton.joints("r_hip")
  def r_knee = skeleton.joints("r_knee")
  def r_ankle = skeleton.joints("r_ankle")
  def r_foot = skeleton.joints("r_foot")

}