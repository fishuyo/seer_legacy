
package com.fishuyo
package maths

import graphics._
import spatial._

import javax.media.opengl._

class VecField3D( var n:Int, c:Vec3=Vec3(0), halfsize:Float=1.f)  extends AABB(c,halfsize) with GLAnimatable {

  var data = new Array[Vec3](n*n*n) 
  val dn = (2*halfsize)/n

  def apply(x:Int,y:Int,z:Int):Vec3 = data(z*n*n+y*n+x)

  //linear interpolate 
  def apply( v:Vec3 ):Vec3 = {
    if( !contains(v) ) return Vec3(0)
    val nv = ((v-c) + Vec3(halfsize)) * (n-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= n-1 ) nv.x = n - 1.001f
    if( nv.y >= n-1 ) nv.y = n - 1.001f
    if( nv.z >= n-1 ) nv.z = n - 1.001f
    apply(nv.x,nv.y,nv.z)
  }
  def apply(x:Float,y:Float,z:Float):Vec3 = {
    val x1 = x.floor.toInt; val dx = x - x1
    val y1 = y.floor.toInt; val dy = y - y1
    val z1 = z.floor.toInt; val dz = z - z1
    var vx1 = this(x1,y1,z1).lerp( this(x1+1,y1,z1), dx )
    var vx2 = this(x1,y1+1,z1).lerp( this(x1+1,y1+1,z1), dx )
    var vy1 = vx1.lerp( vx2, dy)
    vx1 = this(x1,y1,z1+1).lerp( this(x1+1,y1,z1+1), dx )
    vx2 = this(x1,y1+1,z1+1).lerp( this(x1+1,y1+1,z1+1), dx )
    var vy2 = vx1.lerp( vx2, dy)
    vy1.lerp(vy2,dz)
  }
  def set(x:Int,y:Int,z:Int, v:Vec3) = data(z*n*n + y*n + x) = v
  def set(i:Int, v:Vec3) = data(i) = v
  def sset(x:Int,y:Int,z:Int, v:Vec3)= if(!(x<0||x>=n||y<0||y>=n||z<0||z>=n)) data(z*n*n + y*n + x) = v

  def clear = for( i<-(0 until n*n*n)) set(i, Vec3(0))

  def binAt( v:Vec3 ):Option[Vec3] = {
    if( !contains(v) ) None
    else Some(((v-c) + Vec3(halfsize)) * (n-1) / (2*halfsize))
  }
  def centerOfBin(x:Int,y:Int,z:Int) :Vec3 = {
    (c - Vec3(halfsize)) + Vec3(x,y,z)*dn + Vec3(dn/2)
  }

  override def onDraw(gl:GL2) = {
    gl.glColor4f(1.f,1.f,1.f,.3f)
    gl.glLineWidth(1.f)
    gl.glBegin( GL.GL_LINES )
      for( z <- (0 until n); y<-(0 until n); x<-(0 until n) ){
        val cen = centerOfBin(x,y,z)
        val d = cen + this(x,y,z)
        gl.glVertex3f(cen.x,cen.y,cen.z)
        gl.glVertex3f(d.x,d.y,d.z)
      }
    gl.glEnd
  }


}
