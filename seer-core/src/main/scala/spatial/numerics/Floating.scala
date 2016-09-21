
package com.fishuyo.seer
package spatial.numerics

import scala.math.Numeric._

import scala.language.implicitConversions

object Floating {
  trait ExtraImplicits {
    implicit def infixFloatingOps[T](x: T)(implicit num: Floating[T]): Floating[T]#FloatingOps =
      new num.FloatingOps(x)
  }
  object Implicits extends ExtraImplicits
  
  trait FloatIsFloating extends Floating[Float] {
    val Pi = scala.math.Pi.toFloat
    val E = scala.math.E.toFloat

    def acos(x: Float) = scala.math.acos(x).toFloat
    def asin(x: Float) = scala.math.asin(x).toFloat
    def atan(x: Float) = scala.math.atan(x).toFloat
    def atan2(x: Float, y: Float) = scala.math.atan2(x, y).toFloat
    def sin(x: Float) = scala.math.sin(x).toFloat
    def sinh(x: Float) = scala.math.sinh(x).toFloat
    def cos(x: Float) = scala.math.cos(x).toFloat
    def cosh(x: Float) = scala.math.cosh(x).toFloat
    def tan(x: Float) = scala.math.tan(x).toFloat
    def tanh(x: Float) = scala.math.tanh(x).toFloat
    def cbrt(x: Float) = scala.math.cbrt(x).toFloat
    def ceil(x: Float) = scala.math.ceil(x).toFloat
    def exp(x: Float) = scala.math.exp(x).toFloat
    def expm1(x: Float) = scala.math.expm1(x).toFloat
    def floor(x: Float) = scala.math.floor(x).toFloat
    def hypot(x: Float, y: Float) = scala.math.hypot(x, y).toFloat
    def log(x: Float) = scala.math.log(x).toFloat
    def log10(x: Float) = scala.math.log10(x).toFloat
    def log1p(x: Float) = scala.math.log1p(x).toFloat
    def pow(x: Float, y: Float) = scala.math.pow(x, y).toFloat
    def rint(x: Float) = scala.math.rint(x).toFloat
    def round(x: Float) = scala.math.round(x).toFloat
    def sqrt(x: Float) = scala.math.sqrt(x).toFloat
    def toDegrees(x: Float) = scala.math.toDegrees(x).toFloat
    def toRadians(x: Float) = scala.math.toRadians(x).toFloat
    def ulp(x: Float) = scala.math.ulp(x).toFloat

    def fromByte(x: Byte) = x.toFloat
    def fromShort(x: Short) = x.toFloat
    def fromChar(x: Char) = x.toFloat
    def fromLong(x: Long) = x.toFloat
    def fromFloat(x: Float) = x
    def fromDouble(x: Double) = x.toFloat
  }
  implicit object FloatIsFloating extends FloatIsFloating with FloatIsFractional with Ordering.FloatOrdering

  trait DoubleIsFloating extends Floating[Double] {
    val Pi = scala.math.Pi
    val E = scala.math.E

    def acos(x: Double) = scala.math.acos(x)
    def asin(x: Double) = scala.math.asin(x)
    def atan(x: Double) = scala.math.atan(x)
    def atan2(x: Double, y: Double) = scala.math.atan2(x, y)
    def sin(x: Double) = scala.math.sin(x)
    def sinh(x: Double) = scala.math.sinh(x)
    def cos(x: Double) = scala.math.cos(x)
    def cosh(x: Double) = scala.math.cosh(x)
    def tan(x: Double) = scala.math.tan(x)
    def tanh(x: Double) = scala.math.tanh(x)
    def cbrt(x: Double) = scala.math.cbrt(x)
    def ceil(x: Double) = scala.math.ceil(x)
    def exp(x: Double) = scala.math.exp(x)
    def expm1(x: Double) = scala.math.expm1(x)
    def floor(x: Double) = scala.math.floor(x)
    def hypot(x: Double, y: Double) = scala.math.hypot(x, y)
    def log(x: Double) = scala.math.log(x)
    def log10(x: Double) = scala.math.log10(x)
    def log1p(x: Double) = scala.math.log1p(x)
    def pow(x: Double, y: Double) = scala.math.pow(x, y)
    def rint(x: Double) = scala.math.rint(x)
    def round(x: Double) = scala.math.round(x)
    def sqrt(x: Double) = scala.math.sqrt(x)
    def toDegrees(x: Double) = scala.math.toDegrees(x)
    def toRadians(x: Double) = scala.math.toRadians(x)
    def ulp(x: Double) = scala.math.ulp(x)

    def fromByte(x: Byte) = x.toDouble
    def fromShort(x: Short) = x.toDouble
    def fromChar(x: Char) = x.toDouble
    def fromLong(x: Long) = x.toDouble
    def fromFloat(x: Float) = x.toDouble
    def fromDouble(x: Double) = x
  }
  implicit object DoubleIsFloating extends DoubleIsFloating with DoubleIsFractional with Ordering.DoubleOrdering
}

trait Floating[T] extends Fractional[T] {
  val Pi: T
  val E: T

  def div(x: T, y: T): T
  def acos(x: T): T
  def asin(x: T): T
  def atan(x: T): T
  def atan2(x: T, y: T): T
  def sin(x: T): T
  def sinh(x: T): T
  def cos(x: T): T
  def cosh(x: T): T
  def tan(x: T): T
  def tanh(x: T): T
  def cbrt(x: T): T
  def ceil(x: T): T
  def exp(x: T): T
  def expm1(x: T): T
  def floor(x: T): T
  def hypot(x: T, y: T): T
  def log(x: T): T
  def log10(x: T): T
  def log1p(x: T): T
  def pow(x: T, y: T): T
  def rint(x: T): T
  def round(x: T): T
  def sqrt(x: T): T
  def toDegrees(x: T): T
  def toRadians(x: T): T
  def ulp(x: T): T

  def fromByte(x: Byte): T
  def fromShort(x: Short): T
  def fromChar(x: Char): T
  def fromLong(x: Long): T
  def fromFloat(x: Float): T
  def fromDouble(x: Double): T

  class FloatingOps(lhs: T) extends Ops(lhs) {
    def /(rhs: T) = div(lhs, rhs)
  }
  implicit def mkFloatingOps(lhs: T) =
    new FloatingOps(lhs)

}