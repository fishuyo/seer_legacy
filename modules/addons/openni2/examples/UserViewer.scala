
package seer
package openni.examples

import graphics._
import openni._

object UserViewer extends SeerApp {

  val mesh = Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh)

  OpenNI.init
  OpenNI.startTracking

  OpenNI.onUser { case users =>
    Run.animate {
      mesh.clear
      users.foreach { case u => mesh.vertices ++= u.points }
      mesh.update
    }
  }

  override def draw(){
    model.draw
  }

}
