
package seer
package openni.examples

import graphics._
import openni._

object FrameViewer extends SeerApp {

  OpenNI.init
  OpenNI.startDepth
  OpenNI.startColor

  val quad = Plane().translate(-1,0,0)
  val quad2 = Plane().translate(1,0,0)
  var texture:ImageTexture = _
  var texture2:ImageTexture = _

  OpenNI.onFrame { 
    case DepthFrame(img) => Run.animate {
      if(texture == null){
        texture = Texture(img)
        quad.scale.set(-1,-img.aspect,1)
        quad.material.loadTexture(texture)
      } else {
        // println("d") // doesn't freeze when printing... why.. XXX
        texture.image.set(img)
        texture.update
      }
    }
    case ColorFrame(img) => Run.animate {
      if(texture2 == null){
        texture2 = Texture(img)
        quad2.scale.set(-1,-img.aspect,1)
        quad2.material.loadTexture(texture2)
      } else {
        texture2.image.set(img)
        texture2.update
      }
    }
  }

  override def draw(){
    quad.draw
    quad2.draw
  }

}
