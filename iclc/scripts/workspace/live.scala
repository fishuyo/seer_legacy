


// try copy and pasting other scripts from the examples directory..


class Simple extends SeerActor {
  
  val model = Cube()                       // generate cube model
  val osc = Sine(Random.int(100,200)())    // create new sine oscillator
  osc * 0.5f >> Out                        // scale and connect oscillator to audio output
  
  override def draw(){
    model.draw   
  }

  override def animate(dt:Float){
    // rotate cube model along each axis at different rates
    model.rotate(0.01, 0.02, 0.03)    
  }
}

classOf[Simple]
