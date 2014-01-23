package com.fishuyo.seer
package graphics

import maths._

import scala.collection.mutable.ListBuffer

class TriangleMesh {

  val vertices = new ListBuffer[Vec3] 
  val normals = new ListBuffer[Vec3]
  val textcoords = new ListBuffer[Vec3]
  //val triangles = new ListBuffer[Triangle]
  val indices = new ListBuffer[Short]

  def addFace( u:Short, v:Short, w:Short) = {
  	indices += u
  	indices += v
  	indices += w
  	//triangles += new Triangle( (vertices(u-1), vertices(v-1), vertices(w-1)), material ) 
	}

  def normalize = {
  
    var min = Vec3( java.lang.Double.MAX_VALUE )
    var max = Vec3( java.lang.Double.MIN_VALUE )
    vertices.foreach( {
      case Vec3( x,y,z ) => {
        min = Vec3( math.min( min.x, x ), math.min( min.y, y ), math.min( min.z, z ))
        max = Vec3( math.max( max.x, x ), math.max( max.y, y ), math.max( max.z, z ))
      }
    })

    val half = Vec3(.5)
    val diff = max - min
    val maxDiff = math.max( math.max( diff.x, diff.y ), diff.z)
    for (i <- 0 until vertices.length ) {
      vertices(i) -= min
      vertices(i) *= 1.0f / maxDiff
      vertices(i) -= half
    }
    println( min + " " + max )
  }

  //override def translate( v: Vec3 ) = for (i <- 0 until vertices.length ) vertices(i) += v

  // override def intersect( ray: Ray ) : Hit = {
  //   //println("TriMesh: intersect")
  //   val hits = triangles.map( _.intersect(ray) ).collect({case h:Hit => h})
  //   //println( hits.size + " " + hits.first )
  //   hits.size match {
  //     case 0 => null
  //     case _ =>  hits.min //( Ordering[Double].on[Hit]( { case h:Hit => h.t; case _ => java.lang.Double.MAX_VALUE }) )
  //   }
  // }

  // override def onDraw( gl: GL2 ) = {
  //   // gl.glBegin(GL.GL_TRIANGLES)
  //   //   gl.glColor3f( .8f, 0, 0)
  //   //   triangles.foreach( t => t.onDraw( gl ) )
  //   // gl.glEnd
  // }

  override def toString = vertices.toString //( (v:Vec3) => println( v )) 

}