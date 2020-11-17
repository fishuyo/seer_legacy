
package seer
package ui

import actor._
import spatial._
import collection.mutable.ListBuffer
import scala.reflect.ClassTag

object UI {

  def panel(x:Float=0f, y:Float=0f, w:Float=1f, h:Float=1f) = new Panel(x,y,w,h)
  def slider(x:Float=0f, y:Float=0f, w:Float=1f, h:Float=1f) = new Slider(x,y,w,h)
  def xy(x:Float=0f, y:Float=0f, w:Float=1f, h:Float=1f) = new SliderXY(x,y,w,h)
  


}


/**
  * Widget, a pickable shape of some sort, composable, nestable, in order to build up UI components
  */
trait Widget extends Pickable {
  var (x,y,w,h) = (0f,0f,1f,1f)
  
  // var parent:Option[Widget] = None
  override val children = ListBuffer[Widget]()
  def getChildren() = children //.asInstanceOf[ListBuffer[Widget]]

  var movable = false
  var resizable = false
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
    scale.set(w,h,1f)
  }

  def +=(w:Widget){
    getChildren() += w
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
        // println(s"Pick: $this $selected")
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        false
        
      case Drag => 
        if(selected && movable) {
          val xy = hit.ray(selectDist).xy + selectOffset
          pose.pos.set( Vec3(xy, 0f) )
          x = xy.x; y = xy.y
          // println(s"Drag: $this $x")
          // updatePose()
          true
        } else false
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

  def setNormalized(v:T) = {
    value = v
    updatePose()
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
    p.onChange((v:T) => setNormalized(v))
    this
  }
}

trait Rectangle extends Widget


sealed trait Layout 
case object LayoutNone extends Layout
case object LayoutX extends Layout
case object LayoutY extends Layout
case class LayoutGrid(nr:Int=0, nc:Int=0) extends Layout

class Panel(_x:Float, _y:Float, _w:Float, _h:Float) extends Widget with Rectangle {
  var layout:Layout = LayoutNone
  var pad = 0.01f
  setPosition(_x,_y,_w,_h)
  movable = true

  def padding(f:Float) = { pad = f; this }
  def layoutX() = { layout = LayoutX; this }
  def layoutY() = { layout = LayoutY; this }
  def layoutGrid(nr:Int=0, nc:Int=0) = { layout = LayoutGrid(nr,nc); this }

  override def updatePose(): Unit = {
    pose.pos.set(x,y,0)
    scale.set(w,h,1f)
    positionChildren()
  }

  def positionChildren() = {
    val d = 1f / getChildren().size
    layout match {
      case LayoutNone => // do nothing
      case LayoutX =>
        getChildren().zipWithIndex.foreach { case (c,i) =>
          c.x = d*i + pad
          c.y = 0f + pad //y
          c.w = d - 2*pad
          c.h = 1f - 2*pad //h
          c.updatePose()
        }
  
      case LayoutY =>
        getChildren().zipWithIndex.foreach { case (c,i) =>
          c.x = 0f + pad
          c.y = d*i + pad
          c.w = 1f - 2*pad
          c.h = d - 2*pad
          c.updatePose()
        }

      case LayoutGrid(nr,nc) =>
        val n = getChildren().size
        var (rows, cols) = (nr,nc)
        (rows,cols) match {
          case (0,0) =>
            rows = math.round(math.sqrt(n)).toInt
            cols = math.ceil(n/rows).toInt
          case (0,c) => rows = math.ceil(n.toFloat/c).toInt
          case (r,0) => cols = math.ceil(n.toFloat/r).toInt
          case _ =>
        }

        val nw = 1f / cols
        val nh = 1f / rows
        getChildren().zipWithIndex.foreach { case (c,i) =>
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

  // add child widget for slider handle
  this += new Rectangle {
    setPosition(0,0,1,0.1f)
    movable = true
    style = "fill"
    override def onEvent(e:PickEvent, hit:Hit) = {
      var ret = super.onEvent(e,hit)
      e.event match {
        case Drag =>
          x = 0f
          if(y < 0f) y = 0f
          else if(y + h > 1f) y = 1f - h
          setValue( y / (1f - h) )
          ret = selected
        case _ =>
      }
      ret
    }
  }
  
  // TODO handle drag event outside of slider handle
  override def onEvent(e:PickEvent, hit:Hit) = {
    var ret = super.onEvent(e,hit)
    e.event match {
      case Drag =>
      case _ =>
    }
    ret
  }

  override def updatePose() = {
    pose.pos.set(x,y,0)
    scale.set(w,h,1)
    if(!getChildren().isEmpty){
      var tmp = value
      ixform.foreach{ case f => tmp = f(tmp) }
      getChildren().head.y = tmp * (1f-getChildren().head.h)
      getChildren().head.updatePose()
    }
  }

}


class SliderXY(_x:Float, _y:Float, _w:Float, _h:Float) extends WidgetData[Vec2] with Rectangle{ self =>
  setPosition(_x,_y,_w,_h)
  setValue(Vec2())

  // add child widget for slider handle
  this += new Rectangle {
    setPosition(0f,0f,0.1f,0.1f)
    movable = true
    style = "fill"
    override def onEvent(e:PickEvent, hit:Hit) = {
      var ret = super.onEvent(e,hit)
      e.event match {
        case Drag =>
          if(x < 0f) x = 0f
          else if(x + w > 1f) x = 1f - w
          if(y < 0f) y = 0f
          else if(y + h > 1f) y = 1f - h
          setValue( Vec2(x/(1f-w), y/(1f-h)) )
          ret = selected
        case _ =>
      }
      ret
    }
  }
  
  // TODO handle drag event outside of slider handle
  override def onEvent(e:PickEvent, hit:Hit) = {
    var ret = super.onEvent(e,hit)
    e.event match {
      case Drag =>
      case _ =>
    }
    ret
  }

  override def updatePose() = {
    pose.pos.set(x,y,0)
    scale.set(w,h,1)
    if(!getChildren().isEmpty){
      var tmp = value
      ixform.foreach{ case f => tmp = f(tmp) }
      getChildren().head.x = tmp.x * (1f-getChildren().head.w)
      getChildren().head.y = tmp.y * (1f-getChildren().head.h)
      getChildren().head.updatePose()
    }
  }

}