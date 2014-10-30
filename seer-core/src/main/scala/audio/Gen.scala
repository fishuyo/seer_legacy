
package com.fishuyo.seer
package audio

trait Gen extends AudioSource {
  self =>

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

  // def foldLeft[S](s:S)(f:(S,Generator[T]) => S):Generator[S] = new Generator[S]{
  //   value = s
  //   def apply() = {
  //     value = f(value,self)
  //     value
  //   }
  // }
}

	// def *(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() * o() }} }
	// def +(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() + o() }} }
	// def -(o:Gen) = {val that=this; new Gen(){ gen = ()=>{ that() - o() }} }
	// def unary_-(o:Gen) = new Gen(){ gen = ()=>{ -o() }}

 //  def ->(o:Osc) = {val that=this; new Gen(){ gen = ()=>{ o.f(that()); o() }} }
  

class Var(var value:Float) extends Gen{
  def apply() = value
}


class Osc(var f:Gen) extends Gen{
  var sr = Audio().sampleRate
  var phase = 0f
  // var dphase:Float = frequency / Audio().sampleRate
  def apply() = {
    phase += f() / sr
    phase %= 1f
    phase
  }

  // def f() = {val that=this; new Gen(){ gen = ()=>{ that.frequency }} }
  // def f(f:Float) = { frequency = f; dphase = frequency / Audio().sampleRate }
  // def a(f:Float) = amp = f

}

class Sine(f:Float=440f, var a:Float=1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    math.sin(phase * 2*math.Pi).toFloat * a
  }
}

class Tri(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (1f - 4f * math.abs((phase + 0.25f) % 1 - 0.5f)) * a
  }
}

class Saw(f:Float = 440f, var a:Float = 1f) extends Osc(f) {
  override def apply() = {
    super.apply()
    (((phase / 2f + 0.25f) % 0.5f - 0.25f) * 4f) * a
  }
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