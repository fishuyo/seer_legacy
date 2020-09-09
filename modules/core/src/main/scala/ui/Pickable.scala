
package seer
package ui

import spatial._

import collection.mutable.ListBuffer

sealed trait PickEventType
case object Point extends PickEventType
case object Pick extends PickEventType
case object Unpick extends PickEventType
case object Drag extends PickEventType
case object Translate extends PickEventType
case object Rotate extends PickEventType
case object Scale extends PickEventType

case class PickEvent(event:PickEventType, ray:Ray, pose:Option[Pose]=None, deltaPose:Option[Pose]=None )

case class Hit(ray:Ray, t:Option[Float]=None, pickable:Option[Pickable]=None){
  def isDefined = t.isDefined
  def pos:Option[Vec3] = t.map{ case f => ray(f) }
}

trait Pickable {
  val pose = Pose()
  val scale = Vec3(1)
  var hover = false
  var selected = false

  var testChildren = true

  var parent:Option[_ <: Pickable] = None
  val children:ListBuffer[_ <: Pickable] = ListBuffer[Pickable]()

  // def getChildren() = children

  def intersect(r:Ray):Option[Float]

  def onEvent(e:PickEvent, hit:Hit):Boolean = {
    e.event match {
      case Point =>
        hover = hit.isDefined
        hover

      case Pick => 
        selected = hit.isDefined
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        false
        
      case _ => false
    }
  }

  def event(e:PickEvent):Boolean = {
    val t = intersect(e.ray)
    val hit = Hit(e.ray, t)
    var handled = false
    
    // depth first event propagation
    if(testChildren){
      val ray = transformRayLocal(e.ray)
      children.foreach { case c => handled |= c.event(PickEvent(e.event, ray)) }
    }

    // only if not handled by child
    if(!handled) onEvent(e, hit)
    else true
  }

  def transformRayLocal(ray:Ray) = {
    val model = Matrix.translation(pose.pos) * Mat4().fromQuat(pose.quat) * Matrix.scaling(scale)
    val invertable = model.invert
    val m = Mat4()
    invertable match {
      case Some(mat) => m.set(mat)
      case _ => m.set(model)
    }
    // m.set(model)
    val o = m.transform(ray.o, 1)
    val d = m.transform(ray.d, 0)
    Ray(o, d)
  }
}
