
package seer
package spatial


import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

object Octant {
  val o = Array[Vec3]( Vec3(-1,-1,-1), Vec3(1,-1,-1), Vec3(-1,1,-1), Vec3(1,1,-1), Vec3(-1,-1,1),Vec3(1,-1,1),Vec3(-1,1,1),Vec3(1,1,1) )
  def apply( i:Int ) = o(i)
}

object Octree {
  def apply[T]( c:Vec3, halfsize:Float ) = new Octree[T](null, c, halfsize)
  def apply[T]( p:Octree[T], c:Vec3, halfsize:Float ) = new Octree[T](p, c, halfsize)
}

class Octree[T]( val parent:Octree[T], c:Vec3, val halfsize:Float) extends AABB(c - Vec3(halfsize), c + Vec3(halfsize)) {

  var minSize = .1f
  var depth = 0
  var numChildren = 0
  var children: Array[Octree[T]] = null
  // var points: ListBuffer[Vec3] = null
  val values = HashMap[Vec3,T]()

  if( parent != null ){
    depth = parent.depth + 1
    //println("child depth and oct: " + depth + " " + parent.octantId(c) )
    minSize = parent.minSize
  }

  def clear(){
    if(children != null) children.foreach{ case c => if(c != null) c.clear }
    values.clear
  }

  def +=(p:(Vec3,T)) = insert( p._1, p._2)
  def insert( p:Vec3, value:T ):Boolean = {
    //if point inside of octree AABB
    if( contains( p ) ){
      //reached leaf node
      if( halfsize <= minSize ){
        
        // if( values == null ) values = new HashMap[Vec3,T] //new ListBuffer[Vec3]
        values += (p -> value)
        
      } else{ //insert into child octree
        
        if( children == null ) children = new Array[Octree[T]](8)
        val oct = octantId(p)
        
        if( children(oct) == null ){
          val size = halfsize * .5f
          val cen = c + Octant(oct) * size
          //println("new octant")
          children(oct) = Octree( this, cen, size )
          numChildren += 1
        }
        return children(oct).insert(p,value)
      }
    }
    false
  }

  def remove(p:Vec3, value:T): Option[T] = {
    //if point inside of octree AABB
    if( contains( p ) ){
      //reached leaf node
      if( halfsize <= minSize ){
        return values.remove(p)        
      } else{ //remove from child octree
        
        if( children == null ) return None
        val oct = octantId(p)
        
        if( children(oct) == null ) return None
        
        return children(oct).remove(p,value)
      }
    }
    None
  }

  def octantId( p:Vec3 ): Int = (if(p.x >= c.x) 1 else 0) + (if(p.y >= c.y) 2 else 0) + (if(p.z >= c.z) 4 else 0)

  def getAll():HashMap[Vec3,T] = {
    if( numChildren == 0 ){
      values
    }else {
      var results:HashMap[Vec3,T] = HashMap[Vec3,T]()
      for( i <- (0 until 8)){
        if( children(i) != null ){
          val points = children(i).getAll()
          if( points != null ){
            results ++= points
          }
        }
      }
      results
    }
  }
  def getInSphere( c:Vec3, r:Float ):HashMap[Vec3,T] = {
    var results:HashMap[Vec3,T] = HashMap[Vec3,T]()
  
    if( this.intersectsSphere( c, r ) ){
      if( numChildren == 0 ){
        results = values.filter { case (p:Vec3,v) => (c-p).magSq <= r*r }
        //println( "leaf points: " + results.size )
      }else if( numChildren > 0 ) {
        //println("depth numchildren: " + depth + " " + numChildren )
        for( i <- (0 until 8)){
          if( children(i) != null ){
            val points = children(i).getInSphere(c,r)
            if( points != null ){
              results ++= points
            }
          }
        }
      }
    }
    results
  }

  def foreach(f:(Vec3,T) => Unit){
    if( numChildren == 0 ){
      values.foreach{ case (v,t) => f(v,t) }
    }else if( numChildren > 0 ) {
      for( i <- (0 until 8)){
        if( children(i) != null ){
          children(i).foreach(f)
        }
      }
    }
  }


}
