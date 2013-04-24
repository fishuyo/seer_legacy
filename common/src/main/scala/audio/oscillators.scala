
package com.fishuyo
package audio

class Gen extends AudioSource {
	def apply() = gen()
	var gen = ()=>{0.f}

	override def audioIO(in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    for( i <- 0 until numSamples){
      val s = this()
      out(0)(i) += s/2.f
      out(1)(i) += s/2.f
    }
  }

	def *(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() * o() }} }
	def +(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() + o() }} }
	def -(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() - o() }} }
	def unary_-(o:Gen) = new Gen(){ gen = ()=>{ -o() }}

  def ->(o:Osc) = {val that=this; new Gen(){ gen = ()=>{ o.f(that()); o() }} }
  
}
class SSine(var freq:Float = 440.f, var amp:Float=1.f) extends AudioSource {
  var phase = 0.f
  val pi2 = 2*math.Pi
  override def audioIO(in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
  for( c<-(0 until numOut))
    for( i <- 0 until numSamples){
      out(c)(i) += math.sin(phase*pi2).toFloat * amp
      phase += freq / 44100.f
      phase %= 1
    }
  }

  def f(f:Float) = freq = f
  def a(f:Float) = amp = f
}

class Osc(var frequency:Float = 440.f, var amp:Float = 1.f) extends Gen{
  var phase = 0.f
  override def apply() = {
    phase += frequency / 44100.f
    phase %= 1
    gen() * amp
  }

  def f(f:Float) = frequency = f
  def a(f:Float) = amp = f

}

class Sine(f:Float=440.f, a:Float=1.f) extends Osc(f,a) {
  gen = () => { math.sin(phase * 2*math.Pi).toFloat }
}

class Tri(f:Float = 440.f, a:Float = 1.f) extends Osc(f,a) {
  gen = () => {
    var out = 1.f - 4.f * math.abs((phase + 0.25f) % 1 - 0.5f)
    phase = if(phase >1) phase % 1 else phase
    out
  }
}

class Saw(f:Float=440.f, a:Float=1.f) extends Osc(f,a){
  gen = ()=>{
    var out = ((phase / 2.f + 0.25f) % 0.5f - 0.25f) * 4.f;
    phase = if(phase >1) phase % 1 else phase
    out
  }
}

//class Val(var)

class KarplusStrong(f:Float=440.f, var blend:Float=.99f, var damping:Float=.5f) extends Osc(f,1.f){
  /*val buf = Array[Float]()
  gen = ()=>{
    var valu = buf.shift();
    var rndValue = (rnd() > blend) ? -1 : 1;

    damping = damping > 0 ? damping : 0;

    var value = rndValue * (valu + last) * (.5 - damping / 100);

    last = value;

    buffer.push(value);
  }*/
}