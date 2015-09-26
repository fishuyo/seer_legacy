
package com.fishuyo.seer
package graphics
import spatial._

//import javax.media.opengl._
import com.badlogic.gdx.graphics.{Camera => GdxCamera}
import com.badlogic.gdx.graphics.{PerspectiveCamera => GdxPCam }
import com.badlogic.gdx.graphics.{OrthographicCamera => GdxOCam }
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Matrix4

/**
* Camera
*/

trait NavCamera extends GdxCamera {
  var nav = new Nav()
  def step( dt:Float ){
    nav.step(dt)
    position.set(nav.pos.x, nav.pos.y, nav.pos.z)
    direction.set(nav.mUF.x, nav.mUF.y, nav.mUF.z)
    up.set(nav.mUU.x, nav.mUU.y, nav.mUU.z)

    update()
  }

  def ray(x:Int,y:Int) = {
    val r = this.getPickRay(x,y)
    Ray( Vec3(r.origin.x,r.origin.y,r.origin.z), Vec3(r.direction.x,r.direction.y,r.direction.z))
  }
}

class PerspectiveCamera extends GdxPCam(67f, Window.a0, 1f) with NavCamera {
  near = .01f
  def setFOV(f:Float) = fieldOfView = f
}

class OrthographicCamera(w:Float,h:Float) extends GdxOCam(w,h) with NavCamera
 
object Camera extends PerspectiveCamera
