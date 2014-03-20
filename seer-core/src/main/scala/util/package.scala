

package com.fishuyo.seer

package object util{

	def clamper[@specialized(Int, Double) T : Ordering](low: T, high: T)(value:T): T = {
	  import Ordered._
	  if (value < low) low else if (value > high) high else value
	}

	@inline def clamp[@specialized(Int, Double) T : Ordering](value: T, low: T, high: T): T = {
	  import Ordered._
	  if (value < low) low else if (value > high) high else value
	}

	// @inline def map[@specialized(Int, Double) T : Numeric](value: T, inlow: T, inhigh: T, outlow:T, outhight:T): T = {
	//   import Numeric._
	//   val tmp = value - inlow * (inhigh-inlow)
	//   tmp*(outhigh-outlow) + outlow
	// }

	// @inline def lerp[@specialized(Int, Double) T : Ordering](v1:T, v2:T, t:T): T = {
	//   v1*(1.f-t)+v2*t
	// }

	class AutoMapper(var outlow:Float, var outhigh:Float){
		var (inl,inh) = (0.f,1.f)
		def apply(v:Float) = {
			if( v < inl) inl = v
			if( v > inh) inh = v
			map(v,inl,inh,outlow,outhigh)	
		}
	}
	def automapper(outlow:Float,outhigh:Float) = new AutoMapper(outlow,outhigh)

	@inline def map(value: Float, inlow: Float, inhigh: Float, outlow:Float, outhigh:Float): Float = {
	  val tmp = (value - inlow) / (inhigh-inlow)
	  tmp*(outhigh-outlow) + outlow
	}

	@inline def lerp(v1:Float, v2:Float, t:Float): Float = {
	  v1*(1.f-t)+v2*t
	}
}
