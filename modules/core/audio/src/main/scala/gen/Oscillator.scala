
package seer
package audio

/**
  * Osc represents a phase accumulating oscillator 
  */
class Osc(var f:Gen) extends Gen{
  var sr = Audio().sampleRate
  var phase = 0f
  // var dphase:Float = frequency / Audio().sampleRate
  def apply() = {
    phase += f() / sr
    phase %= 1f
    phase
  }
  override def apply(in:Float) = { 
    f = in
    apply()
  }
}

/**
  * sin wave oscillator
  */
object Sine { 
  def apply(f:Gen, a:Float=1f) = { val s = new Sine(0,a); s.f = f; s }
  def apply(f:Float, a:Float) = new Sine(f,a)
}
class Sine(f:Float, var a:Float) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = math.sin(phase * 2*math.Pi).toFloat * a
    value
  }
}

/**
  * Triangle wave oscillator ^
  */
object Tri { 
  def apply(f:Gen, a:Float=1f) = { val t = new Tri(0,a); t.f = f; t}
  def apply(f:Float, a:Float) = new Tri(f,a)
}
class Tri(f:Float, var a:Float) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (1f - 4f * math.abs((phase + 0.25f) % 1 - 0.5f)) * a
    value
  }
}

/**
  * Saw /|/
  */
object Saw { 
  def apply(f:Gen, a:Float=1f) = { val s = new Saw(0,a); s.f = f; s}
  def apply(f:Float, a:Float) = new Saw(f,a)
}
class Saw(f:Float, var a:Float) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (((phase / 2f + 0.25f) % 0.5f - 0.25f) * 4f) * a
    value
  }
}

object Square { 
  def apply(f:Gen, a:Float=1f) = { val s = new Square(0,a); s.f = f; s}
  def apply(f:Float, a:Float) = new Square(f,a)
}
class Square(f:Float, var a:Float) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (math.round(phase) - 0.5f)*2*a
    value
  }
}

class Step(f:Float = 1f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (math.round(phase))*a
    value
  }
}