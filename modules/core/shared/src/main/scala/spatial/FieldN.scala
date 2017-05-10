
// package com.fishuyo.seer
// package spatial

// import util.lerp

// // import spire.syntax.cfor._
// import spire.math._
// import spire.algebra._
// import spire.implicits._

// /**
//   * A Field represents data grids in n-dimensional space. 
//   * Individual cells can be accesed as well as interpolation between cells through lookup at a position in space.
//   */

// class Field[T: spire.math.Numeric : ClassManifest](val dims:Seq[Int]) {
//   val ndims = dims.length
//   val size = dims.fold(1)(_*_)
//   val data = Array.fill(size)(implicitly[spire.math.Numeric[T]].zero)

//   def nx = dims.head
//   def ny = dims(1)

//   val dx,dy = 0.02f

//   def apply(i:Int):T = data(i)
//   def apply(i:Int,j:Int):T = data(nx*j+i)
//   def update(i:Int, value:T) = data(i) = value
//   def update(i:Int, j:Int, value:T) = data(nx*j+i) = value
//   def set(f:Field[T]) = Array.copy(f.data,0,data,0,data.length)

//   // interpolated
//   def apply(_x:Float):T = {
//     var x = _x
//     if( x >= nx-1 ) x = nx - 1.001f
//     val x0 = x.toInt; val x1 = x0 + 1; val dx = x - x0
//     lerp[T](data(x0),data(x1),dx)
//   } 
//   def apply(x:Float, y:Float):T = {
//     val x1 = x.toInt; val dx = x - x1
//     val y1 = y.toInt; val dy = y - y1
//     var vx1 = lerp(this(x1,y1), this(x1+1,y1), dx )
//     var vx2 = lerp(this(x1,y1+1), this(x1+1,y1+1), dx )
//     lerp(vx1, vx2, dy)
//   } 
//   def update(x:Float, value:T):Unit = data(x.round) = value
// }

// class FloatField(override val dims:Seq[Int]) extends Field[Float](dims)
// // class Vec3Field(override val dims:Seq[Int]) extends Field[Vec3](dims)
// class Vec2Field(override val dims:Seq[Int]) extends Field[Vec2](dims) {

// // interpolate 
//   def apply( v:Vec2 ):Vec2 = {
//     val cen = Vec2()
//     val halfsize = nx*dx / 2  //XXX

//     if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize) return Vec2(0)
//     val nv = ((v-cen) + Vec2(halfsize)) * (nx-1) / (2*halfsize)
//     //println( "vecfield3d get: " + nv )
//     if( nv.x >= nx-1 ) nv.x = nx - 1.001f
//     if( nv.y >= ny-1 ) nv.y = ny - 1.001f
//     apply(nv.x,nv.y)
//   }

//   // def update(x:Int, y:Int, value:Vec2):Unit = data(y*nx + x) = value

//   def update(v:Vec2, value:Vec2):Unit = {
//     val cen = Vec2()
//     val halfsize = nx*dx / 2  //XXX

//     if( v.x < -halfsize || v.x > halfsize || v.y > halfsize || v.y < -halfsize) return
//     val nv = ((v-cen) + Vec2(halfsize)) * (nx-1) / (2*halfsize)
//     //println( "vecfield3d get: " + nv )
//     if( nv.x >= nx-1 ) nv.x = nx - 1.001f
//     if( nv.y >= ny-1 ) nv.y = ny - 1.001f
//     update(nv.x.toInt, nv.y.toInt, value)
//     update(nv.x.toInt+1, nv.y.toInt, value)
//     update(nv.x.toInt, nv.y.toInt+1, value)
//     update(nv.x.toInt+1, nv.y.toInt+1, value)
//   }

//   def getCenter(x:Int,y:Int):Vec2 = {
//     Vec2(x*dx,y*dy) + Vec2(dx,dy)*0.5f
//   }

// }



// // /**
// //   * a 2D Field distributed through a rectangle in space
// //   */
// class Field2D(nx:Int, ny:Int) extends Field[Float](Seq(nx,ny))
// class VecField2D(nx:Int, ny:Int) extends Vec2Field(Seq(nx,ny))



// object Field {

//   implicit object Vec2IsNumeric extends spire.math.Numeric[Vec2] {
//        // Members declared in spire.algebra.AdditiveGroup
//   override def fromInt(n: Int): Vec2 = Vec2(n.toFloat)
//   override def fromDouble(n: Double): Vec2 = Vec2(n.toFloat)
//   override def fromBigInt(n: BigInt): Vec2 = Vec2(n.toFloat)
//   override def toDouble(n: Vec2): Double = n.mag.toDouble
//   // override def toRational(n: Float): Rational = super[FloatIsReal].toRational(n)
//   // override def toAlgebraic(n: Float): Algebraic = super[FloatIsReal].toAlgebraic(n)
//   // override def toReal(n: Float): Real = super[FloatIsReal].toReal(n)
//     def negate(x:Vec2):Vec2 = -x

//     // Members declared in spire.algebra.AdditiveMonoid
//     def zero:Vec2 = Vec2(0)

//     // Members declared in spire.algebra.AdditiveSemigroup
//     def plus(x:Vec2,y:Vec2):Vec2 = x + y

