
import seer.ui._

class Curve {
  val mesh = Mesh()
  val model = Model(mesh)
  mesh.primitive = LineStrip
  
  val cp = Array(Vec3(),Vec3(0.3,0,0),Vec3(0.7,0.7,0),Vec3(1,0.7,0))
  
  def point(t:Float) = {
		val t1 = 1f - t
		cp(0) * t1 * t1 * t1 + cp(1) * 3f * t1 * t1 * t + cp(2) * 3f * t1 * t * t + cp(3) * t * t * t
  }

  def tangent(t:Float) = {
    val t1 = 1f - 1
    (cp(1) - cp(0)) * 3f * t1 * t1 + (cp(2) - cp(1)) * 6f * t1 * t + (cp(3)-cp(2)) * 3f * t * t
  }

  def update() = {
    val n = 100
    mesh.clear
    for(i <- 0 until n){
      val t = i.toFloat / (n-1)
      mesh.vertices += point(t)
    }
    mesh.update
  }

  def draw() = {
    update
    model.draw
    // cp.foreach { case p =>
    //   val s = Sphere().scale(0.01)
    //   s.translate(p)
    //   s.draw
    // }
  }
} 

class IOlet extends Pickable {
  val model = Sphere()
  val size = 0.05f

  var dragging = false
  var dragLocation = Vec3()
  var selectDist = 0f
  val curve = new Curve


  override def intersect(r:Ray) = {
    r.intersectSphere(pose.pos, size)
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
        }
        selected

      case Unpick =>
        if(!hit.isDefined) selected = false
        // dragging = false
        false

      case Drag => 
        // if(selected) pose.pos.set( hit.ray(selectDist) + selectOffset )
        if(selected){
          dragging = true
          dragLocation = hit.ray(selectDist)
        }
        hover = hit.isDefined
        selected

      case _ => false
    }
  }

  def draw() = {
    if(selected) model.material.color = RGB(1,0,1)
    else if(hover) model.material.color = RGB(0,1,1)
    else model.material.color = RGB(1,1,1)      
    model.pose.set(pose)
    model.scale.set(size)
    model.draw
    if(dragging){
      curve.cp(0).set(pose.pos)
      curve.cp(1).set(pose.pos + Vec3(0.3f,0,0))
      curve.cp(2).set(dragLocation + Vec3(-0.3f,0,0))
      curve.cp(3).set(dragLocation)
      curve.model.material.color.set(1,0,1)
      curve.draw
    }
  }
}

class Block extends Pickable {
  val model = Cube()
  val prevPose = Pose()
  var selectDist = 0f
  var selectOffset = Vec3()

  override val children = ListBuffer[IOlet]()

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
    if(selected) model.material.color = RGB(1,0,1)
    else if(hover) model.material.color = RGB(0,1,1)
    else model.material.color = RGB(1,1,1)      
    model.pose.set(pose)
    model.scale.set(scale)
    model.draw
  }

} 


class Blocks extends SeerActor {

  val blocks = (0 to 10).map { case i =>
    val b = new Block()
    b.model.material = Material.specular
    val p = Random.vec3(); p.z = 0f
    b.pose.pos.set(p)
    b.scale.set(Vec3(0.5f,0.15f,0.05f))
    val (i,o) = (new IOlet, new IOlet)
    i.pose.pos.set(-0.25f,0,0)
    o.pose.pos.set(0.25f,0,0)
    b.children += i
    b.children += o
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
      
    }
  }

  override def draw() = {
   blocks.foreach{ case b =>
    b.draw
    MatrixStack.push
    MatrixStack.transform(b.pose)
    b.children.foreach { case io => io.draw }
    MatrixStack.pop
   }
  }


}

classOf[Blocks]