
package com.fishuyo.seer
package spatial


object Matrix {

  def apply(n:Int) = new Matrix(n)
  def apply(data:Float*) = {
    val n = math.sqrt(data.length).ceil.toInt
    val m = new Matrix(n)
    m.set(data: _*)
    m
  }

  def apply(matrix:Matrix) = {
    val m = new Matrix(matrix.n)
    m.set(matrix)
    m
  }

  def identity(n:Int) = {
    val m = new Matrix(n)
    m.setIdentity
    m
  }

  def rotation(angle:Double, dim1:Int, dim2:Int, n:Int=4) = {
    val cs = math.cos(angle).toFloat
    val sn = math.sin(angle).toFloat
    val m = identity(n)
    m(dim1,dim1) = cs
    m(dim1,dim2) = -sn
    m(dim2,dim1) = sn
    m(dim2,dim2) = cs
    m
  }

  def scaling(v:Float, n:Int=4) = {
    val m = new Matrix(n)
    for(i <- 0 until n-1) m(i,i) = v
    m(n-1,n-1) = 1f
    m 
  }
  def scaling(v:Vec3):Matrix = scaling(v.x,v.y,v.z)
  def scaling(list:Float*) = {
    val n = list.length + 1
    val m = new Matrix(n)
    for(i <- 0 until n - 1) m(i,i) = list(i)
    m(n-1,n-1) = 1f
    m
  }

  def translation(v:Vec3):Matrix = translation(v.x,v.y,v.z)
  def translation(list:Float*) = {
    val n = list.length + 1
    val m = identity(n)
    for(i <- 0 until n - 1) m(i,n-1) = list(i)
    m
  }

  def multiply(r:Matrix, a:Matrix, b:Matrix) = {
    val n = r.n
    for(j <- 0 until n; i <- 0 until n){
      var sum = 0f
      for(t <- 0 until n) sum += a(i,t) * b(t,j)
      r(i,j) =  sum
    }
    r
  }

  def multiply(a:Matrix, b:Array[Float]) = {
    val n = a.n
    val r = new Array[Float](n)
    for(i <- 0 until n){
      var sum = 0f
      for(t <- 0 until n) sum += a(i,t) * b(t)
      r(i) =  sum
    }
    r
  }

  def multiply(a:Array[Float], b:Matrix) = {
    val n = b.n
    val r = new Array[Float](n)
    for(j <- 0 until n){
      var sum = 0f
      for(t <- 0 until n) sum += a(t) * b(t,j)
      r(j) =  sum
    }
    r
  }

}


// 0 4 8 12
// 1 5 9 13
// 2 6 10 14
// 3 7 11 15

/**
 * 2D square Matrix with n by n elements
 */
class Matrix(val n:Int) {

  /** data stored column major */ 
  val data = new Array[Float](n*n)  

  def size = n*n  

  /** Get element ith column major order **/
  def apply(i:Int) = data(i)
  def update(i:Int, v:Float) = data(i) = v

  /** Get element at row i column j */
  def apply(i:Int, j:Int) = data(j*n + i)  
  def update(i:Int, j:Int, v:Float) = data(j*n + i) = v 

  /** Set elements from list of Floats column major order */
  def set(list:Float*) = { list.take(size).copyToArray(data,0); this }
  
  def set(v:Float) = { for(i <- 0 until size) data(i) = v; this }
  def set(m:Matrix) = { m.data.copyToArray(data); this }

  def setIdentity() = {
    for(i <- 0 until n) data(i*(n+1)) = 1f
    for(i <- 0 until n-1; j <- i+1 until n+i+1) data(i*n + j) = 0f
    this
  }

  def transpose() = {
    for(j <- 0 until n-1; off <- j+1 until n){ // row and column
      val a = this(off,j)
      val b = this(j,off)
      this(off,j) = b
      this(j,off) = a
    }
    this
  }

  def +=(m:Matrix) = { for(i <- 0 until size) data(i) += m(i); this }
  def -=(m:Matrix) = { for(i <- 0 until size) data(i) -= m(i); this }
  def *=(m:Matrix) = Matrix.multiply(this, Matrix(this), m)

  def +=(v:Float) = { for(i <- 0 until size) data(i) += v; this }
  def -=(v:Float) = { for(i <- 0 until size) data(i) -= v; this }
  def *=(v:Float) = { for(i <- 0 until size) data(i) *= v; this }
  def /=(v:Float) = { for(i <- 0 until size) data(i) /= v; this }

  def unary_-() = { val m = Matrix(n); for(i <- 0 until size) m(i) = -data(i); m }
  def +(m:Matrix) = Matrix(this) += m
  def -(m:Matrix) = Matrix(this) += m
  def *(m:Matrix) = Matrix.multiply(Matrix(this), this, m)
  def +(v:Float) = Matrix(this) += v
  def -(v:Float) = Matrix(this) -= v
  def *(v:Float) = Matrix(this) *= v
  def /(v:Float) = Matrix(this) /= v


  def submatrix(row:Int, col:Int) = {
    val m = Matrix(n-1)
    var js = 0
    for(j <- 0 until n-1){ 
      js += (if (js==row) 1 else 0)
      var is = 0
      for(i <- 0 until n-1){
        is += (if (is==col) 1 else 0)
        m(j,i) = this(js,is)
        is += 1
      }
      js += 1
    }
    m
  }

  def cofactor(row:Int, col:Int):Float = {
    val minor = submatrix(row,col).determinant()
    val cofactors = Array(minor, -minor)
    // cofactor sign: + if row+col even, - otherwise
    val sign = (row^col) & 1;
    cofactors(sign)
  }

  def cofactorMatrix() = {
    val m = Matrix(n)
    for(r <- 0 until n; c <- 0 until n){
      m(r,c) = cofactor(r,c)
    }
    m
  }

  def trace():Float = (0 until n).map(i => this(i,i)).sum
  
  def determinant():Float = {
    val m = this
    n match {
      case 1 => m(0,0)
      case 2 => m(0,0)*m(1,1) - m(0,1)*m(1,0)
      case 3 => 
        m(0,0)*(m(1,1)*m(2,2) - m(1,2)*m(2,1)) +
        m(0,1)*(m(1,2)*m(2,0) - m(1,0)*m(2,2)) +
        m(0,2)*(m(1,0)*m(2,1) - m(1,1)*m(2,0))
      case _ =>
        var res = 0f
        for(i <- 0 until n){
          val v = m(0,i)
          if(v != 0f) res += v * cofactor(0,i)
        }
        res
    }
  }

  def invert():Option[Matrix] = {
    // println("this:\n" + this)

    // Get cofactor matrix, C
    val C = cofactorMatrix()
    // println("co:\n" + C)

    // Compute determinant
    var det = 0f
    for(i <- 0 until n) det += this(0,i) * C(0,i)
    // println("det:\n" + det)
    // Divide adjugate matrix, C^T, by determinant
    if(det != 0f){
      val m = (C.transpose() *= 1/det)
      set(m)
      return Some(this)
    }
    return None
  }

  override def toString = data.sliding(n,n).map(_.mkString(" ")).mkString("\n") 
}

