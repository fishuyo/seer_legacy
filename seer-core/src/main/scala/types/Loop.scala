
package com.fishuyo.seer
package types


class Loop[T:scala.reflect.ClassTag](val maxSize:Int) extends Generator[T] {
  val buffer = new LoopBuffer[T](maxSize)

  def apply() = buffer()
}