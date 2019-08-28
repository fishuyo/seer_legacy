
package com.fishuyo.seer
package spatial

// partial porting from allolib
// see https://github.com/AlloSphere-Research-Group/allolib.git

object Mat4 {

  def apply() = new Mat4


    /// Get a perspective projection matrix

  /// @param[in] l  distance from center of near plane to left edge
  /// @param[in] r  distance from center of near plane to right edge
  /// @param[in] b  distance from center of near plane to bottom edge
  /// @param[in] t  distance from center of near plane to top edge
  /// @param[in] n  distance from eye to near plane
  /// @param[in] f  distance from eye to far plane
  def perspective(l:Float, r:Float, b:Float, t:Float, n:Float, f:Float) = {
    val W = r-l;  val W2 = r+l;
    val H = t-b;  val H2 = t+b;
    val D = f-n;  val D2 = f+n;
    val n2 = n*2;
    val fn2 = f*n2;
    
    Mat4().set(  n2/W,  0,    W2/W,    0,
            0,    n2/H,  H2/H,    0,
            0,    0,    -D2/D,    -fn2/D,
            0,    0,    -1,      0 );
  }

  /// Get a perspective projection matrix

  /// @param[in] fovy    field of view angle, in degrees, in the y direction
  /// @param[in] aspect  aspect ratio
  /// @param[in] near    distance from eye to near plane
  /// @param[in] far    distance from eye to far plane
  def perspective(fovy:Float, aspect:Float, near:Float, far:Float) = {
    val f = (1.0/math.tan(fovy*Pi/180f/2.0f)).toFloat
    val D = far-near;  val D2 = far+near;
    val fn2 = far*near*2;
    Mat4().set(  f/aspect,  0,  0,      0,
            0,      f,  0,      0,
            0,      0,  -D2/D,    -fn2/D,
            0,      0,  -1,      0
    );
  }

  // /// Calculate perspective projection from near plane and eye coordinates

  // /// (nearBL, nearBR, nearTL, eye) all share the same coordinate system
  // /// (nearBR,nearBL) and (nearTL,nearBL) should form a right angle
  // /// (eye) can be set freely, allowing diverse off-axis projections
  // /// @see Generalized Perspective Projection, Robert Kooima, 2009, EVL
  // /// @param[in] nearBL  bottom-left near-plane coordinate (world-space)
  // /// @param[in] nearBR  bottom-right near-plane coordinate (world-space)
  // /// @param[in] nearTL  top-left near-plane coordinate (world-space)
  // /// @param[in] eye    eye coordinate (world-space)
  // /// @param[in] near    near plane distance from eye
  // /// @param[in] far    far plane distance from eye
  // def perspective(
  //               const Vec<3,T>& nearBL,
  //               const Vec<3,T>& nearBR,
  //               const Vec<3,T>& nearTL,
  //               const Vec<3,T>& eye,
  //               T near,  T far)
  // {
  //   Vec<3,T> va, vb, vc;
  //   Vec<3,T> vr, vu, vn;
  //   T l, r, b, t, d;

  //   // compute orthonormal basis for the screen
  //   vr = (nearBR-nearBL).normalize();  // right vector
  //   vu = (nearTL-nearBL).normalize();  // up vector
  //   cross(vn, vr, vu);  // normal(forward) vector (out from screen)
  //   vn.normalize();

  //   // compute vectors from eye to screen corners:
  //   va = nearBL-eye;
  //   vb = nearBR-eye;
  //   vc = nearTL-eye;

  //   // distance from eye to screen-plane
  //   // = component of va along vector vn (normal to screen)
  //   d = -va.dot(vn);

  //   // find extent of perpendicular projection
  //   T nbyd = near/d;
  //   l = vr.dot(va) * nbyd;
  //   r = vr.dot(vb) * nbyd;
  //   b = vu.dot(va) * nbyd;  // not vd?
  //   t = vu.dot(vc) * nbyd;

  //   return perspective(l, r, b, t, near, far);
  // }

  // /// Get a left-eye perspective projection matrix (for stereographics)
  // def perspectiveLeft(T fovy, T aspect, T near, T far, T eyeSep, T focal) {
  //   return perspectiveOffAxis(fovy, aspect, near, far,-0.5*eyeSep, focal);
  // }

  // /// Get a right-eye perspective projection matrix (for stereographics)
  // def perspectiveRight(T fovy, T aspect, T near, T far, T eyeSep, T focal) {
  //   return perspectiveOffAxis(fovy, aspect, near, far, 0.5*eyeSep, focal);
  // }

  // /// Get an off-axis perspective projection matrix (for stereographics)
  // def perspectiveOffAxis(T fovy, T aspect, T near, T far, T xShift, T focal) {
  //   T top = near * tan(fovy*M_DEG2RAD*0.5);  // height of view at distance = near
  //   T bottom = -top;
  //   T shift = -xShift*near/focal;
  //   T left = -aspect*top + shift;
  //   T right = aspect*top + shift;
  //   return perspective(left, right, bottom, top, near, far);
  // }

  /// Get an off-axis perspective projection matrix (for stereographics)

  /// @param[in] fovy    field of view angle, in degrees, in the y direction
  /// @param[in] aspect  aspect ratio
  /// @param[in] near    near clipping plane coordinate
  /// @param[in] far    far clipping plane coordinate
  /// @param[in] xShift  amount to shift off x-axis
  /// @param[in] yShift  amount to shift off y-axis
  /// @param[in] focal  focal length
  def perspectiveOffAxis(fovy:Float, aspect:Float, near:Float, far:Float, xShift:Float, yShift:Float, focal:Float) = {
    val tanfovy = math.tan(fovy*Pi/180f/2.0).toFloat
    var t = near * tanfovy;  // height of view at distance = near
    var b = -t;
    var l = -aspect*t;
    var r = aspect*t;

    var shift = -xShift*near/focal;
    l += shift;
    r += shift;
    shift = -yShift*near/focal;
    t += shift;
    b += shift;

    perspective(l, r, b, t, near, far);
  }

}

class Mat4 extends Matrix(4) {

  def toQuat() = Quat().fromMatrix(this)
  def fromQuat(q:Quat) = q.toMatrix(this)

  def transform(v:Vec3, w:Float) = {
    val r = Matrix.multiply(this, Array(v.x,v.y,v.z,w))
    Vec3(r(0),r(1),r(2))
  }

  


}