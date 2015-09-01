

package com.fishuyo.seer
package openni

import graphics.Image
import spatial.Vec3

import collection.mutable.ArrayBuffer
import collection.mutable.ListBuffer

object User{
  def apply(id:Int) = new User(id)
  def apply(u:User) = {
    val copy = new User(u.id)
    copy.alpha = u.alpha
    copy.tracking = u.tracking
    copy.updateMask = u.updateMask
    copy.skeleton = Skeleton(u.skeleton)
    copy.points = u.points.clone 
    copy       
  }
}

class User(val id:Int){

  var alpha = 1f
  var tracking = false
  var updateMask = true

  var skeleton = OpenNI.getSkeleton(id)

  var points = ArrayBuffer[Vec3]()

  // val mask = Image(OpenNI.w,OpenNI.h,1,1)
  // val maskBufferSafe = mask.buffer.duplicate

}