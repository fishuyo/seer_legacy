
package seer
package ui

import actor._
import spatial._
import collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.collection.mutable

object UI {

  def panel(x:Float=0f, y:Float=0f, w:Float=1f, h:Float=1f) = new Panel(x,y,w,h)
  def slider(x:Float=0f, y:Float=0f, w:Float=1f, h:Float=1f) = new Slider(x,y,w,h)
  


}


/**
  * Widget, a pickable shape of some sort, composable, nestable, in order to build up UI components
  */
trait Widget extends Pickable {
  var (x,y,w,h) = (0f,0f,0f,0f)
  
  // var parent:Option[Widget] = None
  override val children = ListBuffer[Widget]()
  // override def getChildren() = children

  var movable = true
  var containChildren = false
  var name = ""
  var style = ""

  var selectDist = 0f
  var selectOffset = Vec2()

  def setPosition(_x:Float, _y:Float, _w:Float, _h:Float) = {
    x = _x; y = _y; w = _w; h = _h;
    updatePose()
  }

  def updatePose() = {
    pose.pos.set(x,y,0)
    scale.set(w,h,0)
  }

  def +=(w:Widget){
    children += w
    w.parent = Some(this)
  }

  def intersect(r:Ray) = {
    r.intersectQuad(Vec3(x+w/2, y+h/2, 0), w/2, h/2)
  }

  override def onEvent(e:PickEvent, hit:Hit):Boolean = {
    e.event match {
      case Point =>
        hover = hit.isDefined
        hover

      case Pick => 
        selected = hit.isDefined
        if(hit.isDefined){
          selectDist = hit.t.get
          selectOffset = pose.pos.xy - hit.pos.get.xy
        } 
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        false
        
      case Drag => 
        if(selected && movable) {
          val xy = hit.ray(selectDist).xy + selectOffset
          pose.pos.set( Vec3(xy, 0f) )
          x = xy.x; y = xy.y
        }
        true
      case _ => false
    }
    
  }
}

class WidgetData[T: ClassTag] extends Widget {
  var value:T = _
  var param:Option[Parameter[T]] = None
  var xform:Option[T=>T] = None // transformation function
  var ixform:Option[T=>T] = None // inverse xform function

  def setValue(v:T) = {
    var tmp = v
    xform.foreach{ case f => tmp = f(tmp)}
    value = tmp
    updatePose()
    param.foreach{ _.set(tmp) }
  }

  def range(mn:T, mx:T)(implicit n: Fractional[T]) = {
    import n._
    xform = Some((v:T) => v  * (mx - mn) + mn )
    ixform = Some((v:T) => (v - mn)  / (mx - mn))
    this
  }

  def bind(p:Parameter[T]) = {
    param = Some(p)
    // change slider on param change
    p.onChange((v:T) => value = v)
    this
  }
}

trait Rectangle extends Widget


sealed trait Layout 
case object LayoutX extends Layout
case object LayoutY extends Layout
case class LayoutGrid(nr:Int=0, nc:Int=0) extends Layout

class Panel(_x:Float, _y:Float, _w:Float, _h:Float) extends Widget {
  var layout:Layout = LayoutY
  var pad = 0.01f
  setPosition(_x,_y,_w,_h)

  def padding(f:Float) = { pad = f; this }
  def layoutX() = { layout = LayoutX; this }
  def layoutY() = { layout = LayoutY; this }
  def layoutGrid(nr:Int=0, nc:Int=0) = { layout = LayoutGrid(nr,nc); this }

  override def updatePose(): Unit = {
    pose.pos.set(x,y,0)
    scale.set(w,h,0)
    positionChildren()
  }

  def positionChildren() = {
    val d = 1f / children.size
    layout match {
      case LayoutX =>
        children.zipWithIndex.foreach { case (c,i) =>
          c.x = d*i + pad
          c.y = 0f + pad //y
          c.w = d - 2*pad
          c.h = 1f - 2*pad //h
          c.updatePose()
        }
  
      case LayoutY =>
        children.zipWithIndex.foreach { case (c,i) =>
          c.x = 0f + pad
          c.y = d*i + pad
          c.w = 1f - 2*pad
          c.h = d - 2*pad
          c.updatePose()
        }

      case LayoutGrid(nr,nc) =>
        var (rows, cols) = (nr,nc)
        if(rows == 0 && cols == 0){ 
          rows = math.round(math.sqrt(children.size)).toInt
          cols = math.ceil(children.size/rows).toInt
        }
        val nw = 1f / cols
        val nh = 1f / rows
        children.zipWithIndex.foreach { case (c,i) =>
          val ix = i % cols
          val iy = i / cols
          c.x = nw*ix + pad
          c.y = nh*iy + pad
          c.w = nw - 2*pad
          c.h = nh - 2*pad
          c.updatePose()
        }
    }
  }

}

class Slider(_x:Float, _y:Float, _w:Float, _h:Float) extends WidgetData[Float] with Rectangle{ self =>
  setPosition(_x,_y,_w,_h)

  movable = false
  this += new Rectangle {
    setPosition(0,0,1,0.1f)
    style = "fill"
    override def onEvent(e:PickEvent, hit:Hit) = {
      var ret = super.onEvent(e,hit)
      e.event match {
        case Drag =>
          x = 0f
          if(y < 0f) y = 0f
          else if(y + h > 1f) y = 1f - h
          setValue( y / (1f - h) )
          // updatePose()
          // self->updatePose()
          // println(value)
          ret = selected
        case _ =>
      }
      ret
    }
  }

  override def updatePose() = {
    pose.pos.set(x,y,0)
    scale.set(w,h,1)
    if(!children.isEmpty){
      var tmp = value
      ixform.foreach{ case f => tmp = f(tmp) }
      children.head.y = tmp * (1f-children.head.h)
      children.head.updatePose()
    }
  }

}

