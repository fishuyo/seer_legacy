
class Lo extends SeerActor {

  val mesh = Mesh()
  mesh.maxVertices = 10000
  val model = Model(mesh)

  gen()


  def gen() = {

    val n = 75
    
    def tri(i:Float, pin:Vec3) = {
      val rand = 0.002f
      val vrand = 0.001f
      val width = 0.008f //*1.5f
      val length = 0.02f //*1.5f
      val offset = 1.02f
      val innerA = rgb(100, 43, 43)
      val innerB = rgb(151, 38, 38)
      val outerA = rgb(38, 100, 151)
      val outerB = rgb(59, 101, 135)

      val r = Random.vec3.map{ case v => v *= rand; v.z = 0f; v }
      val vr = Random.vec3.map{ case v => v *= vrand; v.z = 0f; v }
      val d = pin.normalized
      val t = d.cross(Vec3(0,0,1))
      val p = pin + r()
      // in
      mesh.vertices += p + t*width + vr()
      mesh.vertices += p - t*width + vr()
      mesh.vertices += p - d*length + vr()
      val c0 = innerA * (1f - i) + innerB * i
      val c1 = c0
      mesh.colors += c0
      mesh.colors += c1
      mesh.colors += (c0 + c1) / 2

      // out
      mesh.vertices += p*offset + t*width + vr()
      mesh.vertices += p*offset - t*width + vr()
      mesh.vertices += p*offset + d*length + vr()
      val c2 = outerA * (1f - i) + outerB * i
      val c3 = c2
      mesh.colors += c2
      mesh.colors += c3
      mesh.colors += (c2 + c3) / 2

    }

    for(ring <- 1 to 1){
      
      val count = (n * (1+ring*0.2f)).toInt
      val r = 0.5f * (1+ring*0.2f)

      for(i <- 0 until count){
        val w = i.toFloat/count * 2.0 * Pi
        val p = Vec3()
        p.x = r * math.cos(w).toFloat
        p.y = r * math.sin(w).toFloat

        tri(math.abs(math.cos(w).toFloat), p)
      }
    }



  }

  override def init() = {
    Shader.load("lo", "shaders/lo").monitor()
  }

  var t = 0
  override def animate(dt:Float) = {
    // val r = Random.vec3.map { case v => v *= 0.0001f; v.z=0f; v }
    // mesh.vertices.foreach{ case v => v += r() }
    // mesh.update

    t += 1
    if(t % 1 == 0){
      // mesh.clear
      // gen()
      // mesh.update
    }
  }

  override def draw() = {
    shader("lo")
    model.draw
  }


}

classOf[Lo]