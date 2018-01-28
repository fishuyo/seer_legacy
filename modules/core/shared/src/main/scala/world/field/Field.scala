
package com.fishuyo.seer
package world
package field

import spatial.Vec2

import spire.syntax.cfor._


/**
  * A Field represents data grids in n-dimensional space. 
  * Individual cells can be accesed as well as interpolation between cells through lookup at a position in space.
  */
object Field2D {

  val scratchMap = collection.mutable.HashMap[(Int,Int),Field2D]()
  def scratch(f:Field2D) = scratchMap.getOrElseUpdate((f.nx,f.ny), Field2D(f.nx,f.ny,f.dx,f.dy))

  def apply(nx:Int, ny:Int, dx:Float=1f, dy:Float=1f) = new Field2D(nx,ny,dx,dy)

  def setBoundary(f:Field2D, b:Int) = {
  // void setBoundary ( int N, int b, flo at * x )
    for ( i <- 1 until f.nx ) {
      f(0 ,i) = if(b==1) -f(1,i) else f(1,i);
      f(f.nx-2,i) = if(b==1) -f(f.nx-1,i) else f(f.nx-1,i);
      f(i,0 ) = if(b==2) -f(i,1) else f(i,1);
      f(i,f.nx-2) = if(b==2) -f(i,f.nx-1) else f(i,f.nx-1);
    }
    f(0 ,0 ) = 0.5f*(f(1,0 )+f(0 ,1));
    f(0 ,f.nx-2) = 0.5f*(f(1,f.nx-2)+f(0 ,f.nx-1 ));
    f(f.nx-2,0 ) = 0.5f*(f(f.nx-1,0 )+f(f.nx-2,1));
    f(f.nx-2,f.nx-2) = 0.5f*(f(f.nx-1,f.nx-2)+f(f.nx-2,f.nx-1 ));
  }

  def diffuse(f:Field2D) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy)
    val tmp = scratch(f)
    
    for (y <- ( 1 until f.ny-1 ); x <- ( 1 until f.nx-1 )){
      var fxy = f(x,y)
      tmp(x,y) = fxy + a * ( -4f * fxy + f(x+1,y) + f(x-1,y) + f(x,y+1) + f(x,y-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:Field2D, dt:Float, b:Int=0, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+4f*a), b, itr)
  }

  def linearSolve(f0:Field2D, a:Float, a2:Float, b:Int, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ k =>
      cfor(1)(_ < f0.ny-1, _ + 1){ j => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ i => //i <- 1 until f0.nx-1;
        f(i,j) = ( f0(i,j) + a*( f(i-1,j) + f(i+1,j) + f(i,j-1) + f(i,j+1) ) ) * a2;
      }}
      setBoundary(f,b);
    }
    f0.set( f )
    f0
  }

  def advect(f0:Field2D, vf:VecField2D, dt:Float) = {
    val f = scratch(f0)
    val dt0 = dt*f0.nx //N;

    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      val v = vf(i,j)
      var x = i - dt0 * v.x
      var y = j - dt0 * v.y
      if (x < 0.5) x = 0.5f; if (x > f0.nx + 0.5) x = f0.nx + 0.5f; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5f; if (y > f0.ny + 0.5) y = f0.ny + 0.5f; val j0=y.toInt; val j1=j0+1;
      val s1 = x-i0; val s0 = 1-s1; val t1 = y-j0; val t0 = 1-t1;
      f(i,j) = s0 * (t0 * f0(i0,j0) + t1 * f0(i0,j1) ) + s1 * ( t0 * f0(i1,j0) + t1 * f0(i1,j1) )
    }
    setBoundary (f, 0);
    f0.set( f )
    f0
  }

}

class Field2D(val nx:Int, val ny:Int, val dx:Float=1f, val dy:Float=1f){
  val data = new Array[Float](nx*ny)

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int) = data(y*nx + x)
  def update(x:Int, y:Int, value:Float) = data(y*nx + x) = value
  def set(f:Field2D) = Array.copy(f.data,0,data,0,data.length)
}


object VecField2D {
  implicit def f2v(f:Float) = Vec2(f)  // XXX

  val scratchMap = collection.mutable.HashMap[(Int,Int),VecField2D]()
  def scratch(f:VecField2D) = scratchMap.getOrElseUpdate((f.nx,f.ny), VecField2D(f.nx,f.ny,f.dx,f.dy))

  def apply(nx:Int, ny:Int, dx:Float=0.1f, dy:Float=0.1f) = new VecField2D(nx,ny,dx,dy)

  def setBoundary(f:VecField2D, b:Int) = {
  // void setBoundary ( int N, int b, flo at * x )
    for ( i <- 1 until f.nx ) {
      f(0 ,i).x = (if(b==1) -f(1,i).x else f(1,i).x)
      f(f.nx-2,i).x = (if(b==1) -f(f.nx-1,i).x else f(f.nx-1,i).x)
      f(i,0).y = (if(b==1) -f(i,1).y else f(i,1).y)
      f(i,f.nx-2).y = (if(b==1) -f(i,f.nx-1).y else f(i,f.nx-1).y)
    }
    f(0 ,0) = 0.5f*(f(1,0 )+f(0 ,1));
    f(0 ,f.nx-2) = 0.5f*(f(1,f.nx-2)+f(0 ,f.nx-1 ));
    f(f.nx-2,0 ) = 0.5f*(f(f.nx-1,0 )+f(f.nx-2,1));
    f(f.nx-2,f.nx-2) = 0.5f*(f(f.nx-1,f.nx-2)+f(f.nx-2,f.nx-1 ));
  }

