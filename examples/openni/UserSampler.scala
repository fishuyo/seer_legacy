

package com.fishuyo.seer
package examples.openni

import graphics._
import spatial._
import openni._
import io._
import util._
import openni._

import collection.mutable.ArrayBuffer
import collection.mutable.ListBuffer


object UserSampler extends SeerApp {

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true
  OpenNI.pointCloudDensity = 2

  val loop = new UserLoop()
  val out = ListBuffer[User]()

  // point cloud mesh
  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh)

  override def draw(){
    model.draw
  }

  override def animate(dt:Float){
    // update users 
    val users = OpenNI.users.values.filter(_.tracking)
    users.foreach { case user => 
      user.skeleton.updateJoints
      user.points.clear 
    }

    try{
      // hack to put point data in user object for now
      if(!users.isEmpty) users.head.points ++= OpenNI.pointMesh.vertices
      
      // copy users
      val in = ListBuffer[User]()
      in ++= users.map(User(_))

      // run looper
      out.clear
      loop.io(in, out)

      mesh.clear

      if( out.isEmpty ){
        mesh.vertices ++= OpenNI.pointMesh.vertices
      } else {
        out.foreach{ case user =>
          // mesh.vertices ++= user.skeleton.joints.values
          mesh.vertices ++= user.points
        }
      }
      mesh.update

    } catch { case e:Exception => println(e) }
  }

  var speed = 1f
  implicit val ctx = rx.Ctx.Owner.Unsafe
  Keyboard.bind("r", () => loop.toggleRecord() )
  Keyboard.bind("t", () => loop.togglePlay() )
  Keyboard.bind("x", () => loop.stack() )
  Keyboard.bind("c", () => loop.clear() )
  Keyboard.bind("\t", () => loop.reverse() )
  // Keyboard.bind("j", () => loop.setAlphaBeta(1f,.99f) )
  Keyboard.bind("i", () => {speed *=2; loop.setSpeed(speed) })
  Keyboard.bind("k", () => {speed /=2; loop.setSpeed(speed) })

  Keyboard.bind("o", () => loop.save())
  Keyboard.bind("l", () => {
    // loop.load("2016-03-14-18.07.26.bin")
    loop.load("../../../dailyworlds/emily_fela_dance.bin")
  })


}

