

package com.fishuyo.seer
package openni.examples

import spatial._
import graphics._
import openni._
import io._

import collection.mutable.ListBuffer


object UserRecord extends SeerApp {

  val loop = new UserLoop()

  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh)

  val joints = ListBuffer[Vec3]()

  OpenNI.init
  OpenNI.startTracking
  OpenNI.setPointCloudThinning(4)

  OpenNI.onUser { case users =>

    val out = ListBuffer[User]()
    loop.io(users, out)

    joints.clear
    if(out.isEmpty) joints ++= users.flatMap(_.skeleton.joints.values)
    else joints ++= out.flatMap(_.skeleton.joints.values)

    Run.animate {
      mesh.clear
      if(out.isEmpty) mesh.vertices ++= users.flatMap(_.points)
      else mesh.vertices ++= out.flatMap(_.points)
      mesh.update
    }
  }

  override def draw(){
    model.draw
    joints.foreach { case p => Sphere().scale(0.01f).translate(p).draw }
  }

  var speed = 1f
  // implicit val ctx = rx.Ctx.Owner.Unsafe
  Keyboard.onKeyDown {
    case 'r' => loop.toggleRecord()
    case 't' => loop.togglePlay()
    case 'x' => loop.stack()
    case 'c' => loop.clear()
    case '\t' => loop.reverse()
    case 'i' => speed *=2; loop.setSpeed(speed)
    case 'k' => speed /=2; loop.setSpeed(speed)
    case 'o' => loop.save()
    case 'l' => 
      loop.load("2018-05-21-16.37.09.bin")

    case 'y' =>
      loop.frames.trimStart(loop.frame.toInt)
      loop.frame = 0
    case 'u' =>
      loop.frames.trimEnd(loop.frames.length-1 - loop.frame.toInt)
      loop.frame = 0
  }



}