//     // Members declared in spire.algebra.EuclideanRing
//     def gcd(a:Vec2,b:Vec2):Vec2 = a*b
//     def mod(a:Vec2,b:Vec2):Vec2 = a/b
//     def quot(a:Vec2,b:Vec2):Vec2 = a/b

//     // Members declared in spire.algebra.MultiplicativeGroup
//     def div(x:Vec2,y:Vec2):Vec2 = x/y

//     // Members declared in spire.algebra.MultiplicativeMonoid
//     def one:Vec2 = Vec2(1)

//     // Members declared in spire.algebra.MultiplicativeSemigroup
//     def times(x:Vec2,y:Vec2):Vec2 = x*y
//   }

//   def scratchMap[T] = collection.mutable.HashMap[Field[T],Field[T]]()
//   def scratch[T: spire.math.Numeric : ClassManifest](f:Field[T]) = scratchMap[T].getOrElseUpdate(f, new Field[T](f.dims))

//   def diffuse[T: spire.math.Numeric : ClassManifest](f:Field[T]) = {
//     val dt = 0.15f
//     val alphaA = 0.75f
//     val a = dt * alphaA // / (f.dx*f.dy)
//     val tmp = scratch(f)
    
//     for (y <- ( 1 until f.ny-1 ); x <- ( 1 until f.nx-1 )){
//       var fxy = f(x,y)
//       tmp(x,y) = fxy + a * ( -4f * fxy + f(x+1,y) + f(x-1,y) + f(x,y+1) + f(x,y-1) )
//     }

//     f.set( tmp )
//     f
//   }

//   def diffuseSolve[T: spire.math.Numeric : ClassManifest](f0:Field[T], dt:Float, itr:Int=20) = {
//     val diff = 0.75f
//     val a = dt * diff //* f0.nx * f0.ny
//     linearSolve(f0, a, 1f/(1f+4f*a), 20)
//   }

//   def linearSolve[T: spire.math.Numeric : ClassManifest](f0:Field[T], a:Float, b:Float, itr:Int) = {
//     val f = scratch(f0)

//     cfor(0)(_ < itr, _ + 1){ k =>
//       cfor(1)(_ < f0.ny-1, _ + 1){ j => //     j <- 1 until f0.ny-1 ) {
//       cfor(1)(_ < f0.nx-1, _ + 1){ i => //i <- 1 until f0.nx-1;
//         f(i,j) = ( f0(i,j) + a*( f(i-1,j) + f(i+1,j) + f(i,j-1) + f(i,j+1) ) ) * b;
//       }}
//       // set_bnd ( N, b, x );
//     }
//     f0.set( f )
//     f0
//   }

//   def advect[T: spire.math.Numeric : ClassManifest](f0:Field[T], vf:Vec2Field, dt:Float) = {
//     val f = scratch(f0)
//     val dt0 = dt*f0.nx //N;

//     for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
//       val v = vf(i,j)
//       var x = i - dt0 * v.x
//       var y = j - dt0 * v.y
//       if (x < 0.5) x = 0.5; if (x > f0.nx + 0.5) x = f0.nx + 0.5; val i0=x.toInt; val i1=i0+1;
//       if (y < 0.5) y = 0.5; if (y > f0.ny + 0.5) y = f0.ny + 0.5; val j0=y.toInt; val j1=j0+1;
//       val s1 = x-i0; val s0 = 1-s1; val t1 = y-j0; val t0 = 1-t1;
//       // f(i,j) = s0 * (t0 * f0(i0,j0) + t1 * f0(i0,j1) ) + s1 * ( t0 * f0(i1,j0) + t1 * f0(i1,j1) )
//       f(i,j) = lerp( lerp( f0(i0,j0), f0(i0,j1), t1 ), lerp( f0(i1,j0), f0(i1,j1), t1 ), s1)
//     }
//     // set_bnd ( N, b, d );
//     f0.set( f )
//     f0
//   }

//   def project(vf:Vec2Field) = {
//   // void project ( int N, float * u, float * v, float * p, float * div )
//     val f = scratch(vf) // (p,div) ?

//     val h = 1f/vf.nx;
//     for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
//       // div(i,j) = -0.5*h*(u(i+1,j)-u(i-1,j)+v(i,j+1)-v(i,j-1));
//       val div = -0.5f * h * ( vf(i+1,j).x - vf(i-1,j).x + vf(i,j+1).y - vf(i,j-1).y );
//       f(i,j) = Vec2(0,div)
//     }
//     // set_bnd ( N, 0, div ); set_bnd ( N, 0, p );
//     for (k <- 0 until 20 ){
//       for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
//         f(i,j).x = ( f(i,j).y + f(i-1,j).x + f(i+1,j).x + f(i,j-1).x + f(i,j+1).x ) / 4f;
//       }
//       // set_bnd ( N, 0, p );
//     }
//     for (j <- ( 1 until f.ny-1 ); i <- ( 1 until f.nx-1 )){
//       vf(i,j).x -= 0.5f * ( f(i+1,j).x - f(i-1,j).x ) / h;
//       vf(i,j).y -= 0.5f * ( f(i,j+1).x - f(i,j-1).x ) / h;
//     }
//     // set_bnd ( N, 1, u ); set_bnd ( N, 2, v );
//     // f.set( vf )
//     vf
//   }

// }

