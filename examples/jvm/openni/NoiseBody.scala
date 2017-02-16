

package com.fishuyo.seer
package examples.openni

import graphics._
import spatial._
import openni._
import io._
import util._
import openni._

import collection.mutable.ListBuffer

object NoiseBody extends SeerApp {

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true

  // point cloud mesh
  val mesh = new Mesh()
  mesh.primitive = Lines 
  mesh.maxVertices = 640*480
  mesh.maxIndices = 10000
  val numIndices = 10000
  val model = Model(mesh)
  model.material = Material.basic
  model.material.color = RGBA(0.1,0.1,0.1,0.01)
  model.material.transparent = true

  // environment bounding box
  var min = Vec3(10)
  var max = Vec3(-10)
  val box = Box()
  box.material = Material.basic
  box.material.lineWidth = 4

  //collapse bb after time
  // var waitGen = Random.float(1,5)()
  // var nextWait = waitGen()

  Keyboard.bind("g", () => Camera.nav.pos.set(0,0,0))
  Keyboard.bind("b", () => println(s"bounds: $min $max ${(max+min)/2.0f}"))

  override def init(){
    val blur = new FeedbackNode(0.7f, 0.3f)
    RootNode.outputTo(blur)
    blur.outputTo(ScreenNode)
  }

  override def draw(){
    FPS.print
    model.draw
    box.draw
  }

  override def animate(dt:Float){
    // time += dt
    try{

      // update point cloud add random indices
      mesh.clear
      if(OpenNI.pointMesh.vertices.length > 0){
        mesh.vertices ++= OpenNI.pointMesh.vertices
        val index = Random.int(mesh.vertices.length)
        mesh.indices ++= (0 until numIndices).map( _ => index() )
        mesh.update
      }

      // update bounding box of environment as user moves
      mesh.vertices.foreach{ case v =>
        min = min.min(v)
        max = max.max(v)
      }
      val cen = (max+min)/2
      box.pose.pos.set(cen)
      box.scale.set(max-min)

      // if(time > nextWait){
        // min.lerpTo(cen,0.001)
        // max.lerpTo(cen,0.001)
      // }

    } catch { case e:Exception => println(e) }
  }

}

