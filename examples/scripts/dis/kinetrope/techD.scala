
class TechD extends SeerActor {

  val (w,h,d) = (5f, 2f, 6f)
  val room = Box().scale(w,h,d).translate(0,h/2f,0)

  val kinect = Box().scale(0.2,0.05,0.05)

  val projector = Box().scale(0.3,0.1,0.3).translate(0,h,d/3f)
  val projection = Plane().scale(w/2f,-h/2f,1f).translate(0,h/2f,-d/2f)
  val frustrum = Model(Mesh())
  frustrum.mesh.primitive = Lines
  val vs = for(x <- Seq(-1,1); y <- Seq(-1,1)) yield { 
    val v1 = projector.pose.pos + Vec3(0,0,-0.1f)
    val v2 = Vec3(w/2f, h/2f, -d/2f) * Vec3(x,y,1f)
    v2.y += h/2f
    Seq(v1,v2)
  }
  frustrum.mesh.vertices ++= vs.flatten

  override def init() = {
    projection.material.loadTexture("/Users/fishuyo/_projects/2020_dissertation/media/01_kinetrope/metree4.jpg")
  }

  override def draw() = {
    room.draw
    kinect.draw

    projector.draw
    projection.draw
    frustrum.draw
  }




}

classOf[TechD]