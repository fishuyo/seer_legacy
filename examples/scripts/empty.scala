
import com.fishuyo.seer._

import dynamic.SeerScript
import graphics._
import audio._
import io._

object Script extends SeerScript {


  override def draw(){}

  override def animate(dt:Float){}

  override def audioIO(io:AudioIOBuffer){}

  Trackpad.clear
  Trackpad.connect
  Trackpad.bind((touch) => {})
}

Script