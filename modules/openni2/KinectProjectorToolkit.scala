
package com.fishuyo.seer
package openni

import spatial.Vec2
import spatial.Vec3

object KPC {

  var matrix = new Array[Float](11)

  def loadCalibration(filename:String) {
    try {
      val source = scala.io.Source.fromFile(filename)
      val m = source.getLines.map(_.toFloat).toArray
      if( m.length != 11) println("bad calibration file!")
      else matrix = m 
    } catch { case e:Exception => println(e.getMessage)}
  }

  def worldToScreen(kinectPoint:Vec3) = {
    val denom = matrix(8)*kinectPoint.x + matrix(9)*kinectPoint.y + matrix(10)*kinectPoint.z + 1.0f
    val x = (matrix(0)*kinectPoint.x + matrix(1)*kinectPoint.y + matrix(2)*kinectPoint.z + matrix(3)) / denom
    val y = (matrix(4)*kinectPoint.x + matrix(5)*kinectPoint.y + matrix(6)*kinectPoint.z + matrix(7)) / denom
    Vec3(
      (x - 0.5),
      ((1-y) - 0.5),
      0
    )
  }

}