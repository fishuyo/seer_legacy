
package com.fishuyo.seer
package ui

import spatial._

/**
  * Widget represents a graphical UI object that generates some kind of output data stream
  * 2D / 3D?
  */
class Widget(var position:Vec2, var bounds:Vec2) extends Pickable {
  var value:Any = 0f

  pose.pos.set(Vec3(position,0))
  scale.set(Vec3(bounds,1f))
  // var parent:Option[Widget] = None
  override val children = collection.mutable.ListBuffer[Widget]() 
  var containChildren = false
  var movable = true
  var name = ""
  var style = ""
  // var x,y,w,h = 0f  //Vecs?

  def +=(w:Widget){
    children += w
    w.parent = Some(this)
  }

  def intersect(r:Ray) = {
    r.intersectQuad(Vec3(position + bounds/2,0), bounds.x/2, bounds.y/2)
  }

  var selectDist = 0f
  var selectOffset = Vec2()

  override def onEvent(e:PickEvent, hit:Hit):Boolean = {

    e.event match {
      case Point =>
        hover = hit.isDefined
        hover

      case Pick => 
        selected = hit.isDefined
        if(hit.isDefined){
          // prevPose.set(pose)
          selectDist = hit.t.get
          selectOffset = position - hit.pos.get.xy
        } 
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        false
        
      case Drag => 
        if(selected && movable) {
          position.set( hit.ray(selectDist).xy + selectOffset )
          pose.pos.set(Vec3(position,0))
        }
        true
      case _ => false
    }
    
  }

}


class QuadWidget(pos:Vec2, bnds:Vec2) extends Widget(pos,bnds)
class FilledQuadWidget(pos:Vec2, bnds:Vec2) extends Widget(pos,bnds)

class Slider(pos:Vec2, bnds:Vec2 = Vec2(0.33f,1f)) extends QuadWidget(pos,bnds) {
  movable = false
  containChildren = false
  this += new FilledQuadWidget(Vec2(0f,0), Vec2(1f,0.1f)){
    override def onEvent(e:PickEvent, hit:Hit) = {
      var ret = super.onEvent(e,hit)
      e.event match {
        case Drag =>
          position.x = 0f
          if(position.y < 0f) position.y = 0f
          else if(position.y + bounds.y > 1f) position.y = 1f - bounds.y
          value = position.y / (1f - bounds.y)
          println(value)
          ret = selected
        case _ =>
      }
      ret
    }
  }

  def getValue = children.head.value.asInstanceOf[Float]
  def setValue(v:Float) = {
    children.head.value = v
    children.head.position.y = v * (1f-children.head.bounds.y)
  } 
}
