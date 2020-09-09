
package seer 
package ui

import spatial._


class BoundingBoxPickable(min:Vec3, max:Vec3) extends Pickable {
  val prevPose = Pose()
  var selectDist = 0f
  var selectOffset = Vec3()
  val bb = AABB(min,max)

  override def intersect(r:Ray) = {
    val ray = transformRayLocal(r)
    ray.intersectBox(bb.center, bb.dim)
  }

  override def onEvent(e:PickEvent, hit:Hit):Boolean = {

    e.event match {
      case Point =>
        hover = hit.isDefined
        hover

      case Pick => 
      selected = hit.isDefined
       if(hit.isDefined){
          prevPose.set(pose)
          selectDist = hit.t.get
          selectOffset = pose.pos - hit.pos.get
        }
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        false

      case Drag => 
        if(selected) pose.pos.set( hit.ray(selectDist) + selectOffset )
        selected

      case _ => false
    }
  }


}