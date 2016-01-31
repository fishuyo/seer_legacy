

package com.fishuyo.seer
package examples.openni

import graphics._
import spatial._
import openni._
import io._
import util._
import openni._

import collection.mutable.ListBuffer

object Spotlight extends SeerApp {

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true
  OpenNI.makeDebugImage = false

  // spotlight spheres
  val spotlights = ListBuffer[Model]()
  for(i <- 0 until 4){
    val s = Circle().scale(0.15f,0.25f,1)
    s.pose.pos.set(0,-100,0)
    spotlights += s
  }

  // point cloud mesh
  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh)

  // environment bounding box
  var min = Vec3(10)
  var max = Vec3(-10)
  val box = Box()

  Keyboard.bind("g", () => Camera.nav.pos.set(0,0,0))
  Keyboard.bind("b", () => println(s"bounds: $min $max ${(max+min)/2.0f}"))

  override def draw(){
    FPS.print

    spotlights.foreach(_.draw)
    model.draw
    box.draw
  }

  override def animate(dt:Float){
    try{
      // move circles to user torsos
      for( id <- 0 until 4){
        val s = OpenNI.getSkeleton(id)
        if(s.tracking){
          s.updateJoints()
          spotlights(id).pose.pos.set(s.joints("torso"))
        } else spotlights(id).pose.pos.set(0,-100,0)
      }

      // update point cloud
      mesh.clear
      mesh.vertices ++= OpenNI.pointMesh.vertices
      mesh.update

      // update bounding box of environment as user moves
      mesh.vertices.foreach{ case v =>
        min = min.min(v)
        max = max.max(v)
      }
      box.pose.pos.set( (max+min)/2.0f )
      box.scale.set( max-min )

    } catch { case e:Exception => println(e) }
  }

}

