
package seer
package world
package field

import spatial.Vec2
import util._

import spire.syntax.cfor._


/**
  * A Field represents data grids in n-dimensional space. 
  * Individual cells can be accesed as well as interpolation between cells through lookup at a position in space.
  */
object Field2 {

  val scratchMap = collection.mutable.HashMap[(Int,Int),Field2]()
  def scratch(f:Field2) = scratchMap.getOrElseUpdate((f.nx,f.ny), Field2(f.nx,f.ny,f.dx,f.dy))
  def scratch(nx:Int,ny:Int,dx:Float,dy:Float) = scratchMap.getOrElseUpdate((nx,ny), Field2(nx,ny,dx,dy))

  def apply(nx:Int, ny:Int, dx:Float=1f, dy:Float=1f) = new Field2(nx,ny,dx,dy)
  def apply(n:Vec2, size:Vec2) = new Field2(n.x.toInt,n.y.toInt,size.x,size.y)

  def setBoundary(f:Field2) = {
    for ( j <- 1 until f.ny - 1 ) {
      f(0,j) = f(1,j);
      f(f.nx-1,j) = f(f.nx-2,j);
    }
    for ( i <- 1 until f.nx - 1 ) {
      f(i,0 ) = f(i,1);
      f(i,f.ny-1) = f(i,f.ny-2);
    }
    f(0 ,0 ) = 0.5f*(f(1,0)+f(0,1));
    f(0 ,f.ny-1) = 0.5f*(f(1,f.ny-1)+f(0,f.ny-2 ));
    f(f.nx-1,0 ) = 0.5f*(f(f.nx-2,0 )+f(f.nx-1,1));
    f(f.nx-1,f.ny-1) = 0.5f*(f(f.nx-1,f.ny-2)+f(f.nx-2,f.ny-1 ));
  }

  def diffuse(f:Field2) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy)
    val tmp = scratch(f)
    
    for (y <- ( 1 until f.ny-1 ); x <- ( 1 until f.nx-1 )){
      var fxy = f(x,y)
      tmp(x,y) = fxy + a * ( -4f * fxy + f(x+1,y) + f(x-1,y) + f(x,y+1) + f(x,y-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:Field2, dt:Float, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+4f*a), itr)
  }

  def linearSolve(f0:Field2, a:Float, a2:Float, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ k =>
      cfor(1)(_ < f0.ny-1, _ + 1){ j => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ i => //i <- 1 until f0.nx-1;
        f(i,j) = ( f0(i,j) + a*( f(i-1,j) + f(i+1,j) + f(i,j-1) + f(i,j+1) ) ) * a2;
      }}
      setBoundary(f);
    }
    f0.set( f )
    f0
  }

  def advect(f0:Field2, vf:VecField2, dt:Float) = {
    val f = scratch(f0)
    val dt0x = dt*(f0.nx-2)
    val dt0y = dt*(f0.ny-2)

    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      val v = vf(i,j)
      var x = i - dt0x * v.x
      var y = j - dt0y * v.y
      if (x < 0.5) x = 0.5f; if (x > f0.nx-2 + 0.5) x = f0.nx-2 + 0.5f; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5f; if (y > f0.ny-2 + 0.5) y = f0.ny-2 + 0.5f; val j0=y.toInt; val j1=j0+1;
      val s1 = x-i0; val s0 = 1-s1; val t1 = y-j0; val t0 = 1-t1;
      f(i,j) = s0 * (t0 * f0(i0,j0) + t1 * f0(i0,j1) ) + s1 * ( t0 * f0(i1,j0) + t1 * f0(i1,j1) )
    }
    setBoundary(f);
    f0.set( f )
    f0
  }

}

