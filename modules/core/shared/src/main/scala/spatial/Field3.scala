
package com.fishuyo.seer
package spatial

import util._

import spire.syntax.cfor._


/**
  * A Field represents data grids in n-dimensional space. 
  * Individual cells can be accesed as well as interpolation between cells through lookup at a position in space.
  */
object Field3 {

  val scratchMap = collection.mutable.HashMap[Field3,Field3]()
  def scratch(f:Field3) = scratchMap.getOrElseUpdate(f, new Field3(f.nx,f.ny,f.nz,f.dx,f.dy,f.dz))


  def setBoundary(f:Field3, b:Int) = {
  // void setBoundary ( int N, int b, flo at * x )
    // for ( i <- 1 until f.nx ) {
    //   f(0 ,i) = if(b==1) -f(1,i) else f(1,i);
    //   f(f.nx-2,i) = if(b==1) -f(f.nx-1,i) else f(f.nx-1,i);
    //   f(i,0 ) = if(b==2) -f(i,1) else f(i,1);
    //   f(i,f.nx-2) = if(b==2) -f(i,f.nx-1) else f(i,f.nx-1);
    // }
    // f(0 ,0 ) = 0.5f*(f(1,0 )+f(0 ,1));
    // f(0 ,f.nx-2) = 0.5f*(f(1,f.nx-2)+f(0 ,f.nx-1 ));
    // f(f.nx-2,0 ) = 0.5f*(f(f.nx-1,0 )+f(f.nx-2,1));
    // f(f.nx-2,f.nx-2) = 0.5f*(f(f.nx-1,f.nx-2)+f(f.nx-2,f.nx-1 ));
  }

  def diffuse(f:Field3) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy*f.dz)
    val tmp = scratch(f)
    
    for (z <- 1 until f.nz-1; y <- 1 until f.ny-1 ; x <- 1 until f.nx-1 ){
      var fxyz = f(x,y,z)
      tmp(x,y,z) = fxyz + a * ( -6f * fxyz + f(x+1,y,z) + f(x-1,y,z) + f(x,y+1,z) + f(x,y-1,z) + f(x,y,z+1) + f(x,y,z-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:Field3, dt:Float, b:Int=0, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+6f*a), b, itr)
  }

  def linearSolve(f0:Field3, a:Float, a2:Float, b:Int, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ i =>
      cfor(1)(_ < f0.nz-1, _ + 1){ z => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.ny-1, _ + 1){ y => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ x => //i <- 1 until f0.nx-1;
        f(x,y,z) = ( f0(x,y,z) + a*( f(x-1,y,z) + f(x+1,y,z) + f(x,y-1,z) + f(x,y+1,z) + f(x,y,z+1) + f(x,y,z-1) ) ) * a2;
      }}}
      setBoundary(f,b);
    }
    f0.set( f )
    f0
  }

  def advect(f:Field3, vf:VecField3, dt:Float) = {
    val next = scratch(f)
    val dt0 = dt*f.nx //N;

    for (k <- 1 until f.nz-1; j <- 1 until f.ny-1; i <- 1 until f.nx-1 ){
      val v = vf(i,j,k)
      var x = i - dt0 * v.x
      var y = j - dt0 * v.y
      var z = k - dt0 * v.z
      if (x < 0.5) x = 0.5; if (x > f.nx + 0.5) x = f.nx + 0.5; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5; if (y > f.ny + 0.5) y = f.ny + 0.5; val j0=y.toInt; val j1=j0+1;
      if (z < 0.5) z = 0.5; if (z > f.nz + 0.5) z = f.nz + 0.5; val k0=z.toInt; val k1=k0+1;
      val dx = x-i0; val dy = y-j0; val dz = z-k0;
      val xy0 = lerp( lerp(f(i0,j0,k0), f(i1,j0,k0), dx), lerp(f(i0,j1,k0), f(i1,j1,k0), dx), dy)
      val xy1 = lerp( lerp(f(i0,j0,k1), f(i1,j0,k1), dx), lerp(f(i0,j1,k1), f(i1,j1,k1), dx), dy)
      next(i,j,k) = lerp(xy0, xy1, dz) //s0 * (t0 * f(i0,j0) + t1 * f(i0,j1) ) + s1 * ( t0 * f(i1,j0) + t1 * f(i1,j1) )
    }
    setBoundary (next, 0);
    f.set( next )
    f
  }

}

