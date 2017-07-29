
package com.fishuyo.seer
package spatial


object Mat4 {

  def apply() = new Mat4

}

class Mat4 extends Matrix(4) {

  def toQuat() = Quat().fromMatrix(this)
  def fromQuat(q:Quat) = q.toMatrix(this)

  def transform(v:Vec3, w:Float) = {
    val r = Matrix.multiply(this, Array(v.x,v.y,v.z,w))
    Vec3(r(0),r(1),r(2))
  }


}