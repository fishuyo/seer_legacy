
package com.fishuyo.seer
package audio

sealed trait FilterType
case object AllPass extends FilterType
case object LowPass extends FilterType
case object HighPass extends FilterType
case object BandPass extends FilterType
case object BandPassUnit extends FilterType
case object BandReject extends FilterType
case object Peaking extends FilterType
case object LowShelf extends FilterType
case object HighShelf extends FilterType


class Biquad(var freq:Float, var res:Float=1f, var mode:FilterType=LowPass) extends Gen {
  val frqToRad = (2f*math.Pi/Audio().sampleRate).toFloat

  var a0,a1,a2 = 0f
  var b0,b1,b2 = 0f
  var d1,d2 = 0f
  var resRecip = 0f
  var level = 1f
  var (alpha,beta) = (0f,1f)
  var imag, real = 0f

  setResonance(res)
  setFrequency(freq)

  def setFrequency(v:Float) = {
    freq = v
    val w = util.clamp(freq * frqToRad, -3.13f, 3.13f);
    real = math.cos(w).toFloat
    imag = math.sin(w).toFloat
    setResRecip(resRecip)
  }

  def setLevel(v:Float) = {
    level = v
    mode match {
      case Peaking => beta = 1f/level
      case LowShelf | HighShelf =>
        beta = 2f*math.pow(level, 0.25).toFloat
      case _ => beta = 1f
    }
    setResRecip(resRecip)
  }

  def setResonance(v:Float) = setResRecip(0.5f/v)
  def setResRecip(v:Float) = {
    resRecip = v
    alpha = imag * resRecip * beta;
    mode match {
      case LowShelf | HighShelf =>  () // coefs computed in type()
      case _ =>
        b0 = 1 / (1 + alpha) // 1/b_0
        b1 = -2 * real * b0
        b2 = (1 - alpha) * b0
    }
    setMode(mode)
  }

  def setMode(m:FilterType) = {
    mode = m
    mode match {
      case LowPass =>
        a1 = ((1f) - real) * b0
        a0 = a1 * (0.5f)
        a2 = a0
      case HighPass => 
        a1 = ((-1f) - real) * b0
        a0 = a1 * (-0.5f)
        a2 = a0
      case BandPass =>
        a0 = imag * (0.5f) * b0
        a1 = (0)
        a2 = -a0
      case BandPassUnit =>
        a0 = alpha * b0
        a1 = (0)
        a2 = -a0
      case BandReject =>
        a0 = b0
        a1 = b1
        a2 = b0
      case AllPass =>
        a0 = b2
        a1 = b1
        a2 = (1)
      case Peaking =>
        val alpha_A_b0 = alpha * level * b0
        a0 = b0 + alpha_A_b0
        a1 = b1
        a2 = b0 - alpha_A_b0
      case LowShelf =>
        val A = beta*beta*(0.25f) // sqrt(level)
        val Ap1 = A + (1f)
        val Am1 = A - (1f)
        b0 =    (1)/(Ap1 + Am1*real + alpha) // 1/b_0
        b1 =   (-2f)*(Am1 + Ap1*real         ) * b0
        b2 =          (Ap1 + Am1*real - alpha) * b0
        a0 =        A*(Ap1 - Am1*real + alpha) * b0
        a1 = ( 2f)*A*(Am1 - Ap1*real         ) * b0
        a2 =        A*(Ap1 - Am1*real - alpha) * b0
      case HighShelf =>
        val A = beta*beta*(0.25f) // sqrt(level)
        val Ap1 = A + (1f)
        val Am1 = A - (1f)
        b0 =   (1f)/(Ap1 - Am1*real + alpha) // 1/b_0
        b1 =   (2f)*(Am1 - Ap1*real         ) * b0
        b2 =         (Ap1 - Am1*real - alpha) * b0
        a0 =       A*(Ap1 + Am1*real + alpha) * b0
        a1 = (-2f)*A*(Am1 + Ap1*real         ) * b0
        a2 =       A*(Ap1 + Am1*real - alpha) * b0
    }
  }

  override def apply(in:Float) = {
    val i0 = in - d1*b1 - d2*b2
    val o0 = i0*a0 + d1*a1 + d2*a2
    d2 = d1
    d1 = i0
    o0
  }
  def apply():Float = apply(0f)
}