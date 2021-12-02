
import seer.ui._


class Boid {
  val speed = Var("speed", 0.015f)
  val separateDist = Var("sDist", 0.25f)
  val alignDist = Var("aDist", 0.35f)
  val cohesionDist = Var("cDist", 0.35f)
  val tailLength = Var("tailLength", 5.0f)

  val pos = Random.vec3()
  val lpos = Vec3(pos)
  var tail = List[Vec3](pos)
  val vel = Random.vec3()*speed()

  def step(boids:Seq[Boid]) = {
    val newDir = Vec3()
    val avgVel = Vec3()

    val center = Vec3()
    var count = 0

    boids.collect { case b if b != this =>
      val d = pos - b.pos
      val dist = d.mag()
      
      // separate
      if(dist < separateDist()){
        val dir = d.normalize() / dist
        vel.lerpTo(dir * speed(), 0.01f)
      }

      // align
      if(dist < alignDist()){
        vel.lerpTo(b.vel, 0.01f)
      }

      // cohesion accumulate
      if(dist < cohesionDist()){
        center += b.pos
        count += 1
      }
    
    }

    // cohesion average
    if(count > 0){
      center /= count
      val dir = (center - pos).normalize
      vel.lerpTo(dir * speed(), 0.01f)
    }
    
    pos.wrap(Vec3(-3), Vec3(3))
    // vel.set(vel.normalize * speed() + Random.vec3()*0.01f)
    lpos.set(pos)
    tail = Vec3(pos) :: tail
    tail = tail.take(tailLength().toInt)
    pos += vel 
  }
}

class Flock extends SeerActor {

  val mesh = Mesh()
  mesh.primitive = Lines
  mesh.maxVertices = 100000
  val model = Model(mesh)

  val boids = Array.fill(800)(new Boid)
  val b = boids.head

  val panel = UI.panel(0,0,1,1).layoutX()
  panel += UI.slider().range(1f,10f).bind(b.tailLength)
  panel += UI.slider().range(0.0f,0.1f).bind(b.speed)
  panel += UI.slider().range(0.01f,1f).bind(b.separateDist)
  panel += UI.slider().range(0.01f,1f).bind(b.alignDist) 
  panel += UI.slider().range(0.01f,1f).bind(b.cohesionDist)
  panel.positionChildren()

  // val sines = (0 until 20).map { case i => Sine(100f * i + 80f, 0.01f) }
  // var spatializer = AudioSpatializer.stereo()
  // sines.foreach { case s => spatializer.sources += s }  
  // Out += spatializer

  Mouse.listen { case m =>
    m.event match {
      case "move" => rayEvent(Point, m.x, m.y)
      case "down" => rayEvent(Pick, m.x, m.y)
      case "drag" => rayEvent(Drag, m.x, m.y)
      case "up" => rayEvent(Unpick, m.x, m.y)
    }
  }

  Keyboard.listen {
    case '!' => Parameter.save("1", "*")
    case '1' => Parameter.load("1", "*")
    case '@' => Parameter.save("2", "*")
    case '2' => Parameter.load("2", "*")
    case '#' => Parameter.save("3", "*")
    case '3' => Parameter.load("3", "*")
    case '$' => Parameter.save("4", "*")
    case '4' => Parameter.load("4", "*")
  }

  def rayEvent(evt:PickEventType, x:Float, y:Float) = {
    val ray = Camera.ray(x * Window.width, (1f - y) * Window.height)
    val e = PickEvent(evt, ray)
    panel.event(e)
  }

  override def animate(dt:Float) = {
    boids.foreach(_.step(boids))

    // val clusters = boids.map{ case b1 => 
    //   val near = boids.collect{ case b2 if b1 != b2 && (b1.pos-b2.pos).mag() < 0.5f => b2 }
    //   (b1,near)
    // }.sortBy{ case (b1,near) => -near.length }

    // clusters.take(20).zipWithIndex.foreach { case ((b,near),i) => sines(i).location.set(b.pos); }


    // spatializer.listener.set(Camera.nav)

  }
  override def draw() = {
    mesh.clear()
    mesh.vertices ++= boids.flatMap{ case b => 
      val ps = b.pos :: b.tail
      ps.sliding(2,1).toList.filter { case List(a,b) => (a - b).mag() < 3f }.flatten
    }
    mesh.update()
    model.draw()

    // UIRenderer.draw(panel)
    WidgetRenderer.draw(panel)
  }
}

classOf[Flock]