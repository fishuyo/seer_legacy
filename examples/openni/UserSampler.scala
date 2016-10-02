

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
  OpenNI.pointCloudDensity = 4

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
      // user.points.clear 
    }

    try{
      // hack to put point data in user object for now
      // if(!users.isEmpty) users.head.points ++= OpenNI.pointMesh.vertices
      
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
  Keyboard.bindTyped( (c) => c match {
    case 'r' => loop.toggleRecord()
    case 't' => loop.togglePlay()
    case 'x' => loop.stack()
    case 'c' => loop.clear()
    case '\t' => loop.reverse()
    case 'i' => speed *=2; loop.setSpeed(speed)
    case 'k' => speed /=2; loop.setSpeed(speed)
    case 'o' => loop.save()
    case 'l' => 
      // loop.load("2016-09-29-23.23.09.bin") //left off
      // loop.load("2016-09-29-23.20.19.bin") //spin powerful
      // loop.load("2016-09-29-23.18.26.bin") //twist bounce meh 

    case 'y' =>
      loop.frames.trimStart(loop.frame.toInt)
      loop.frame = 0
    case 'u' =>
      loop.frames.trimEnd(loop.frames.length-1 - loop.frame.toInt)
      loop.frame = 0

    case _ => ()
  })



}