  def diffuse(f:VecField2D) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy)
    val tmp = scratch(f)
    
    for (y <- ( 1 until f.ny-1 ); x <- ( 1 until f.nx-1 )){
      var fxy = f(x,y)
      tmp(x,y) = fxy + a * ( -4f * fxy + f(x+1,y) + f(x-1,y) + f(x,y+1) + f(x,y-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:VecField2D, dt:Float, b:Int=0, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+4f*a), b, itr)
  }

  def linearSolve(f0:VecField2D, a:Float, a2:Float, b:Int, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ k =>
      cfor(1)(_ < f0.ny-1, _ + 1){ j => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ i => //i <- 1 until f0.nx-1;
        f(i,j) = ( f0(i,j) + a*( f(i-1,j) + f(i+1,j) + f(i,j-1) + f(i,j+1) ) ) * a2;
      }}
      setBoundary (f, b);
    }
    f0.set( f )
    f0
  }

  def advect(f0:VecField2D, vf:VecField2D, dt:Float, b:Int) = {
    val f = scratch(f0)
    val dt0 = dt*f0.nx //N;

    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      val v = vf(i,j)
      var x = i - dt0 * v.x
      var y = j - dt0 * v.y
      if (x < 0.5) x = 0.5f; if (x > f0.nx + 0.5) x = f0.nx + 0.5f; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5f; if (y > f0.ny + 0.5) y = f0.ny + 0.5f; val j0=y.toInt; val j1=j0+1;
      val s1 = x-i0; val s0 = 1-s1; val t1 = y-j0; val t0 = 1-t1;
      f(i,j) = s0 * (t0 * f0(i0,j0) + t1 * f0(i0,j1) ) + s1 * ( t0 * f0(i1,j0) + t1 * f0(i1,j1) )
    }
    setBoundary(f,b)
    f0.set( f )
    f0
  }

  def project(vf:VecField2D) = {
  // void project ( int N, float * u, float * v, float * p, float * div )
    val f = scratch(vf) // (p,div) ?

    val h = 1f/vf.nx;
    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      // div(i,j) = -0.5*h*(u(i+1,j)-u(i-1,j)+v(i,j+1)-v(i,j-1));
      val div = -0.5f * h * ( vf(i+1,j).x - vf(i-1,j).x + vf(i,j+1).y - vf(i,j-1).y );
      f(i,j) = Vec2(0,div)
    }
    setBoundary (f, 0)
    for (k <- 0 until 20 ){
      for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
        f(i,j).x = ( f(i,j).y + f(i-1,j).x + f(i+1,j).x + f(i,j-1).x + f(i,j+1).x ) / 4f;
      }
      setBoundary (f, 0);
    }
    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      vf(i,j).x -= 0.5f * ( f(i+1,j).x - f(i-1,j).x ) / h;
      vf(i,j).y -= 0.5f * ( f(i,j+1).x - f(i,j-1).x ) / h;
    }
    setBoundary (f,1); //setBoundary ( N, 2, v );
    // f.set( vf )
    vf
  }
}

class VecField2D(val nx:Int, val ny:Int, val dx:Float=0.1f, val dy:Float=0.1f){
  val data:Array[Vec2] = Array.fill(nx*ny)(Vec2())

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int) = data(y*nx + x)

  // interpolate 
  def apply( v:Vec2 ):Vec2 = {
    val cen = Vec2()
    val halfsize = nx*dx / 2  //XXX

    if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize) return Vec2(0)
    val nv = ((v-cen) + Vec2(halfsize)) * (nx-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    apply(nv.x,nv.y)
  }

  def apply(x:Float, y:Float):Vec2 = {
    val x1 = x.floor.toInt; val dx = x - x1
    val y1 = y.floor.toInt; val dy = y - y1
    var vx1 = this(x1,y1).lerp( this(x1+1,y1), dx )
    var vx2 = this(x1,y1+1).lerp( this(x1+1,y1+1), dx )
    vx1.lerp( vx2, dy)
  } 

  def update(x:Int, y:Int, value:Vec2):Unit = data(y*nx + x) = value

  def update(v:Vec2, value:Vec2):Unit = {
    val cen = Vec2()
    val halfsize = nx*dx / 2  //XXX

    if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize) return
    val nv = ((v-cen) + Vec2(halfsize)) * (nx-1) / (2*halfsize)
    //println( "vecfield3d get: " + nv )
    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    update(nv.x.toInt, nv.y.toInt, value)
    update(nv.x.toInt+1, nv.y.toInt, value)
    update(nv.x.toInt, nv.y.toInt+1, value)
    update(nv.x.toInt+1, nv.y.toInt+1, value)
  }


  def set(f:VecField2D) = Array.copy(f.data,0,data,0,data.length)

  def getCenter(x:Int,y:Int):Vec2 = {
    Vec2(x*dx,y*dy) + Vec2(dx,dy)*0.5f
  }
}