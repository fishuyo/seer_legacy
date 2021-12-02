


class IFS extends SeerActor {

  val n = 10000
  val mesh = Mesh()
  mesh.primitive = Points
  mesh.maxVertices = n 
  val model = Model(mesh).scale(0.1f)
  model.material.transparent = true
  model.material.color = RGB(0.05f)

  val (a,b,c) = (10.0f, 28.0f, 8.0f/3.0f)
  val f = (v:Vec3, dt:Float) => {
    val x = v.x + dt*(a*v.y - a*v.x)
    val y = v.y + dt*(b*v.x - v.y - v.z*v.x)
    val z = v.z + dt*(v.x*v.y - c*v.z)
    Vec3(x,y,z)
  }

  var points = Array.fill(n)(Random.vec3()*100f)

  override def init(){
    // Camera.nav.pos.set(Vec3(0,0,4))
    // Camera.nav.quat.setIdentity
    // Renderer().clear = true 
    Renderer().clear = false 
  }

  override def animate(dt:Float){
    points = points.map(f(_,0.005f))
    mesh.clear
    mesh.vertices ++= points
    mesh.update
  }

  override def draw(){
    model.draw
    // Sphere().draw
    Renderer().clear = false
  }

  Keyboard.listen {
    case 'g' => Renderer().clear = true; points = Array.fill(n)(Random.vec3()*100f)
  }


}

classOf[IFS]