class Field2(val nx:Int, val ny:Int, val dx:Float=1f, val dy:Float=1f){
  val data = new Array[Float](nx*ny)

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int) = data(y*nx + x)

  // interpolate 
  def apply( v:Vec2 ):Float = {
    val cen = Vec2()
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return 0f
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))

    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    apply(nv.x,nv.y)
  }

  def apply(x:Float, y:Float):Float = {
    val x1 = x.floor.toInt; val dx = x - x1
    val y1 = y.floor.toInt; val dy = y - y1
    var vx1 = spatial.lerp(this(x1,y1), this(x1+1,y1), dx )
    var vx2 = spatial.lerp(this(x1,y1+1), this(x1+1,y1+1), dx )
    spatial.lerp(vx1, vx2, dy)
  } 

  def update(x:Int, y:Int, value:Float) = data(y*nx + x) = value

  def update(v:Vec2, value:Float):Unit = {
    val cen = Vec2()
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))

    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    update(nv.x.toInt, nv.y.toInt, value)
    update(nv.x.toInt+1, nv.y.toInt, value)
    update(nv.x.toInt, nv.y.toInt+1, value)
    update(nv.x.toInt+1, nv.y.toInt+1, value)
  }


  def set(f:Field2) = Array.copy(f.data,0,data,0,data.length)

  def getCenter(x:Int,y:Int):Vec2 = {
    Vec2(x*dx,y*dy) + Vec2(dx,dy)*0.5f
  }

  def getClosestCell(v:Vec2):(Int,Int) = {
    val cen = Vec2()
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return (0,0)
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))

    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    (nv.x.floor.toInt, nv.y.floor.toInt)
  }
}


object VecField2 {
  implicit def f2v(f:Float) = Vec2(f)  // XXX

  val scratchMap = collection.mutable.HashMap[(Int,Int),VecField2]()
  def scratch(f:VecField2) = scratchMap.getOrElseUpdate((f.nx,f.ny), VecField2(f.nx,f.ny,f.dx,f.dy))

  def apply(nx:Int, ny:Int, dx:Float=0.1f, dy:Float=0.1f) = new VecField2(nx,ny,dx,dy)
  def apply(n:Vec2, size:Vec2) = new VecField2(n.x.toInt,n.y.toInt,size.x,size.y)

  def setBoundary(f:VecField2) = {

    for ( i <- 1 until f.ny-1 ) {
      f(0 ,i).x = -f(1,i).x
      f(0 ,i).y = f(1,i).y
      f(f.nx-1,i).x = -f(f.nx-2,i).x
      f(f.nx-1,i).y = f(f.nx-2,i).y
    }
    for ( i <- 1 until f.nx-1 ) {
      f(i,0).x = f(i,1).x
      f(i,0).y = -f(i,1).y
      f(i,f.ny-1).x = f(i,f.ny-2).x
      f(i,f.ny-1).y = -f(i,f.ny-2).y
    }
    f(0,0) = -0.5f*(f(1,0)+f(0,1));
    f(0,f.ny-1) = -0.5f*(f(1,f.ny-1)+f(0,f.ny-2));
    f(f.nx-1,0) = -0.5f*(f(f.nx-2,0)+f(f.nx-1,1));
    f(f.nx-1,f.ny-1) = -0.5f*(f(f.nx-1,f.ny-2)+f(f.nx-2,f.ny-1 ));
  }

  def diffuse(f:VecField2) = {
    val a = f.dt * f.alphaA / (f.dx*f.dy)
    val tmp = scratch(f)
    
    for (y <- ( 1 until f.ny-1 ); x <- ( 1 until f.nx-1 )){
      var fxy = f(x,y)
      tmp(x,y) = fxy + a * ( -4f * fxy + f(x+1,y) + f(x-1,y) + f(x,y+1) + f(x,y-1) )
    }

    f.set( tmp )
    f
  }

  def diffuseSolve(f0:VecField2, dt:Float, itr:Int=20) = {
    val diff = 0.75f
    val a = dt * diff //* f0.nx * f0.ny
    linearSolve(f0, a, 1f/(1f+4f*a), itr)
  }

  def linearSolve(f0:VecField2, a:Float, a2:Float, itr:Int) = {
    val f = scratch(f0)

    cfor(0)(_ < itr, _ + 1){ k =>
      cfor(1)(_ < f0.ny-1, _ + 1){ j => //     j <- 1 until f0.ny-1 ) {
      cfor(1)(_ < f0.nx-1, _ + 1){ i => //i <- 1 until f0.nx-1;
        f(i,j) = ( f0(i,j) + a*( f(i-1,j) + f(i+1,j) + f(i,j-1) + f(i,j+1) ) ) * a2;
      }}
      setBoundary(f);
    }
    f0.set( f )
    f0
  }

