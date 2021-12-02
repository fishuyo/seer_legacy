
class BFly {
  val body = Plane().scale(0.01f)
  body.material.transparent = true
  body.material.color = RGB(0.5f,0.2f,0.1f)

  var state = "soar"
  val pos = Random.vec3()
  val dir = Random.quat()
  val vel = Random.vec3()*0.01f
  val force = Vec3()

  var near:Seq[BFly] = Seq()

  def sense(all:Seq[BFly]) = {
    near = all.filter { case b => (b.pos - pos).mag() < 0.1f } 
  }

  def step(all:Seq[BFly]) = {

    sense(all)

    if(pos.y < 0f) state = "flutter"
    else if(Random.float() < 0.05f) state = Random.oneOf(Seq("flutter","soar","fall"):_*)()

    state match {
      case "flutter" => flutter()
      case "soar" => soar()
      case "fall" => fall()
    }

    vel += Vec3(0f,-0.001,0f)
    vel += force
    pos += vel

    if(pos.mag() > 5f) pos.set(pos.normalize * 5f)
    // pos.set(pos.max(Vec3(-5)))
    // pos.set(pos.min(Vec3(5)))

  }
  def flutter() = { 
    if(!near.isEmpty) {
      val love = Random.oneOf(near:_*)()
      dir.slerpTo(Quat().fromForwardUp(love.pos - pos + Random.vec3()*0.01f, Vec3(0,1,0)), 0.8f)
    }
    else dir.slerpTo(Quat().fromForwardUp(dir.toZ()+Random.vec3(), Vec3(0,1,0)), 0.2f)

    // dir.slerpTo(Random.quat(), 0.2f)
    force.set(dir.toZ() * 0.0001f + Vec3(0,0.00105f,0))
  }
  def soar() = {
    if(!near.isEmpty) {
      val love = Random.oneOf(near:_*)()
      dir.slerpTo(Quat().fromForwardUp(love.pos - pos + Random.vec3()*0.01f, Vec3(0,1,0)), 0.8f)
    }
    force.set(dir.toZ() * 0.0002f + Vec3(0,0.0005f,0))
    if(vel.y < -.01f) state = "flutter"

  }
  def fall() = {
    force.set(Vec3())
    if(vel.y < -.01f) state = "flutter"
  }

  def draw() = {
    MatrixStack.push
    MatrixStack.translate(pos)
    MatrixStack.rotate(dir)
    body.draw
    MatrixStack.pop
  }

}

class Begin extends SeerActor {

  val butterflies = (0 to 1500).map{case i => new BFly}

  Schedule.every(.01f seconds){
    butterflies.foreach{ case b => b.step(butterflies) }
  }

  override def draw() = {
    butterflies.foreach(_.draw)
  }

  Trackpad.listen { case t => 
  
    if(t.size > 0f) butterflies.foreach{ case b => b.state = "flutter"}
  
  }

}

classOf[Begin]