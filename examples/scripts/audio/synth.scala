
class Synth extends SeerActor {
  
  val synths = for(i <- 0 until 20) yield {    
    val env = new ADSR
    env.attack(0.01f)
    env.decay(0.2f,0.1f)
    env.sustain(0f)
    env.release(1f)

    val gain = Ramp(0.1f,0.1f,1000)
    val freq = Ramp(60f,60f,100)
    val modF = Ramp(4f,4f,1000)
    val modD = Ramp(10f,10f,1000)

    val mod = modD * Sine(modF)
    val synth =  env * gain * Square(freq + mod)
    synth >> Out

    (env,gain,freq,modF,modD,synth)
  }

  // Mouse interaction, left
  var idx = 0
  Mouse.listen { 
    case "down" :: x :: y :: dx :: dy :: xs => 
      val (env,gain,freq,modF,modD,synth) = synths(idx)
      env.reset
      idx += 1; idx %= 20

    case "move" :: (x:Float) :: (y:Float) :: (dx:Float) :: (dy:Float) :: xs => 
      val (env,gain,freq,modF,modD,synth) = synths(idx)
      freq.set(x * 1400f + 10f)
      modF.set(y * 10f)
      modD.set(dy * 20f)

    case _ => 

  }

  // Apple multitouch trackpad interaction
  Trackpad.clear
  Trackpad.listen { case event =>
    
    event.fingers.zipWithIndex.foreach{ case (f,i) =>
      
      val (env,gain,freq,modF,modD,synth) = synths(f.id)

      freq.set(f.pos.x * 1400f + 10f)
      gain.set(f.size * 0.055f)
      modF.set(f.pos.y * 10f)
      // modD.set(f.angle * 8f)
      if(f.state == "down") env.reset
    }
  }

  // Graphics
  val spheres = synths.map{ case s => Sphere() }
  spheres.foreach{ case s => s.material.transparent = true }  
  
  override def draw() = spheres.foreach(_.draw())

  override def animate(dt:Float) = {
    synths.zipWithIndex.foreach { case ((env,gain,freq,modF,modD,synth), index) =>
    
      val s = spheres(index)
      s.scale.set(env.value + gain.value*0.1f + 0.01f)
      
      val x = ((freq.value - 10f) / 1400f) * 2f - 1f
      val y = (modF.value / 10f) * 2f - 1f
      s.pose.pos.set(x, y, 0f)

      val color = HSV(modF.value / 10f, 0.5f, 1f)
      s.material.color.set(RGB(color) * 0.35f) 
    }
  }

}

classOf[Synth]