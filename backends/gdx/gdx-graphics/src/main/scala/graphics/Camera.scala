
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
  def step( dt:Float ): Unit ={
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

class ManualCamera extends GdxCamera with NavCamera {
  val tmp = new com.badlogic.gdx.math.Vector3()
  override def update(): Unit ={ update(true) }
  override def update(updateFrustum:Boolean): Unit ={
    view.setToLookAt(position, tmp.set(position).add(direction), up);
    combined.set(projection);
    Matrix4.mul(combined.`val`, view.`val`);

    if (updateFrustum) {
      invProjectionView.set(combined);
      Matrix4.inv(invProjectionView.`val`);
      frustum.update(invProjectionView);
    }
  }
}

object PerspectiveCamera { def apply() = new PerspectiveCamera }
class PerspectiveCamera extends GdxPCam(67f, Window.a0, 1f) with NavCamera {
  near = .01f
  def setFOV(f:Float) = fieldOfView = f
  def setFOVx(f:Float) = {
    // val fovy = f * viewportHeight / viewportWidth
    val fovy = 2*math.atan(math.tan(f*Pi/180f/2)*viewportHeight/viewportWidth) * 180f / Pi
    fieldOfView = fovy.toFloat
    // println(s"fovy: $fovy")
  }
}

object OrthographicCamera { def apply(w:Float,h:Float) = new OrthographicCamera(w,h) }
// class Ortho(w:Float,h:Float) extends OrthographicCamera(w,h)
class OrthographicCamera(w:Float,h:Float) extends GdxOCam(w,h) with NavCamera
 
object Camera extends PerspectiveCamera
