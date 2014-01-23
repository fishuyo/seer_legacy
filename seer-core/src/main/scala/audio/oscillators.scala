
package com.fishuyo.seer
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

class Osc(var frequency:Float = 440.f, var amp:Float = 1.f) extends Gen{
  var phase = 0.f
  override def apply() = {
    phase += frequency / 44100.f
    phase %= 1
    gen() * amp
  }

  def f() = {val that=this; new Gen(){ gen = ()=>{ that.frequency }} }
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

object Scale {
  var root = 440.f
  var ratios = Array(1.f, 1.1f, 1.3f, 1.67f, 1.8f)
  def note( idx: Int) : Float = {
    var i = idx % ratios.length
    var s = idx.toFloat / ratios.length + 1.f
    root * ratios(i)*s
  }
}