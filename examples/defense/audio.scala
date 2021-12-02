

class AudioActor extends SeerActor {
  
  val loops = os.list(os.pwd/"data"/"nothing"/"sources").filter(_.ext == "wav").map(_.toString).map(new Loop(_) )

  loops.zipWithIndex.foreach { case (l,i) => l.location = Vec3(-1f + 2f*i,0f,-0.5f); l.play() }
  
  var listener = Pose()
  var spatializer = AudioSpatializer.stereo()
  loops.foreach { case l => spatializer.sources += l }
  Out += spatializer

  // loops >> spatializer >> Audio.out
  // spatializer >> Audio.out 

  val index = Var("slideIndex", 0) 

  override def animate(dt:Float) = {
    spatializer.listener.pos.lerpTo(Vec3(index() * 2f,0f,0f), 0.01f)

  }

  
}

classOf[AudioActor]