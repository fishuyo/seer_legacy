
package com.fishuyo
package graphics
import maths._
import spatial._

//import javax.media.opengl._
import com.badlogic.gdx.graphics.{Camera => C}
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Matrix4

/**
* Camera
*/

object Camera extends Camera
class Camera extends PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f) {

  var nav = new Nav()
  var rotWorld = new Matrix4()
  var theta = 0.f
  var dw = 0.f

  near = .05f

  def step( dt:Float ) = {

    if(dw != 0.f){
      theta += dw
      if(theta > 180.f) theta = -180.f
      val s = math.sin(theta.toRadians).toFloat
      val c = math.cos(theta.toRadians).toFloat
      rotWorld.set( Array(c,0,s,0, 0,1,0,0, -s,0,c,0, 0,0,0,1 ) )
    }

    nav.step(dt)
    position.set(nav.pos.x, nav.pos.y, nav.pos.z)
    direction.set(nav.mUF.x, nav.mUF.y, nav.mUF.z)
    up.set(nav.mUU.x, nav.mUU.y, nav.mUU.z)

    update()

    Matrix4.mul(combined.`val`, rotWorld.`val`);
    Matrix4.mul(view.`val`, rotWorld.`val`);

  }
  def rotateWorld(speed:Float) = dw = speed

  def setFOV(f:Float) = fieldOfView = f

  def ray(x:Int,y:Int) = {
    val r = this.getPickRay(x,y)
    Ray( Vec3(r.origin.x,r.origin.y,r.origin.z), Vec3(r.direction.x,r.direction.y,r.direction.z))
  }

}

 
