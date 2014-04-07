
package com.fishuyo.seer
package util

import maths._

import scala.util.{Random => JRandom }

object Randf{
	val gen = new JRandom
	def apply(min:Float=0.f, max:Float=1.f, gaus:Boolean=false) = new Randf(min,max,gaus)
}

class Randf( var min:Float=0.f, var max:Float=1.f, var gaus:Boolean=false){

	def apply():Float = {
		if( min == max ) return min
		if( gaus ){
			(Randf.gen.nextGaussian.toFloat / (10.f) + .5f) * (max-min) + min
		}else{
			Randf.gen.nextFloat * (max-min) + min
		}
	}
	def seed(s:Long) = Randf.gen.setSeed(s)
	def set( mi:Float ) { setMinMax(mi,mi,gaus) }
	def setMinMax( mi:Float, ma:Float, gaus:Boolean=false ){
		min=mi; max=ma;	
	}
}

object RandVec3 {
	def apply(min:Vec3=Vec3(0), max:Vec3=Vec3(1), gaus:Boolean=false) = new RandVec3(min,max,gaus)	
}
class RandVec3( min:Vec3=Vec3(0), max:Vec3=Vec3(1), var gaus:Boolean=false){
	var x = Randf(min.x,max.x,gaus)
	var y = Randf(min.y,max.y,gaus)
	var z = Randf(min.z,max.z,gaus)

	def apply() = Vec3(x(),y(),z())

	def seed(s:Long) = Randf.gen.setSeed(s)
	def set( min:Vec3 ) { setMinMax(min,min,gaus) }
	def setMinMax( min:Vec3, max:Vec3, gaus:Boolean=false ){
		x = Randf(min.x,max.x,gaus)
		y = Randf(min.y,max.y,gaus)
		z = Randf(min.z,max.z,gaus)	
	}
}

object Chooser {
	def apply[T]( c:Array[T], p:Array[Float]=Array[Float]()) = new Chooser[T](c,p)
}
class Chooser[T]( var choices:Array[T], var prob:Array[Float]=Array[Float]()){
	val equal = prob.length == 0

	def setProb( a:Array[Float] ) = prob = a
	def setChoices( c:Array[T]) = choices = c
	
	def apply():T = {
		var i = 0
		if( equal ){
			i = JRandom.nextInt(choices.length)
			return choices(i)
		} else{
			val r = JRandom.nextFloat
			var sum = 0.f
			for( i <- ( 0 until prob.length)){
				sum += prob(i)
				if( r < sum) return choices(i)
			}
		}
		choices(i)
	}
}