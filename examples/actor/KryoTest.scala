
package com.fishuyo.seer

import graphics._
import spatial._
import types._
import util._
import io._

import com.twitter.chill.KryoInjection
import scala.util.Success

object KryoTest extends SeerApp {

  val mesh = Mesh()
  mesh.primitive = LineStrip
  mesh.vertices ++= (0 to 1000).map( (i) => Vec3() )
  mesh.maxVertices = 1000

  var buffer = new RingBuffer[Vec2](1000)

  override def draw(){

    mesh.draw()
  }

  override def animate(dt:Float){

    buffer += Mouse.xy()
    mesh.vertices.clear
    mesh.vertices ++= buffer.map(Vec3(_))
    mesh.update()
  }

  // val kryoBuffer = buffer.clone
  var kryoBytes:Array[Byte] = _

  Keyboard.bind("r", () => { kryoBytes = KryoInjection(buffer) })
  Keyboard.bind("t", () => { 
    val decode = KryoInjection.invert(kryoBytes)
    decode match {
      case Success(b:RingBuffer[Vec2]) => buffer = b 
      case _ => println("Invert failed!")
    }
  })



// val someItem = for( i <- (0 until 100)) yield util.Random.vec3()
// val bytes:  Array[Byte]    = KryoInjection(someItem)
// val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
// val tryDecode = KryoInjection.invert(bytes)

// println(tryDecode)

}
