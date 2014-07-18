
package com.fishuyo.seer
package types

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
	def filter(f:T => Boolean): Generator[T] = new Generator[T]{
		def apply() = {
			value = self.apply()
			while(!f(value)) value = self.apply()
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


