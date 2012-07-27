
package com.fishuyo
package spatial

import maths._

/** Class Pose represents a position and orientation in 3d space */
class Pose( var pos:Vec3=Vec3(0), var quat:Quat=Quat(1,0,0,0) ){
  
  /** translate and rotate pose by another pose */
  def *(p:Pose) = new Pose( pos+p.pos, quat*p.quat)
  def *=(p:Pose) = {    
    pos += p.pos
    quat *= p.quat
    this
  }


  /** return linear interpolated Pose from this to p by amount d*/
  def lerp(p:Pose, d:Float) = new Pose( pos.lerp(p.pos, d), quat.slerp(p.quat, d) )


}

/** Pose that moves through space */
class Nav( p:Vec3=Vec3(0) ) extends Pose(p) {


}


