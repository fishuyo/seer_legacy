
package com.fishuyo.seer

import spatial._

import com.twitter.chill.KryoInjection


object KryoTest extends App {

val someItem = for( i <- (0 until 100)) yield util.Random.vec3()

val bytes:  Array[Byte]    = KryoInjection(someItem)
val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)

println(tryDecode)

}
