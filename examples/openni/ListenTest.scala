

package com.fishuyo.seer
package examples.openni

import graphics._
import spatial._
import openni._
import io._
import util._
import openni._

import collection.mutable.ListBuffer

object ListenTest extends SeerApp {

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true

  val model = Cube().scale(0.1)
  implicit val name = "listenTest"
  OpenNI.listen { case body :: bodies =>                // user object contains image data, point cloud, and skeleton joint information
    model.pose.pos = body.head                // map position of cube to position of user's head
    println(body.head.y)
    println(body.skeleton.joints("head").y)
  }

  override def draw(){
    model.draw
  }

  override def animate(dt:Float){}

}