class Field3(val nx:Int, val ny:Int, val nz:Int, val dx:Float=1f, val dy:Float=1f, val dz:Float=1f){
  val data = new Array[Float](nx*ny*nz)

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int, z:Int) = data(z*ny*nx + y*nx + x)
  def update(x:Int, y:Int, z:Int, value:Float) = data(z*ny*nx + y*nx + x) = value
  def set(f:Field3) = Array.copy(f.data,0,data,0,data.length)
}


object VecField3 {
  implicit def f2v(f:Float) = Vec3(f)  // XXX

  val scratchMap = collection.mutable.HashMap[VecField3,VecField3]()
  def scratch(f:VecField3) = scratchMap.getOrElseUpdate(f, new VecField3(f.nx,f.ny,f.nz,f.dx,f.dy,f.dz))

  def setBoundary(f:VecField3, b:Int) = {
  // void setBoundary ( int N, int b, flo at * x )
    // for ( i <- 1 until f.nx ) {
    //   f(0 ,i).x = (if(b==1) -f(1,i).x else f(1,i).x)
    //   f(f.nx-2,i).x = (if(b==1) -f(f.nx-1,i).x else f(f.nx-1,i).x)
    //   f(i,0).y = (if(b==1) -f(i,1).y else f(i,1).y)
    //   f(i,f.nx-2).y = (if(b==1) -f(i,f.nx-1).y else f(i,f.nx-1).y)
    // }
    // f(0 ,0) = 0.5f*(f(1,0 )+f(0 ,1));
    // f(0 ,f.nx-2) = 0.5f*(f(1,f.nx-2)+f(0 ,f.nx-1 ));
    // f(f.nx-2,0 ) = 0.5f*(f(f.nx-1,0 )+f(f.nx-2,1));
    // f(f.nx-2,f.nx-2) = 0.5f*(f(f.nx-1,f.nx-2)+f(f.nx-2,f.nx-1 ));
  }

  def diffuse(f:VecField3) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy*f.dz)
    val tmp = scratch(f)
    
    for (z <- 1 until f.nz-1; y <- 1 until f.ny-1 ; x <- 1 until f.nx-1 ){
      var fxyz = f(x,y,z)
      tmp(x,y,z) = fxyz + a * ( -6f * fxyz + f(x+1,y,z) + f(x-1,y,z) + f(x,y+1,z) + f(x,y-1,z) + f(x,y,z+1) + f(x,y,z-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:VecField3, dt:Float, b:Int=0, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+6f*a), b, itr)
  }

  def linearSolve(f0:VecField3, a:Float, a2:Float, b:Int, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ i =>
      cfor(1)(_ < f0.nz-1, _ + 1){ z => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.ny-1, _ + 1){ y => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ x => //i <- 1 until f0.nx-1;
        f(x,y,z) = ( f0(x,y,z) + a*( f(x-1,y,z) + f(x+1,y,z) + f(x,y-1,z) + f(x,y+1,z) + f(x,y,z+1) + f(x,y,z-1) ) ) * a2;
      }}}
      setBoundary(f,b);
    }
    f0.set( f )
    f0
  }

  def advect(f:VecField3, vf:VecField3, dt:Float, b:Int) = {
    val next = scratch(f)
    val dt0 = dt*f.nx //N;
    for (k <- 1 until f.nz-1; j <- 1 until f.ny-1; i <- 1 until f.nx-1 ){
    try {
      val v = vf(i,j,k)
      var x = i - dt0 * v.x
      var y = j - dt0 * v.y
      var z = k - dt0 * v.z
      if (x < 0.5) x = 0.5; if (x > f.nx + 0.5) x = f.nx + 0.5; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5; if (y > f.ny + 0.5) y = f.ny + 0.5; val j0=y.toInt; val j1=j0+1;
      if (z < 0.5) z = 0.5; if (z > f.nz + 0.5) z = f.nz + 0.5; val k0=z.toInt; val k1=k0+1;
      val dx = x-i0; val dy = y-j0; val dz = z-k0;
      val xy0 = lerp( lerp(f(i0,j0,k0), f(i1,j0,k0), dx), lerp(f(i0,j1,k0), f(i1,j1,k0), dx), dy)
      val xy1 = lerp( lerp(f(i0,j0,k1), f(i1,j0,k1), dx), lerp(f(i0,j1,k1), f(i1,j1,k1), dx), dy)
      next(i,j,k) = lerp(xy0, xy1, dz) //s0 * (t0 * f(i0,j0) + t1 * f(i0,j1) ) + s1 * ( t0 * f(i1,j0) + t1 * f(i1,j1) )
    } catch { case e:Exception => println(s"$i $j $k"); println(e)}
    }
    setBoundary (next, b);
    f.set( next )
    f
  }

  def project(vf:VecField3) = {
  // void project ( int N, float * u, float * v, float * p, float * div )
    // val f = scratch(vf) // (p,div) ?

    // val h = 1f/vf.nx;
    // for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
    //   // div(i,j) = -0.5*h*(u(i+1,j)-u(i-1,j)+v(i,j+1)-v(i,j-1));
    //   val div = -0.5f * h * ( vf(i+1,j).x - vf(i-1,j).x + vf(i,j+1).y - vf(i,j-1).y );
    //   f(i,j) = Vec3(0,div)
    // }
    // setBoundary (f, 0)
    // for (k <- 0 until 20 ){
    //   for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
    //     f(i,j).x = ( f(i,j).y + f(i-1,j).x + f(i+1,j).x + f(i,j-1).x + f(i,j+1).x ) / 4f;
    //   }
    //   setBoundary (f, 0);
    // }
    // for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
    //   vf(i,j).x -= 0.5f * ( f(i+1,j).x - f(i-1,j).x ) / h;
    //   vf(i,j).y -= 0.5f * ( f(i,j+1).x - f(i,j-1).x ) / h;
    // }
    // setBoundary (f,1); //setBoundary ( N, 2, v );
    // // f.set( vf )
    // vf
  }
}

