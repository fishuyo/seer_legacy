
package com.fishuyo.seer
package spatial


class VecField3D( var n:Int, cen:Vec3=Vec3(0), hsize:Float=1f)  extends AABB(cen,hsize) {

  var data = new Array[Vec3](n*n*n) 
  val dn = (2*halfsize)/n

  def apply(x:Int,y:Int,z:Int):Vec3 = data(z*n*n+y*n+x)

  //linear interpolate 
  def apply( v:Vec3 ):Vec3 = {
    if( !contains(v) ) return Vec3(0)
    val nv = ((v-center) + Vec3(halfsize)) * (n-1) / (2*halfsize)
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

  def update(p:Vec3, v:Vec3){
    if( !contains(p) ) return Vec3(0)
    val nv = ((p-center) + Vec3(halfsize)) * (n-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= n-1 ) nv.x = n - 1.001f
    if( nv.y >= n-1 ) nv.y = n - 1.001f
    if( nv.z >= n-1 ) nv.z = n - 1.001f
    set(nv.x.toInt,nv.y.toInt,nv.z.toInt,v)
    // update(nv.x,nv.y,nv.z,v)
  }
  // def update(x:Float,y:Float,z:Float,v:Vec3) = {
  //   val x1 = x.floor.toInt; val dx = x - x1
  //   val y1 = y.floor.toInt; val dy = y - y1
  //   val z1 = z.floor.toInt; val dz = z - z1
  //   var vx1 = this(x1,y1,z1).lerp( this(x1+1,y1,z1), dx )
  //   var vx2 = this(x1,y1+1,z1).lerp( this(x1+1,y1+1,z1), dx )
  //   var vy1 = vx1.lerp( vx2, dy)
  //   vx1 = this(x1,y1,z1+1).lerp( this(x1+1,y1,z1+1), dx )
  //   vx2 = this(x1,y1+1,z1+1).lerp( this(x1+1,y1+1,z1+1), dx )
  //   var vy2 = vx1.lerp( vx2, dy)
  //   vy1.lerp(vy2,dz)
  // }


  def set(x:Int,y:Int,z:Int, v:Vec3) = data(z*n*n + y*n + x) = v
  def set(i:Int, v:Vec3) = data(i) = v
  def sset(x:Int,y:Int,z:Int, v:Vec3)= if(!(x<0||x>=n||y<0||y>=n||z<0||z>=n)) data(z*n*n + y*n + x) = v

  def clear = for( i<-(0 until n*n*n)) set(i, Vec3(0))

  def binAt( v:Vec3 ):Option[Vec3] = {
    if( !contains(v) ) None
    else Some(((v-center) + Vec3(halfsize)) * (n-1) / (2*halfsize))
  }
  def centerOfBin(x:Int,y:Int,z:Int) :Vec3 = {
    (center - Vec3(halfsize)) + Vec3(x,y,z)*dn + Vec3(dn/2)
  }

}
