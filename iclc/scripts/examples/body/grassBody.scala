
import com.fishuyo.seer.branch._

class Grass extends SeerActor {

  val model = Model(Cylinder.generateMesh(1f,0.8f,2,30)) 
  model.material.transparent = true
  model.material.color = RGBA(0.2,0.2,0.2,0.2)

  var trees:IndexedSeq[Tree] = _
  var wind = true
  var force = 0f
  var windVolume = 0f

  grassWall()

  override def init(){
    OpenNI.listen { 
      case body :: bodies =>                // user object contains image data, point cloud, and skeleton joint information
        val spine = body.skeleton.bones("spine").quat.toZ
        val angle = math.atan2(spine.y, spine.x).toDegrees
        force = 4*(angle / 180.0f - 0.5f)
        if( force.isNaN ) force = 0f

        // Run.animate {
        //   mesh.clear
        //   mesh.vertices ++= body.points // set mesh vertices to user point cloud data
        //   mesh.update  
        // }             
      case _ => ()
    }
  }

  def grassWall(){
    trees = for(x <- (-50 to 50); z <- (0 until 3)) yield {
      val t = new Tree()
      t.trunk.thick = 0.025
      t.trunk.pose.pos = Vec3(x.toFloat/10 + Random.float(), -1.5, -z + Random.float()*0.5)
      generateGrass(t, Vec3(), Random.int(3,6)())
      t
    }
  }
  def grass(){
    trees = for(x <- (-20 to 20); z <- (0 until 2)) yield {
      val t = new Tree()
      t.trunk.thick = 0.025
      t.trunk.pose.pos = Vec3(x.toFloat/10 + Random.float(), -1.5, -z + Random.float()*0.5)
      generateGrass(t, Vec3(0.0,Random.float()*0.05 + x.toFloat / 400,0.0), Random.int(2,5)())
      // generateGrass(t, Vec3(), Random.int(6,9)())
      t
    }
  }

  def generateGrass(tree:Tree, bend:Vec3, depth:Int=100){
    tree.trunk.children.clear
    tree.generate(depth){ case b:Branch =>
      val pos = b.pose.quat.toZ * b.length
      val child = Branch(b, Pose(pos,Quat().fromEuler(bend.x,bend.y,bend.z))) //Random.vec3())))
      child.maxLength = b.maxLength * 0.95
      child.length = b.length * 0.95
      child.thick = b.thick * 0.999
      b.children += child
    }
  }

  var i0 = 0
  def cut(t:Float){
    val i1 = (t * trees.length).toInt
    for( i <- i0 until i1){
      val tree = trees(i)
      tree.trunk.children.clear
      generateGrass(tree, Vec3(0.0,Random.float()*0.1 + tree.trunk.pose.pos.x / 20,0.0), Random.int(1,3)())
    }
    i0 = i1
  }


  override def receive = super.receive orElse {           // handles messages from other actors
    case ("grass", b:Boolean) => active = b 
    case ("grass", f:Float) => model.material.color.set(f*0.2,f*0.2,f*0.2,f*0.2) 
    case ("cut", t:Float) => cut(t)
    case ("wind", f:Float) => force = f
    case "reset" => active = false; grassWall(); i0 = 0
  }

  override def draw(){
    // if(!active) return
    MatrixStack.push()
    MatrixStack.scale(0.1)
    trees.foreach { case tree =>
      tree.draw(model)
    }
    MatrixStack.pop()
  }

  override def animate(dt:Float){
    // if(!active) return

    trees.foreach { case tree =>
      // tree.trunk.grow(dt)
      tree.trunk.applyForce( Vec3(1,0,0) * force + Random.vec3()*force)

      tree.trunk.step(dt)
      tree.trunk.solve()
    }
  }

}

classOf[Grass]