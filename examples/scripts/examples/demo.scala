import seer.world.particle._

class DemoParticles extends SeerActor {
  val n = 30000
  val mesh = Mesh()
  mesh.maxVertices = n
  mesh.primitive = Points
  val model = Model(mesh)
 

  val emitter = new ParticleEmitter(30000) {
    override def animate(dt:Float){
      attractors.foreach(_(particles.toSeq))
      att2(particles.toSeq)
      super.animate(dt)
    }
    override def draw(){ 
      mesh.clear
      mesh.vertices ++= particles.map( _.position )
      mesh.update
      model.draw
    }
  }
  emitter.ttl = 0f
  emitter.damping = 100f
  Gravity.set(0f,0f,0f)

  var numAttractors = 10 
  var attractors = (0 until numAttractors).map{ case _ => 
    val a = new Attractor
    a.position = Vec3(Random.vec2(),0f).normalize
    a.radius = 0f
    a.strength = 0.15f //+ Random.float()*0.01f
    a
  }
 
  val att2 = new Attractor
  att2.radius = 0f
  att2.strength = -0.05f


  (0 until 10000).foreach{ case _ => emitter += Particle(Random.vec3()) }


    Schedule.every(200.millis){
      emitter += Particle(Random.vec3()*3) //Vec3(Random.float(),2,0))
    }

  override def animate(dt:Float){
    emitter.animate(dt)
  }
  override def draw(){
    emitter.draw()
    // attractors.foreach{ case a => Sphere().scale(0.005f).translate(a.position).draw }
  }


}































classOf[DemoParticles]