class VecField3(val nx:Int, val ny:Int, val nz:Int, val dx:Float=0.1f, val dy:Float=0.1f, val dz:Float=0.1f){
  val data:Array[Vec3] = Array.fill(nx*ny*nz)(Vec3())

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int, z:Int) = data(z*ny*nx + y*nx + x)

  // interpolate 
  def apply( v:Vec3 ):Vec3 = {
    val cen = Vec3()
    val halfsize = nx*dx / 2  //XXX

    if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize || v.z < -halfsize || v.z > halfsize) return Vec3(0)
    val nv = ((v-cen) + Vec3(halfsize)) * (nx-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    if( nv.z >= nz-1 ) nv.z = nz - 1.001f
    apply(nv.x,nv.y,nv.z)
  }

  def apply(x:Float, y:Float, z:Float):Vec3 = {
    val x1 = x.floor.toInt; val dx = x - x1
    val y1 = y.floor.toInt; val dy = y - y1
    val z1 = z.floor.toInt; val dz = z - z1
    val xy1 = this(x1,y1,z1).lerp( this(x1+1,y1,z1), dx )
    val xy2 = this(x1,y1+1,z1).lerp( this(x1+1,y1+1,z1), dx )
    val xy3 = this(x1,y1,z1+1).lerp( this(x1+1,y1,z1+1), dx )
    val xy4 = this(x1,y1+1,z1+1).lerp( this(x1+1,y1+1,z1+1), dx )
    val xyz1 = xy1.lerp(xy2,dy)
    val xyz2 = xy3.lerp(xy3,dy)
    xyz1.lerp( xyz2, dz)
  } 

  def update(x:Int, y:Int, z:Int, value:Vec3):Unit = data(z*ny*nx + y*nx + x) = value

  def update(v:Vec3, value:Vec3):Unit = {
    val cen = Vec3()
    val halfsize = nx*dx / 2  //XXX

    if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize || v.z < -halfsize || v.z > halfsize) return
    val nv = ((v-cen) + Vec3(halfsize)) * (nx-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    if( nv.z >= nz-1 ) nv.y = nz - 1.001f
    update(nv.x.round, nv.y.round, nv.z.round, value)
    // update(nv.x.toInt, nv.y.toInt, value)
    // update(nv.x.toInt+1, nv.y.toInt, value)
    // update(nv.x.toInt, nv.y.toInt+1, value)
    // update(nv.x.toInt+1, nv.y.toInt+1, value)
  }


  def set(f:VecField3) = Array.copy(f.data,0,data,0,data.length)

  def getCenter(x:Int,y:Int,z:Int):Vec3 = {
    Vec3(x*dx,y*dy,z*dz) + Vec3(dx,dy,dz)*0.5f
  }
}