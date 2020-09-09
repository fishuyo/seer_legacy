
package seer
package ui

import graphics._
import spatial._

object WidgetRenderer {

  val mat = Material.basic
  val q = Mesh()
  q.primitive = LineStrip
  q.vertices += Vec3(0,0,0)
  q.vertices += Vec3(1,0,0)
  q.vertices += Vec3(1,1,0)
  q.vertices += Vec3(0,1,0)
  q.vertices += Vec3(0,0,0)
  val quad = Model(q)
  quad.material = mat

  val qf = Mesh()
  qf.primitive = Triangles
  qf.vertices += Vec3(0,0,0)
  qf.vertices += Vec3(1,0,0)
  qf.vertices += Vec3(1,1,0)
  qf.vertices += Vec3(1,1,0)
  qf.vertices += Vec3(0,1,0)
  qf.vertices += Vec3(0,0,0)
  val quadFilled = Model(qf)
  quadFilled.material = mat


  def draw(w:Widget){
    MatrixStack.push
    MatrixStack.translate(w.x,w.y,0f)
    MatrixStack.scale(w.w,w.h,1)
    w match {
      case q:Rectangle =>
        if(q.hover) mat.color.set(1,0,0,1)
        else mat.color.set(1,1,1,1)
        if(w.style.startsWith("fill")) quadFilled.draw
        else quad.draw
      // case q:FilledQuadWidget => 
      //   if(q.hover) quadFilled.material.color.set(1,0,0,1)
      //   else quadFilled.material.color.set(1,1,1,1)
      //   quadFilled.draw //translate(Vec3(w.position,0)).scale(Vec3(w.bounds,1)).draw
      case _ =>
    }
    w.children.foreach(draw(_))
    MatrixStack.pop
  }


}