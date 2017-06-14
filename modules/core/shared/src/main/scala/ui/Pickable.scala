
package com.fishuyo.seer
package ui

import spatial._

import collection.mutable.ListBuffer

sealed trait PickEventType
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

trait Pickable {
  val pose = Pose()
  val scale = Vec3(1)
  var hover = false
  var selected = false

  var containChildren = true

  var parent:Option[_ <: Pickable] = None
  val children:ListBuffer[_ <:Pickable] = ListBuffer[Pickable]()

  def intersect(r:Ray):Option[Float]

  def point(hit:Hit, child:Seq[Boolean]):Boolean = false
  def pick(hit:Hit, child:Seq[Boolean]):Boolean = false
  def drag(hit:Hit, child:Seq[Boolean]):Boolean = false
  def unpick(hit:Hit, child:Seq[Boolean]):Boolean = false

  def pickEvent(e:PickEvent):Boolean = {
    val hit = Hit(e.ray, intersect(e.ray))
    var childs = Seq[Boolean]()
    
    // depth first event propogation, so we can know to ignore events handled by children
    if(hit.t.isDefined || !containChildren)
      childs = children.map { _.pickEvent(PickEvent(e.event, transformRayLocal(e.ray))) }

    // XXX events probably need to be handled differently, ie unpick should probably always propogate?
    e.event match {
      case Point => point(hit, childs)
      case Pick => pick(hit, childs)
      case Drag => drag(hit, childs)
      case Unpick => unpick(hit, childs)
    }
  }

  def transformRayLocal(r:Ray) = {
    r
  }
}


// object Pickable { 
//   def apply() = new Pickable
// }

class PickableExample extends Pickable { //val model:Model) {
  // val pose = Pose()
  // val scale = Vec3(1)

  val prevPose = Pose()
  var selectDist = 0f
  var selectOffset = Vec3()

  // var parent:Option[Pickable] = None
  // val children = ListBuffer[Pickable]()

  override def intersect(r:Ray) = {
    r.intersectSphere(pose.pos, scale.x )
  }

  override def point(hit:Hit, childs:Seq[Boolean]) = {
    if(hit.isDefined) hover = true
    else hover = false
    hover
  }
  override def pick(hit:Hit, childs:Seq[Boolean]) = {
    if(hit.isDefined){
      prevPose.set(pose)
      selectDist = hit.dist
      selectOffset = pose.pos - hit.pos
      selected = true
    } else selected = false
    selected
  }
  override def drag(hit:Hit, childs:Seq[Boolean]) = {
    if(selected) pose.pos.set( hit.ray(selectDist) + selectOffset )
    selected
  }
  override def unpick(hit:Hit, childs:Seq[Boolean]) = {
    if(!hover) selected = false
    false
  }

}