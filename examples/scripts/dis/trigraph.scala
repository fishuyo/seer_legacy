
class Trigraph extends SeerActor {

  var font:Font = _

  val mesh = Mesh()
  mesh.maxVertices = 10000
  val model = Model(mesh)

  val mesh2 = Mesh()
  mesh2.maxVertices = 10000
  val model2 = Model(mesh2)

  val w = (2*Pi/3).toFloat

  val T = Vec3(0,1,0).normalize
  val E = Quat().fromEuler(0,0,w).rotate(T).normalize
  val W = Quat().fromEuler(0,0,-w).rotate(T).normalize

  val colorT = rgb(45, 185, 82)
  val colorE = rgb(62, 136, 234)
  val colorW = rgb(235, 76, 76)

  line(Vec3(), T, 0.005f, colorT)(mesh)
  line(Vec3(), E, 0.005f, colorE)(mesh)
  line(Vec3(), W, 0.005f, colorW)(mesh)
  line(T, E, 0.002f)(mesh)
  line(T, W, 0.002f)(mesh)
  line(E, W, 0.002f)(mesh)

  // circle(Vec3(), 1f, 0.002f)(mesh)

  case class Work(name:String, p:Vec3, d:Float=1f)
  val works = ListBuffer[Work]()
  works += Work("kodama", Vec3(0.5f,0.6f,0.5f), 0.8f)
  works += Work("kinetrope", Vec3(1f,1f,0.2f), 0.85f) 
  works += Work("becoming light", Vec3(1f,0.2f,1f), 1.2f)
  works += Work("meditation mirror", Vec3(0.2f,1f,1f), 1.3f) // move more towards E?
  // works += Work("moon circle", Vec3(0.3f,0.9f,1f))
  works += Work("terrarium", Vec3(0.5f,0.4f,0.5f), 1.6f)
  // works += Work("embodiment lab", Vec3(0.1f,1f,0.1f))
  // works += Work("looper", Vec3(1f,0.1f,0.1f))
  // works += Work("meeting place", Vec3(0f,1f,1f))

  def graph(p:Vec3) = {
    val q = p / (p.x+p.y+p.z) // manhattan normalize
    (T * q.x + E * q.y + W * q.z)
  }

  def line(v1:Vec3, v2:Vec3, w:Float, c:RGBA=RGBA(0.3f,0.3f,0.3f,1))(m:Mesh) = {
    val d = (v2 - v1).normalize
    val n = d cross Vec3(0,0,1)
    m.vertices += v1 - n*w - v1*w/2
    m.vertices += v1 + n*w - v1*w/2
    m.vertices += v2 - n*w - v2*w/2
    m.vertices += v1 + n*w - v1*w/2
    m.vertices += v2 + n*w - v2*w/2
    m.vertices += v2 - n*w - v2*w/2
    m.colors ++= (0 until 6).map{ case i => c}
  }

  def circle(c:Vec3, r:Float, w:Float, n:Int=360)(m:Mesh) = {
    var prev = c + Vec3(r,0,0)
    for(i <- 1 to n){
      val ang = (2*Pi * i / n.toFloat).toFloat
      val x = r * math.cos(ang)
      val y = r * math.sin(ang)
      val p = Vec3(x,y,0)
      line(prev, p, w)(m)
      prev.set(p)
    }
  }

  override def init() = {
    Camera.nav.quat.setIdentity()
    Shader.load("lo", "shaders/lo").monitor()
    font = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New Bold.ttf")
    // font = new Font("/Library/Fonts/Arial Unicode.ttf")
    Renderer().environment.backgroundColor.set(1,1,1,0)

  }

  var capture = false
  var count = 0
  override def animate(dt:Float) = {
    if(capture){
      val image = Renderer().capture()
      image.save(s"out$count.png", false)
      count += 1
      capture = false
    }
  }

  val dot = Circle().scale(0.02f)
  dot.material.transparent = true
  dot.material.color = RGBA(0,0,0,1)

  override def draw() = {

    shader("lo")
    model.draw

    font.size(0.85f)
    Text.begin()
    font.color.set(colorT*0.35f)
    Text.draw(font, "technology", T.xy*1.1f, 0f, Text.Center)
    Text.end()

    Text.begin()
    font.color.set(colorE*0.35f)
    Text.draw(font, "embodiment", E.xy*1.1f, 0f, Text.Center)
    Text.end()

    Text.begin()
    font.color.set(colorW*0.35f)
    Text.draw(font, "worldmaking", W.xy*1.1f, 0f, Text.Center)
    Text.end()



    works.foreach { 
      case Work(name, p, d) => 
        val v = graph(p)
        MatrixStack.push
        MatrixStack.translate(v)
        MatrixStack.scale(d)
        shader("basic")
        dot.material.color.set(p.normalized.zxy * 1f)
        dot.draw()
        MatrixStack.pop
    }

    font.size(0.55f)
    font.color.set(RGBA(0,0,0,1))
    Text.begin()
    works.foreach { 
      case Work(name, p, d) => 
        val v = graph(p)
        Text.draw(font, name, v.xy + Vec2(0,.085f), 0f, Text.Center)
    }
    Text.end()

  }

  Keyboard.listen {
    case 'j' => println("capture go."); capture = true;
    case _ =>
  }


  def rainbowTriangles(){
    mesh.vertices += Vec3()
    mesh.vertices += T
    mesh.vertices += (T+E)/2
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(0,1,0,1)
    mesh.colors += RGBA(0,1,1,1)
    mesh.vertices += Vec3()
    mesh.vertices += (T+E)/2
    mesh.vertices += E
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(0,1,1,1)
    mesh.colors += RGBA(0,0,1,1)

    mesh.vertices += Vec3()
    mesh.vertices += W
    mesh.vertices += (T+W)/2
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(1,0,0,1)
    mesh.colors += RGBA(1,1,0,1)
    mesh.vertices += Vec3()
    mesh.vertices += (T+W)/2
    mesh.vertices += T
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(1,1,0,1)
    mesh.colors += RGBA(0,1,0,1)

    mesh.vertices += Vec3()
    mesh.vertices += E
    mesh.vertices += (E+W)/2
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(0,0,1,1)
    mesh.colors += RGBA(1,0,1,1)
    mesh.vertices += Vec3()
    mesh.vertices += (E+W)/2
    mesh.vertices += W
    mesh.colors += RGBA(1,1,1,1)
    mesh.colors += RGBA(1,0,1,1)
    mesh.colors += RGBA(1,0,0,1)
  }

}

classOf[Trigraph]