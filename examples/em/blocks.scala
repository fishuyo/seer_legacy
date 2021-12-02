
import seer.ui._

class Block extends Pickable {
  val model = Cube()
  val prevPose = Pose()
  var selectDist = 0f
  var selectOffset = Vec3()
  // val bb = AABB(min,max)

  override def intersect(r:Ray) = {
    // val ray = transformRayLocal(r)
    // ray.intersectBox(bb.center, bb.dim)
    r.intersectBox(pose.pos, scale)
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

  def draw() = {
    model.draw
  }

} 


class Blocks extends SeerActor {

  val blocks = (0 to 10).map { case i =>
    val b = new Block()
    b.model.material = Material.specular
    b.pose.pos.set( Random.vec3() )
    b.scale.set(Vec3(1f,0.35f,0.35f))
    b
  }

  override def animate(dt:Float){
    implicit def f2i(f:Float) = f.toInt
    val ray = Camera.ray(Mouse.x.now*Window.width, (1f - Mouse.y.now)*Window.height)
    val e = Mouse.status.now match {
      case "move" => Point
      case "down" => Pick
      case "drag" => Drag
      case "up" => Unpick
    }
    blocks.foreach { case b =>
      b.event(PickEvent(e,ray))
      val m = b.model
      if(b.selected) m.material.color = RGB(1,0,1)
      else if(b.hover) m.material.color = RGB(0,1,1)
      else m.material.color = RGB(1,1,1)
      m.pose.set(b.pose)
      m.scale.set(b.scale)
    }
  }

  override def draw() = {
   blocks.foreach(_.draw)
  }


}

classOf[Blocks]