  def advect(f0:VecField2, vf:VecField2, dt:Float) = {
    val f = scratch(f0)
    val dt0x = dt*(f0.nx-2)
    val dt0y = dt*(f0.ny-2)

    for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
      val v = vf(i,j)
      var x = i - dt0x * v.x
      var y = j - dt0y * v.y
      if (x < 0.5) x = 0.5f; if (x > f0.nx-2 + 0.5) x = f0.nx-2 + 0.5f; val i0=x.toInt; val i1=i0+1;
      if (y < 0.5) y = 0.5f; if (y > f0.ny-2 + 0.5) y = f0.ny-2 + 0.5f; val j0=y.toInt; val j1=j0+1;
      val s1 = x-i0; val s0 = 1-s1; val t1 = y-j0; val t0 = 1-t1;
      f(i,j) = s0 * (t0 * f0(i0,j0) + t1 * f0(i0,j1) ) + s1 * ( t0 * f0(i1,j0) + t1 * f0(i1,j1) )
    }
    setBoundary(f)
    f0.set( f )
    f0
  }

  def project(f0:VecField2) = {
    val p = Field2.scratch(f0.nx,f0.ny,f0.dx,f0.dy)
    val div = Field2.scratch(f0.nx,f0.ny,f0.dx,f0.dy)

    val h = 1f/f0.nx; //XXX
    for (j <- ( 1 until f0.ny-1 ); i <- ( 1 until f0.nx-1 )){
      val d = -0.5f * h * ( f0(i+1,j).x - f0(i-1,j).x + f0(i,j+1).y - f0(i,j-1).y );
      div(i,j) = d
      p(i,j) = 0f
    }
    Field2.setBoundary(div); Field2.setBoundary(p)
    for (k <- 0 until 20 ){
      for (j <- ( 1 until f0.ny-1 ); i <- ( 1 until f0.nx-1 )){
        p(i,j) = ( div(i,j) + p(i-1,j) + p(i+1,j) + p(i,j-1) + p(i,j+1) ) / 4f;
      }
      Field2.setBoundary(p);
    }
    for (j <- ( 1 until f0.ny-1 ); i <- ( 1 until f0.nx-1 )){
      f0(i,j).x -= 0.5f * ( p(i+1,j) - p(i-1,j) ) / h;
      f0(i,j).y -= 0.5f * ( p(i,j+1) - p(i,j-1) ) / h;
    }
    setBoundary(f0);
    f0
  }
}

class VecField2(val nx:Int, val ny:Int, val dx:Float=0.1f, val dy:Float=0.1f){
  val data:Array[Vec2] = Array.fill(nx*ny)(Vec2())

  var alphaA = .75f
  // var alphaB = 0f
  // val dx = 1f //165f / 200f //Main.n
  var dt = .15f //.02f

  def apply(x:Int, y:Int) = data(y*nx + x)

  // interpolate 
  def apply( v:Vec2 ):Vec2 = {
    val cen = Vec2()
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return Vec2(0)
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))
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
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))
    //println( "vecfield3d get: " + nv )
    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    update(nv.x.toInt, nv.y.toInt, value)
    update(nv.x.toInt+1, nv.y.toInt, value)
    update(nv.x.toInt, nv.y.toInt+1, value)
    update(nv.x.toInt+1, nv.y.toInt+1, value)
  }


  def set(f:VecField2) = Array.copy(f.data,0,data,0,data.length)

  def getCenter(x:Int,y:Int):Vec2 = {
    Vec2(x*dx,y*dy) + Vec2(dx,dy)*0.5f
  }

  def getClosestCell(v:Vec2):(Int,Int) = {
    val cen = Vec2()
    val s = Vec2(nx*dx / 2, ny*dy / 2)

    if( v.x < -s.x || v.x > s.x || v.y > s.y || v.y < -s.y) return (0,0)
    val nv = ((v-cen) + s) * (Vec2(nx-1,ny-1) / (s*2))

    if( nv.x >= nx-1 ) nv.x = nx - 1.001f
    if( nv.y >= ny-1 ) nv.y = ny - 1.001f
    (nv.x.floor.toInt, nv.y.floor.toInt)
  }
}