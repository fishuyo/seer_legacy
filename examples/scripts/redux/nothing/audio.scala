

class AudioActor extends SeerActor {

  // val io = AudioDevice(in=1, out=2)
  
  val loops = os.list(os.pwd/"data"/"nothing"/"sources").filter(_.ext == "wav").map(_.toString).map(new Loop(_) )

  loops.foreach { case l => l.location = Random.vec3()*5f; l.location.y = 0f; l.play() }

  println(loops.length)

  
  var listener = Pose()
  var spatializer = AudioSpatializer.stereo()
  loops.foreach { case l => spatializer.sources += l }
  Out += spatializer

  // loops >> spatializer >> Audio.out
  // spatializer >> Audio.out 

  override def animate(dt:Float) = {}

  override def draw() = {
    loops.foreach { case s =>
      Sphere().scale(0.1f).translate(s.location).draw 
    }
    Sphere().scale(0.06f).translate(listener.pos).draw 
    Cube().scale(0.05f,0.05f,0.2f).translate(listener.pos).rotate(listener.quat).draw
    Cube().scale(0.03f,0.03f,0.03f).translate(listener.pos + listener.quat.toZ()*0.3).draw //rotate(listener.quat).draw
    Cube().scale(0.03f,0.03f,0.03f).translate(listener.pos + listener.quat.toX()*0.1).draw //rotate(listener.quat).draw
    spatializer.listener.set(listener)
  }

  Trackpad.listen { case t =>
    listener.pos.set(Vec3(2*t.pos.x-1, 0f, 2*(1-t.pos.y)-1) * 5f)
    if(t.vel.mag() > 0.1f){
      val d = Vec3(t.vel.x, 0f, -t.vel.y).normalize // forward
      listener.quat.slerpTo(listener.quat.fromForwardUp(-d,Vec3(0,1,0)), 0.1f) // actually from +Z(back) and up
    }
  }
}

classOf[AudioActor]