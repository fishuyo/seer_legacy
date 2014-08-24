
// package com.fishuyo.seer
// package audio.gen

// import types._

// class Osc(var freq: Generator[Float], var amp: Generator[Float]) extends Generator[Float] with com.fishuyo.seer.audio.AudioSource {
//   var phase = freq.foldLeft(0.f)((p,g) => (p + g() / 44100.f) % 1 )

//   override def apply() = phase()*amp()

//   override def audioIO(in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
//     for( i <- 0 until numSamples){
//       val s = this()
//       out(0)(i) += s/2.f
//       out(1)(i) += s/2.f
//     }
//   }

//   def f(v:Float){ f(new Single(v)) }
//   def f(frequency:Generator[Float]){
//   	freq = frequency
//   	phase = freq.foldLeft(phase.value)((p,g) => (p + g() / 44100.f) % 1 )
//   }
//   def a(v:Float){ a(new Single(v)) }
//   def a(amplitude:Generator[Float]) = amp = amplitude
  
// }

// class Sine(f: Generator[Float], a: Generator[Float]) extends Osc(f,a){
// 	override def apply() = math.sin(phase()*2*math.Pi).toFloat * amp()
// }



// object Env {
//   def decay(t:Float) = new Generator[Float]{
//     value = 1.f
//     def apply() = {value = math.max(value-(1.f/(t*44100.f)),0.f); value }
//   }
//   // def adsr(ta:Float,td:Float,ts:Float,tr:Float) = new Generator[Float]{
//   //   value = 0.f
//   //   // def apply() = {value = math.max(value-(1.f/(t*44100.f)),0.f); value }
//   // }
// }
