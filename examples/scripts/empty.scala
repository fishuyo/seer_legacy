
import com.fishuyo.seer._

import dynamic.SeerScript
import graphics._
import audio._
import io._

object Script extends SeerScript {


  override def draw(){
    Sphere().draw
  }

  override def animate(dt:Float){}

  override def audioIO(io:AudioIOBuffer){}

  Trackpad.clear
  Trackpad.connect
  Trackpad.bind((touch) => {
    val p = touch.pos

    touch.count match {
      case 1 => println(s"${p.x} ${p.y}")
      case 2 =>
      case 3 =>
      case _ => ()  
    }
  })
}

Script