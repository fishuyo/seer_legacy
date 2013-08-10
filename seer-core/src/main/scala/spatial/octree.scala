
package com.fishuyo
package spatial

import maths._

import scala.collection.mutable.ListBuffer
//import scala.tools.nsc.interpreter.ILoop._
//import scala.tools.nsc.interpreter.NamedParam

object Octant {
  val o = Array[Vec3]( Vec3(-1,-1,-1), Vec3(1,-1,-1), Vec3(-1,1,-1), Vec3(1,1,-1), Vec3(-1,-1,1),Vec3(1,-1,1),Vec3(-1,1,1),Vec3(1,1,1) )
  def apply( i:Int ) = o(i)
}
object Octree {

  def apply( c:Vec3, halfsize:Float ) = new Octree(null, c, halfsize)
  def apply( p:Octree, c:Vec3, halfsize:Float ) = new Octree(p, c, halfsize)

}

class Octree( val parent:Octree, c:Vec3, halfsize:Float) extends AABB(c,halfsize) {

  var minSize = .1f
  var depth = 0
  var numChildren = 0
  var children: Array[Octree] = null
  var points: ListBuffer[Vec3] = null

  if( parent != null ){
    depth = parent.depth + 1
    //println("child depth and oct: " + depth + " " + parent.octantId(c) )
    minSize = parent.minSize
  }

  def insertPoint( p:Vec3 ):Boolean = {
    //if point inside of octree AABB
    if( contains( p ) ){
      //reached leaf node
      if( halfsize <= minSize ){
        
        if( points == null ) points = new ListBuffer[Vec3]
        points += p
        
      } else{ //insert into child octree
        
        if( children == null ) children = new Array[Octree](8)
        val oct = octantId(p)
        
        if( children(oct) == null ){
          val size = halfsize * .5f
          val cen = c + Octant(oct) * size
          //println("new octant")
          children(oct) = Octree( this, cen, size )
          numChildren += 1
        }
        return children(oct).insertPoint(p)
      }
    }
    false
  }

  def octantId( p:Vec3 ): Int = (if(p.x >= c.x) 1 else 0) + (if(p.y >= c.y) 2 else 0) + (if(p.z >= c.z) 4 else 0)

  def getPoints = {}
  def getPointsInSphere( c:Vec3, r:Float ):ListBuffer[Vec3] = {
    var results:ListBuffer[Vec3] = null
  
    if( this.intersectsSphere( c, r ) ){
      if( points != null ){
        results = points.filter( (p:Vec3) => (c-p).magSq <= r*r )
        //println( "leaf points: " + results.size )
      }else if( numChildren > 0 ) {
        //println("depth numchildren: " + depth + " " + numChildren )
        for( i <- (0 until 8)){
          if( children(i) != null ){
            val points = children(i).getPointsInSphere(c,r)
            if( points != null ){
              if( results == null ) results = new ListBuffer[Vec3]
              results.appendAll( points )
            }
          }
        }
      }
    }
    results
  }

}
