import seer.openni._

class BodyActor extends SeerActor {


  val body = Var("body", new UserLoop())
  if(body().frames.isEmpty) body().loadKryo("data/body/stand.k.bin")

  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh) 

  val joints = ListBuffer[Vec3]()

  override def animate(dt:Float) = {

    val out = ListBuffer[User]()
    body().io(Seq(), out)

    joints.clear
    joints ++= out.flatMap(_.skeleton.joints.values)

    mesh.clear
    mesh.vertices ++= out.flatMap(_.points)
    mesh.update
  }

  override def draw(){
    model.draw
    // joints.foreach { case p => Sphere().scale(0.01f).translate(p).draw }
  }

  var speed = 1f

  Keyboard.listen {
    // case 'r' => body().toggleRecord()
    case 't' => body().togglePlay()
    // case 'x' => body().stack()
    // case 'c' => body().clear()
    case '\t' => body().reverse()
    case 'I' => speed *=2; body().setSpeed(speed)
    case 'K' => speed /=2; body().setSpeed(speed)
    // case 'o' => body().saveKryo()
    // case 'l' => body().load("2018-05-21-20.46.19.bin")

    // case 'y' =>
      // body().frames.trimStart(body().frame.toInt)
      // body().frame = 0
    // case 'u' =>
      // body().frames.trimEnd(body().frames.length-1 - body().frame.toInt)
      // body().frame = 0
  }


}

classOf[BodyActor]