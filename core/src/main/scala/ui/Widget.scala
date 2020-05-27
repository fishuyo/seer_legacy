
package com.fishuyo.seer
package ui

import spatial._
import collection.mutable.ListBuffer

/**
  * Widget represents a graphical UI object that generates some kind of output data stream
  * 2D / 3D?
  * Pickable?
  */
class Widget(var position:Vec2, var bounds:Vec2) extends Pickable {
  var value:Any = 0f

  pose.pos.set(Vec3(position,0))
  scale.set(Vec3(bounds,1f))
  // var parent:Option[Widget] = None
  val wchildren = ListBuffer[Widget]() 

  var movable = true
  var name = ""
  var style = ""
  // var x,y,w,h = 0f  //Vecs?

  def +=(w:Widget): Unit ={
    wchildren += w
    w.parent = Some(this)
  }

  def intersect(r:Ray) = {
    r.intersectQuad(Vec3(position + bounds/2,0), bounds.x/2, bounds.y/2)
  }

  var selectDist = 0f
  var selectOffset = Vec2()

  override def point(hit:Hit, childs:Seq[Boolean]) = {
    if(hit.isDefined) hover = true
    else hover = false
    hover
  }
  override def pick(hit:Hit, childs:Seq[Boolean]):Boolean = {
    if(childs.contains(true)) return false
    if(hit.isDefined){
      // prevPose.set(pose)
      selectDist = hit.dist
      selectOffset = position - hit.pos.xy
      selected = true
    } else selected = false
    selected
  }
  override def drag(hit:Hit, childs:Seq[Boolean]):Boolean = {
    if(childs.contains(true)) return false
    if(selected && movable) {
      position.set( hit.ray(selectDist).xy + selectOffset )
      pose.pos.set(Vec3(position,0))
    }
    true
  }
  override def unpick(hit:Hit, childs:Seq[Boolean]) = {
    selected = false
    false
  }

  // def update(){}
  // def hitTest(){}  // ray? 2d/3d
  // def pickEvent(e:PickEvent){}

}


class QuadWidget(pos:Vec2, bnds:Vec2) extends Widget(pos,bnds)
class FilledQuadWidget(pos:Vec2, bnds:Vec2) extends Widget(pos,bnds)

class Slider(pos:Vec2, bnds:Vec2 = Vec2(0.33f,1f)) extends QuadWidget(pos,bnds) {
  movable = false
  containChildren = false
  this += new FilledQuadWidget(Vec2(0f,0), Vec2(1f,0.1f)){
    override def drag(hit:Hit, childs:Seq[Boolean]) = {
      super.drag(hit,childs)
      position.x = 0f
      if(position.y < 0f) position.y = 0f
      else if(position.y + bounds.y > 1f) position.y = 1f - bounds.y
      value = position.y / (1f - bounds.y)
      println(value)
      selected
    }
  }

  def getValue = wchildren.head.asInstanceOf[Widget].value.asInstanceOf[Float]
  def setValue(v:Float) = {
    val w = wchildren.head.asInstanceOf[Widget]
    w.value = v
    w.position.y = v * (1f-w.bounds.y)
  } 
}
