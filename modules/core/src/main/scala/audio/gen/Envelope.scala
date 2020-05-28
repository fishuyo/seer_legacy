
package com.fishuyo.seer
package audio


object Envelope {
  def apply(e:Envelope) = {
    val env = new Envelope(e.segments)
    for(i <- 0 until e.segments){
      env.lengths(i) = e.lengths(i)
      env.values(i) = e.values(i)
    }
  }
}
class Envelope(val segments:Int) extends Gen {

  val lengths = Array.fill(segments)(0f)
  val values = Array.fill(segments+1)(0f)
  var segmentIndices:Seq[Int] = Seq()

  var ramp = new Curve(0,0,1) //Ramp(0,0,1)
  var done = true
  var pos = 0
  var duration = 0

  var func = util.Ease.expoOut _

  def reset(){
    done = false
    pos = 0
    segmentIndices = lengths.map(_ * Audio().sampleRate).map(_.toInt).scanLeft(0)(_+_)
    duration = segmentIndices.last
    //1f 2f -> 44100 88200
    // 0  44100 88200+44100
  }

  override def apply() = {
    if(pos == duration) done = true
    if(!done){
      segmentIndices.zipWithIndex.foreach { case (off,i) =>
        if(pos == off){
          val l = lengths(i) * Audio().sampleRate
          // ramp = Ramp(values(i), values(i+1), l.toInt)
          ramp = new Curve(value, values(i+1), l.toInt, func)
          // println(s"new segment $i -> ${values(i)} ${values(i+1)} $l")
        }
      }
      pos += 1
      value = ramp()
      value
    } else { 
      value = 0f
      value
    }
  }
}

class ADSR extends Envelope(4){

  def attack(dur:Float, amp:Float=1f) = {
    lengths(0) = dur
    values(1) = amp
    this
  }
  
  def decay(dur:Float, amp:Float=0.8f) = {
    lengths(1) = dur 
    values(2) = amp
    values(3) = amp
    this
  }
  def sustain(dur:Float) = { lengths(2) = dur; this }
  def release(dur:Float) = { lengths(3) = dur; this }
}
