
package com.fishuyo.seer
package audio

import types._

/**
  * Gen represents a floating point audio sample generator
  */
trait Gen extends AudioSource {
  self =>

  var value = 0f

	def apply():Float

	override def audioIO( io:AudioIOBuffer ){ 
    while(io()){
      val s = self()
      for( c <- 0 until io.channelsOut)
        io.outSum(c)(s)
    }
  }

  def map(f: Float => Float): Gen = new Gen{
    def apply() = f(self.apply())
  }

  def flatMap(f: Float => Gen): Gen = new Gen{
    def apply() = f(self.apply()).apply()
  }

  def foldLeft(s:Float)(f:(Float,Gen) => Float):Gen = new Gen{
    value = s
    def apply() = {
      value = f(value,self)
      value
    }
  }
}

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
}

/**
  * Inefficient sin wave oscillator
  */
class Sine(f:Float=440f, var a:Float=1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    math.sin(phase * 2*math.Pi).toFloat * a
  }
}

/**
  * Triangle wave oscillator ^
  */
class Tri(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (1f - 4f * math.abs((phase + 0.25f) % 1 - 0.5f)) * a
  }
}

/**
  * Saw /|/
  */
class Saw(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (((phase / 2f + 0.25f) % 0.5f - 0.25f) * 4f) * a
  }
}

class Square(f:Float = 100f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (math.round(phase) - 0.5f)*2*a
  }
}

class Step(f:Float = 1f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (math.round(phase))*a
  }
}

//
class Var(v:Float) extends Gen {
  value = v
  def apply() = value
}


class PulseTrain(var width:Gen) extends Gen {
  var i = 0
  def apply() = {
    if(i == 0) value = 1f
    else value = 0f
    val w = width().toInt
    if(w > 0) i = (i+1) % w
    value
  }
}



class Impulse extends Gen {
  value = 1f
  def apply() = { val s=value; value=0f; s}
}

class Noise extends Gen {
  def apply() = Random.float()
}

class Nyquist extends Gen {
  value = 1f
  def apply() = { value = -value; value }
}

class Add(var add:Float=1f) extends Gen{
  value = -add
  def apply() = { value += add; value}
}

class Line(var begin:Float=0f, var end:Float=1f, var len:Float=44100) extends Add((end-begin)/len)

class Ramp(start:Float, end:Float, len:Int) extends Gen {
  var add = (end-start) / len
  value = start

  def apply() = {
    if(math.abs(value-end) >= math.abs(add)) value += add
    else value = end
    value
  }
}


class Delay(var delay:Gen=100f, var c:Gen=0.9f, maxDelay:Int=44100) extends Gen {
  val ring = new LoopBuffer[Float](maxDelay)

  def apply(in:Float) = {
    val d = delay().toInt
    if(d > 0 && d <= maxDelay) ring.count_ = d
    val s = in + ring() * c()
    ring() = s //in + s * c()
    s 
  }
  def apply = apply(0f)
} 


// //class Val(var)

// class KarplusStrong(f:Float=440f, var blend:Float=.99f, var damping:Float=.5f) extends Osc(f,1f){
//   /*val buf = Array[Float]()
//   gen = ()=>{
//     var valu = buf.shift();
//     var rndValue = (rnd() > blend) ? -1 : 1;

//     damping = damping > 0 ? damping : 0;

//     var value = rndValue * (valu + last) * (.5 - damping / 100);

//     last = value;

//     buffer.push(value);
//   }*/
// }

// object Scale {
//   var root = 440f
//   var ratios = Array(1f, 1.1f, 1.3f, 1.67f, 1.8f)
//   def note( idx: Int) : Float = {
//     var i = idx % ratios.length
//     var s = idx.toFloat / ratios.length + 1f
//     root * ratios(i)*s
//   }
// }