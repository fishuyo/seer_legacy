
package com.fishuyo.seer
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
object Sine { def apply(f:Float=440f, a:Float=1f) = new Sine(f,a) }
class Sine(f:Float=440f, var a:Float=1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = math.sin(phase * 2*math.Pi).toFloat * a
    value
  }
}

/**
  * Triangle wave oscillator ^
  */
class Tri(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (1f - 4f * math.abs((phase + 0.25f) % 1 - 0.5f)) * a
    value
  }
}

/**
  * Saw /|/
  */
class Saw(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    value = (((phase / 2f + 0.25f) % 0.5f - 0.25f) * 4f) * a
    value
  }
}

class Square(f:Float = 100f, var a:Float = 1f) extends Osc(f) {
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