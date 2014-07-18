
package com.fishuyo.seer

package object spatial {
	val Pi = math.Pi
	val Phi = (1.f + math.sqrt(5))/2.f; // the golden ratio

	implicit def d2f(d:Double) = d.toFloat
}

