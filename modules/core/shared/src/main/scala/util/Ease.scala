
  /**
   * Easing functions thanks to: http://gizma.com/easing/
   */

  package com.fishuyo.seer
  package util

  object Ease {

    @inline def quadIn(t:Float) = t*t
    @inline def quadOut(t:Float) = -t*(t-2)
    @inline def quad(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f*d*d
      else {
        d -= 1
        -0.5f * (d*(d-2)-1)
      }
    }
    @inline def cubicIn(t:Float) = t*t*t
    @inline def cubicOut(t:Float) = (t-1)*(t-1)*(t-1)+1
    @inline def cubic(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f*d*d*d
      else {
        d -= 2
        0.5f * (d*d*d + 2)
      }
    }
    @inline def quartIn(t:Float) = t*t*t*t
    @inline def quartOut(t:Float) = -((t-1)*(t-1)*(t-1)*(t-1) - 1)
    @inline def quart(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f*d*d*d*d
      else {
        d -= 2
        -0.5f * (d*d*d*d - 2)
      }
    }
    @inline def quintIn(t:Float) = t*t*t*t*t
    @inline def quintOut(t:Float) = (t-1)*(t-1)*(t-1)*(t-1)*(t-1) + 1
    @inline def quint(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f*d*d*d*d*d
      else {
        d -= 2
        0.5f * (d*d*d*d*d + 2)
      }
    }

    @inline def expoIn(t:Float) = math.pow(2, 10 * (t-1)).toFloat
    @inline def expoOut(t:Float) = -math.pow(2, -10 * t).toFloat + 1
    @inline def expo(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f * expoIn(d)
      else {
        d -= 1
        0.5f * (expoOut(d) + 1)
      }
    }

    @inline def sineIn(t:Float) = -math.cos(t * math.Pi/2) + 1
    @inline def sineOut(t:Float) = math.sin(t * math.Pi/2)
    @inline def sine(t:Float) = -0.5f * (math.cos(t * math.Pi) - 1)

    @inline def circIn(t:Float) = -(math.sqrt(1 - t*t) - 1)
    @inline def circOut(t:Float) = math.sqrt(1 - (t-1)*(t-1))
    @inline def circ(t:Float) = {
      var d = 2*t
      if (d < 1) 0.5f * circIn(d)
      else {
        d -= 1
        0.5f * (circOut(d) + 1)
      }
    }


  }
