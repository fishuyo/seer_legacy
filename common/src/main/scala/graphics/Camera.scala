
package com.fishuyo
package graphics
import maths._

//import javax.media.opengl._
import com.badlogic.gdx.graphics.PerspectiveCamera

/**
* Camera
*/

object Cam extends PerspectiveCamera(67.f, SimpleAppSize.aspect, 1.f)

/*object Camera extends Camera(Vec3(0,0,2),0.f)

class Camera( var position:Vec3=Vec3(0,0,2), az:Float=0.f ) {

  val initPos = position + Vec3(0)
  val initDir = az
  val rad = scala.math.Pi / 180.f 
  
  var fovy = 60.f
  var aspect = 1.f
  var near = .01f
  var far = 40.f
  var t  = math.tan( fovy * rad / 2 )
  var height = near * t
  var width = height * aspect

  var v = 2.f
  var a = 90.f
  
  var velocity = Vec3(0,0,0)
  var upDirection = Vec3(0,1,0)

  var elevation=0.0f
  var azimuth=az
  var roll=0.0f
  var w = Vec3( 0,0,0 ) //angluar velocity


  def loadGLProjection( gl: GL2 ) = {
    gl.glMatrixMode(fixedfunc.GLMatrixFunc.GL_PROJECTION)
    gl.glLoadIdentity()
    t = math.tan( fovy * rad / 2 )
    height = near * t
    width = height * aspect
    gl.glFrustum(-width,width,-height,height,near,far);
  }
  
  def loadGLModelview( gl: GL2 ) = {
    gl.glMatrixMode(fixedfunc.GLMatrixFunc.GL_MODELVIEW)
    gl.glLoadIdentity()
    gl.glRotatef( elevation, 1.f, 0, 0 )
    gl.glRotatef( azimuth, 0, 1.f, 0 )
    gl.glTranslatef( -position.x, -position.y, -position.z )
  }

  def projectPoint( x: Float, y: Float ) : Vec3 = {
    Vec3( width*(x-300.f+0.5)/300.f, height*(300.f-y+0.5f)/300.f, position.z - near)
  }

  def initialPosition() = { position = initPos; elevation=0.f; azimuth=initDir }
  def forward() = velocity = Vec3( math.sin( azimuth * rad),0, -math.cos(azimuth*rad) ).normalize * v
  def backward() = velocity = Vec3( math.sin( (180.f + azimuth) * rad),0, -math.cos((180.f+azimuth)*rad) ).normalize * v
  def left() = velocity = Vec3( math.sin( (270.f + azimuth) * rad),0, -math.cos((270.f+azimuth)*rad) ).normalize * v
  def right() = velocity = Vec3( math.sin( (90.f + azimuth) * rad),0, -math.cos((90.f+azimuth)*rad) ).normalize * v
  def up() = velocity = Vec3(0,v,0)
  def down() = velocity = Vec3(0,-v,0)

  def lookUp = w = Vec3(-a,0,0) 
  def lookDown = w = Vec3(a,0,0)
  def lookLeft = w = Vec3(0,-a,0)
  def lookRight = w = Vec3(0,a,0)

  def stop = velocity = Vec3(0)
  def stopLook = w = Vec3(0)

  def step( dt: Float ) = {
    position += velocity * dt
    val a = w * dt
    elevation += a.x; azimuth += a.y; roll += a.z;
    if( elevation > 180.f ) elevation = -180.f
    if( elevation < -180.f ) elevation = 180.f
    if( azimuth > 180.f ) azimuth = -180.f
    if( azimuth < -180.f ) azimuth = 180.f
  }
}*/
 
