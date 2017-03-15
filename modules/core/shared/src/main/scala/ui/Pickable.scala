
package com.fishuyo.seer
package ui

import spatial._

import collection.mutable.ListBuffer

abstract trait PickEventType
case object Point extends PickEventType
case object Pick extends PickEventType
case object Drag extends PickEventType
case object Unpick extends PickEventType

case class PickEvent(val event:PickEventType, val ray:Ray)

case class Hit(val ray:Ray, val t:Option[Float]){
  def isDefined = t.isDefined
  def dist = t.get
  def pos = ray(t.get)
}

object Pickable { 
  def apply() = new Pickable
}

class Pickable(){ //val model:Model) {
  val pose = Pose()
  val scale = Vec3(1)

  val prevPose = Pose()
  var hover = false
  var selected = false
  var selectDist = 0f
  var selectOffset = Vec3()

  var parent:Option[Pickable] = None
  val children = ListBuffer[Pickable]()

  def intersect(r:Ray) = {
    r.intersectSphere(pose.pos, scale.x )
  }

  def pickEvent(e:PickEvent){
    val hit = Hit(e.ray, intersect(e.ray))
      e.event match {
        case Point => point(hit)
        case Pick => pick(hit)
        case Drag => drag(hit)
        case Unpick => unpick(hit)
      }
  }

  def point(hit:Hit){
    if(hit.isDefined) hover = true
    else hover = false
  }
  def pick(hit:Hit){
    if(hit.isDefined){
      prevPose.set(pose)
      selectDist = hit.dist
      selectOffset = pose.pos - hit.pos
      selected = true
    } else selected = false
  }
  def drag(hit:Hit){
    if(selected) pose.pos.set( hit.ray(selectDist) + selectOffset )
  }
  def unpick(hit:Hit){
    if(!hover) selected = false
  }
}