
import de.sciss.osc._

class PosenetVis extends SeerActor {

  val osc = new flow.OSCSend()
  osc.connect("localhost", 7000)

  val cameras = HashMap[String, Pose]()

  val recv = new flow.OSCRecv()
  recv.listen(8008)
  val view = "/(.*)/(.*)/viewMatrix".r
  val cam = "/(.*)/(.*)/cameraMatrix".r
  val pose = "/(.*)/(.*)/pose2d".r
  recv.bind {
    case (Message(view(group,name), vs @ _*), _) => 
      println(s"New camera: $group $name $vs")
      val fvs = vs.asInstanceOf[Seq[Float]]
      val b = ArrayBuffer[Float]()
      b ++= fvs
      b ++= Seq(0f,0f,0f,1f)

      val m = Mat4()
      val indices = List(0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15)
      b.zip(indices).foreach { case (v,i) =>
        m(i) = v 
      }

      cameras(name) = Pose(m)
      
    // case (Message(cam(group,name), vs @ _*), _) => 
    //   println(s"New camera: $group $name $vs")
    //   val fvs = vs.asInstanceOf[Seq[Float]]
    //   val b = ArrayBuffer[Float]() 
    //   b ++= fvs.take(3)
    //   b += 0f
    //   b ++= fvs.drop(3).take(3)
    //   b += 0f
    //   b ++= fvs.drop(6).take(3)
    //   b += 0f
    //   b ++= fvs.drop(9).take(3)
    //   b += 1f

    //   val m = Mat4()
    //   m.set(b.toSeq :_*)
    //   cameras(name) = Pose(m)
    case (Message(pose(group,name), vs @ _*), _) => 

    case _ => ()
  }

  var pose3D = List[(Vec3,Float)]() 
  VRPN.clear()
  // VRPN.analogListen("tim.pose0@tcp://67.58.49.49", (data) => {  
  // VRPN.analogListen("tim.pose0@localhost", (data) => { 
  VRPN.analogListen("grpc_client.pose0@localhost", (data) => { 
    pose3D = data.init.grouped(4).map { case a => (Vec3(a(0),a(1),a(2)), a(3).toFloat) }.toList
  })

  override def draw() = {
    pose3D.foreach { case (p,s) =>
      val v = Vec3(p.x,-p.y,p.z*0.1f)
      val color = RGB(Vec3(1,0,0).lerp(Vec3(0,1,0), s))
      // println(s)
      val sphere = Sphere()
      // sphere.material = Material.basic
      sphere.material.color.set(color)
      if(s > 0.5f) sphere.scale(0.1f).translate(v).draw()
    }

    cameras.foreach { case (name, pose) =>

      val cube = Cube().scale(0.1f,0.1f,0.2f)
      cube.pose.set(pose)

      cube.draw()
    }
  }

  override def preunload() = {
    try{ recv.disconnect } catch { case e:Exception => () }
  }

}

classOf[PosenetVis]