
package com.fishuyo.seer
package util

import maths.Vec3

trait Generator[T]{
	self =>

	var value:T = null.asInstanceOf[T]

	def apply(): T
	def next() = apply()

	def map[S](f: T => S): Generator[S] = new Generator[S]{
		def apply() = {
			value = f(self.apply())
			value
		}
	}
	def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S]{
		def apply() = {
			value = f(self.apply()).apply()
			value
		}
	}

	def foldLeft[S](s:S)(f:(S,Generator[T]) => S):Generator[S] = new Generator[S]{
		value = s
		def apply() = {
			value = f(value,self)
			value
		}
	}
}

class Single[T](val x:T) extends Generator[T]{
	value = x
	def apply() = x
}

// class Accumulator[Float](var x:Float, val g:Generator[Float]) extends Generator[Float]{
// 	def apply() = {
// 		x += g()
// 		x
// 	}
// }

// Single(1.f).foldLeft(0.f)((v,g) => v + g() )


object Random {
	val r = new java.util.Random
	val rseed = new java.util.Random
	
	def seed() = r.setSeed(rseed.nextLong)
	def seed(s:Long) = r.setSeed(s)

	val int = new Generator[Int]{
		def apply() = r.nextInt
	}
	def int(lo:Int,hi:Int): Generator[Int] = for(x <- int) yield lo + math.abs(x) % (hi - lo)

	val float = new Generator[Float]{
		def apply() = r.nextFloat
	}
	def float(lo:Float,hi:Float): Generator[Float] = for(x <- float) yield x * (hi-lo) + lo

	val double = new Generator[Double]{
		def apply() = r.nextFloat
	}

	val bool = new Generator[Boolean]{
		def apply() = r.nextBoolean
	}

	val vec3 = new Generator[Vec3]{
		def apply() = Vec3(float(-1.f,1.f)(),float(-1.f,1.f)(),float(-1.f,1.f)())
	}
	def vec3(lo:Vec3, hi:Vec3): Generator[Vec3] = {
		for(x <- float(lo.x,hi.x);
				y <- float(lo.y,hi.y);
				z <- float(lo.z,hi.z)) yield Vec3(x,y,z)
	}

	def oneOf[T](xs: T*) = for(i <- int(0,xs.length)) yield xs(i)

	def decide[T](xs:Seq[T], prob:Seq[Float]) = new Generator[T]{
		def apply():T = {
			val r = float()
			var sum = 0.f
			for( i <- ( 0 until prob.length)){
				sum += prob(i)
				if( r < sum) return xs(i)
			}
			xs(0)
		}
	}

}

