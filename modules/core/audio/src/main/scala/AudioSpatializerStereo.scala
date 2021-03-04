
package seer 
package audio 

import spatial._

class AudioSpatializerStereo(width:Float = 0.5f) extends AudioSpatializer {

  // speakers += Speaker(0, Vec3(-width,0,0), Vec3(1,0,0))
  // speakers += Speaker(1, Vec3(width,0,0), Vec3(-1,0,0))

  override def render(io:AudioIOBuffer, listener:Pose) = {
    val buf = io.clone

    sources.foreach { case src =>
      var rpos = src.location - listener.pos
      var atten = 1.0f / (1.0f + rpos.mag())
      atten = math.pow(atten, 2.0).toFloat

      rpos = listener.quat.inverse.rotate(rpos)
      val (gainL, gainR) = pan(rpos.normalized)

      buf.zero()
      buf.reset()
      src.audioIO(buf)
      buf.multiply(0, atten * gainL)
      buf.multiply(1, atten * gainR)
      io += buf
    }

  }
  
  def pan(relPos:Vec3):(Float,Float) = {
    var panVal = 0.5;
    if (relPos.z != 0.0 || relPos.x != 0.0) {
      panVal = 1.0 - math.abs(math.atan2(relPos.z, relPos.x) / Pi);
    }

    val gainL = math.cos((Pi / 2.0) * panVal)
    val gainR = math.sin((Pi / 2.0) * panVal)
    (gainL.toFloat, gainR.toFloat)
  }
}

