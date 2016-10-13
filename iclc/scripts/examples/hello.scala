

// try copy and pasting other scripts from the examples directory..

class Hello extends SeerActor {

  val n = 30       // number of shapes
  val scl = 1f / n   // scale value

  var boost = 1f   // boost rotation speed

  //make a list of sine oscillators
  val sines = (0 until n) map( _ => Sine(100, 1f/n) )
  sines.foreach( _ >> Out )  // connect oscillators ot output

  // make a list of cubes, using for and yield
  val cubes = for(i <- 0 until n) yield { 
    val c = Cube().scale(1,scl,1).translate(0,i*scl - .5f,0)
    c.material = Material.specular
    c.material.color = HSV(i*scl,0.7f,0.7f)
    c
  }

  // listen to Mouse events
  Mouse.listen {
    case List(event:String,x:Float,y:Float,dx:Float,dy:Float) => boost = (x-0.5) * 10
  }

  // animate callback called every frame prior to draw
  override def animate(dt:Float){
    // normal for loop
    for(i <- 0 until n){
      val c = cubes(i)
      c.rotate(0, (i+1)*scl/100*boost, 0)  // rotate arround y axis

      // ramp sine frequency to target over 100 samples
      val s = sines(i)
      val target = (c.pose.quat.toX() dot Vec3(1,0,0)) + 1
      s.f = Ramp(s.f.value, target*100 + 200, 100) 
    }
  }

  // draw callback called every frame
  override def draw() = cubes.foreach( _.draw )
}

classOf[Hello]   // must return actor class from script to be intantiated