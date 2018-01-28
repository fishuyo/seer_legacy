
package com.fishuyo.seer
package graphics

import spatial._
import util._


import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Queue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

import com.badlogic.gdx.graphics.g3d.loader._

trait ModelGenerator {
  var mesh:Option[Mesh] = None
  def apply():Model = Model(mesh.getOrElse({mesh = Some(generateMesh()); mesh.get}))
  def generateMesh() = Mesh()
}


object Sphere extends ModelGenerator {
  var radius = 1f
  var bands = 30

  override def generateMesh():Mesh = generateMesh(radius, bands)
  def generateMesh(radius:Float=1f, bands:Int=30):Mesh = generateMesh(new Mesh(), radius, bands)
  def generateMesh( mesh:Mesh, radius:Float, bands:Int):Mesh = {
    mesh.primitive = Triangles
    for ( lat <- (0 to bands)){
      var theta = lat * math.Pi / bands
      var sinTheta = math.sin(theta)
      var cosTheta = math.cos(theta)

      for (long <- (0 to bands)){
        var phi = long * 2 * math.Pi / bands
        var sinPhi = math.sin(phi)
        var cosPhi = math.cos(phi)
        var x = cosPhi * sinTheta
        var y = cosTheta
        var z = sinPhi * sinTheta
        var u = 1f - (long.toFloat / bands)
        var v = lat.toFloat / bands
        mesh.vertices += Vec3(x,y,z)*radius
        mesh.texCoords += Vec2(u,v)
        mesh.normals += Vec3(x,y,z)
      }
    }
    for ( lat <- (0 until bands)){
      for ( long <- (0 until bands)){
        var first = (lat * (bands + 1)) + long
        var second = first + bands + 1
        mesh.indices += first //.toShort
        mesh.indices += second //.toShort
        mesh.indices += (first + 1) //.toShort
        mesh.indices += second //.toShort
        mesh.indices += (second + 1) //.toShort
        mesh.indices += (first + 1) //.toShort
      }
    }
    // mesh.init
    mesh
  }

}
class Sphere extends Model {
  mesh = Sphere.generateMesh()
}

object Cube extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh, l:Float=0.5f ):Mesh = {

    mesh.indices ++= Array[Int](
      0,1,2, 1,2,3, //f
      4,5,6, 5,6,7, //b
      8,9,10, 9,10,11, //l
      12,13,14, 13,14,15, //r
      16,17,18, 17,18,19, //t
      20,21,22, 21,22,23 //b
    )

    for( i <- 0 until 4) mesh.normals += Vec3(0,0,1)
    mesh.vertices += Vec3(-l,l,l)
    mesh.vertices += Vec3(l,l,l)
    mesh.vertices += Vec3(-l,-l,l)
    mesh.vertices += Vec3(l,-l,l)

    for( i <- 0 until 4) mesh.normals += Vec3(0,0,-1)
    mesh.vertices += Vec3(-l,l,-l)
    mesh.vertices += Vec3(l,l,-l)
    mesh.vertices += Vec3(-l,-l,-l)
    mesh.vertices += Vec3(l,-l,-l)

    for( i <- 0 until 4) mesh.normals += Vec3(-1,0,0)
    mesh.vertices += Vec3(-l,l,-l)
    mesh.vertices += Vec3(-l,l,l)
    mesh.vertices += Vec3(-l,-l,-l)
    mesh.vertices += Vec3(-l,-l,l)

    for( i <- 0 until 4) mesh.normals += Vec3(1,0,0)
    mesh.vertices += Vec3(l,l,l)
    mesh.vertices += Vec3(l,l,-l)
    mesh.vertices += Vec3(l,-l,l)
    mesh.vertices += Vec3(l,-l,-l)

    for( i <- 0 until 4) mesh.normals += Vec3(0,1,0)
    mesh.vertices += Vec3(-l,l,-l)
    mesh.vertices += Vec3(l,l,-l)
    mesh.vertices += Vec3(-l,l,l)
    mesh.vertices += Vec3(l,l,l)

    for( i <- 0 until 4) mesh.normals += Vec3(0,-1,0)
    mesh.vertices += Vec3(-l,-l,-l)
    mesh.vertices += Vec3(l,-l,-l)
    mesh.vertices += Vec3(-l,-l,l)
    mesh.vertices += Vec3(l,-l,l)

    for( i <- 0 until 6){
      mesh.texCoords += Vec2(0, 0)
      mesh.texCoords += Vec2(1, 0)
      mesh.texCoords += Vec2(1, 1)
      mesh.texCoords += Vec2(0, 1)
    }

    for( f<-(0 until 6); i<-List(0,1,1,3,3,2,2,0)) mesh.wireIndices += (4*f+i) //.toShort

    mesh
  }
}
class Cube extends Model {
  mesh = Cube.generateMesh()
}

object Box extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh, l:Float=0.5f ):Mesh = {
    mesh.primitive = Lines
    mesh.indices ++= Array[Int](
      0,1,1,3,3,2,2,0,
      4,5,5,7,7,6,6,4,
      0,4, 1,5, 3,7, 2,6
    )

    mesh.vertices += Vec3(-l,l,l)
    mesh.vertices += Vec3(l,l,l)
    mesh.vertices += Vec3(-l,-l,l)
    mesh.vertices += Vec3(l,-l,l)

    mesh.vertices += Vec3(-l,l,-l)
    mesh.vertices += Vec3(l,l,-l)
    mesh.vertices += Vec3(-l,-l,-l)
    mesh.vertices += Vec3(l,-l,-l)

    for( i <- 0 until 2){
      mesh.texCoords += Vec2(0, 0)
      mesh.texCoords += Vec2(1, 0)
      mesh.texCoords += Vec2(1, 1)
      mesh.texCoords += Vec2(0, 1)
    }

    mesh
  }
}
class Box extends Model {
  mesh = Box.generateMesh()
}

object Tetrahedron extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh, l:Float = math.sqrt(1f/3).toFloat ):Mesh = {
    mesh.primitive = Triangles
    mesh.vertices += Vec3(l,l,l)
    mesh.vertices += Vec3(-l,l,-l)
    mesh.vertices += Vec3(l,-l,-l)
    mesh.vertices += Vec3(-l,-l,l)
    
    mesh.indices ++= List(0,2,1, 0,1,3, 1,2,3, 2,0,3)
    mesh.wireIndices ++= List(0,2,2,1,1,0, 0,3,1,3,2,3)

    // mesh.vertices.foreach( (v) => mesh.normals += v.normalized )
    mesh.recalculateNormals
    mesh
  }
}
class Tetrahedron extends Model {
  mesh = Tetrahedron.generateMesh()
}

object Octahedron extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh, l:Float = 1f ):Mesh = {
    mesh.primitive = Triangles
    mesh.vertices += Vec3(l,0,0)
    mesh.vertices += Vec3(0,l,0)
    mesh.vertices += Vec3(0,0,l)
    mesh.vertices += Vec3(-l,0,0)
    mesh.vertices += Vec3(0,-l,0)
    mesh.vertices += Vec3(0,0,-l)
    
    mesh.indices ++= List(0,1,2, 1,3,2, 3,4,2, 4,0,2,
                          1,0,5, 3,1,5, 4,3,5, 0,4,5)

    // mesh.wireIndices ++= List(0,2,2,1,1,0, 0,3,1,3,2,3)

    // mesh.vertices.foreach( (v) => mesh.normals += v.normalized )
    mesh.recalculateNormals
    mesh
  }
}
class Octahedron extends Model {
  mesh = Octahedron.generateMesh()
}

object Dodecahedron extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh ):Mesh = {
    mesh.primitive = Triangles
    val a = 1.6f * 0.5f;
    val b = 1.6f / (2 * Phi);
    mesh.vertices ++= List(
      Vec3(0, b,-a),  Vec3(b, a, 0), Vec3(-b, a, 0), //  0  1  2
      Vec3(0, b, a),  Vec3(0,-b, a), Vec3(-a, 0, b), //  3  4  5
      Vec3(a, 0, b),  Vec3(0,-b,-a), Vec3(a, 0,-b), //  6  7  8
      Vec3(-a, 0,-b), Vec3(b,-a, 0), Vec3(-b,-a, 0)  //  9 10 11
    )

    mesh.indices ++= List(
       1, 0, 2,  2, 3, 1,  4, 3, 5,  6, 3, 4,
       7, 0, 8,  9, 0, 7, 10, 4,11, 11, 7,10,
       5, 2, 9,  9,11, 5,  8, 1, 6,  6,10, 8,
       5, 3, 2,  1, 3, 6,  2, 0, 9,  8, 0, 1,
       9, 7,11, 10, 7, 8, 11, 4, 5,  6, 4,10
    )

    // mesh.vertices.foreach( (v) => mesh.normals += v.normalized )
    mesh.recalculateNormals
    mesh
  }
}
class Dodecahedron extends Model {
  mesh = Dodecahedron.generateMesh()
}

object Icosahedron extends ModelGenerator {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh ):Mesh = {
    mesh.primitive = Triangles
    mesh.vertices ++= List(
      Vec3(-0.57735, -0.57735, 0.57735),
      Vec3(0.934172,  0.356822, 0),
      Vec3(0.934172, -0.356822, 0),
      Vec3(-0.934172, 0.356822, 0),
      Vec3(-0.934172, -0.356822, 0),
      Vec3(0,  0.934172,  0.356822),
      Vec3(0,  0.934172,  -0.356822),
      Vec3(0.356822,  0,  -0.934172),
      Vec3(-0.356822,  0,  -0.934172),
      Vec3(0,  -0.934172,  -0.356822),
      Vec3(0,  -0.934172,  0.356822),
      Vec3(0.356822,  0,  0.934172),
      Vec3(-0.356822,  0,  0.934172),
      Vec3(0.57735,  0.57735,  -0.57735),
      Vec3(0.57735,  0.57735, 0.57735),
      Vec3(-0.57735,  0.57735,  -0.57735),
      Vec3(-0.57735,  0.57735,  0.57735),
      Vec3(0.57735,  -0.57735,  -0.57735),
      Vec3(0.57735,  -0.57735,  0.57735),
      Vec3(-0.57735,  -0.57735,  -0.57735)
    )

    mesh.indices ++= List(
      18, 2, 1, 11,18, 1, 14,11, 1,  7,13, 1, 17, 7, 1,
       2,17, 1, 19, 4, 3,  8,19, 3, 15, 8, 3, 12,16, 3,
       0,12, 3,  4, 0, 3,  6,15, 3,  5, 6, 3, 16, 5, 3,
       5,14, 1,  6, 5, 1, 13, 6, 1,  9,17, 2, 10, 9, 2,
      18,10, 2, 10, 0, 4,  9,10, 4, 19, 9, 4, 19, 8, 7,
       9,19, 7, 17, 9, 7,  8,15, 6,  7, 8, 6, 13, 7, 6,
      11,14, 5, 12,11, 5, 16,12, 5, 12, 0,10, 11,12,10,
      18,11,10
    )

    // mesh.vertices.foreach( (v) => mesh.normals += v.normalized )
    mesh.recalculateNormals
    mesh
  }
}
class Icosahedron extends Model {
  mesh = Icosahedron.generateMesh()
}


object Cylinder extends ModelGenerator {
  var radius1 = 1f
  var radius2 = 1f
  var rings = 2
  var count = 30

  override def generateMesh():Mesh = generateMesh(new Mesh(),radius1,radius2,rings,count)
  def generateMesh(r1:Float=1f, r2:Float=1f, rings:Int=2, vertCount:Int=30):Mesh = generateMesh(new Mesh(),r1,r2,rings,vertCount)
  def generateMesh(mesh:Mesh, r1:Float, r2:Float, rings:Int, vertCount:Int):Mesh = {
    mesh.primitive = TriangleStrip
    
    val indxCount = vertCount+2
    var theta = 0.0

    //TODO add rings
    for (j <- (0 until vertCount)){
      val r = (if(j % 2 == 0) r1 else r2)
      val x = math.cos(theta).toFloat
      val y = math.sin(theta).toFloat
      val u = (if(j % 2 == 0) 1f else 0f)
      val v = j*1f / vertCount

      mesh.normals += Vec3(x,y,(r1-r2)/2f)
      mesh.texCoords += Vec2(u,v)
      mesh.vertices += Vec3(r*x,r*y,(if(j % 2 == 0) 0f else 1f))

      theta += 2 * math.Pi / (vertCount)
    }

    for( i<-(0 until vertCount)){
      if( i % 2 == 0){
        mesh.wireIndices += i //.toShort
        mesh.wireIndices += ((i+1) % vertCount) //.toShort
      }
      mesh.wireIndices += ((i) % vertCount) //.toShort
      mesh.wireIndices += ((i+2) % vertCount) //.toShort
    }
    for( i<-(0 until indxCount)) mesh.indices += (i % vertCount) //.toShort
    
    // mesh.init
    mesh
  }
}
class Cylinder extends Model {
  mesh = Cylinder.generateMesh()
}


// object OBJ {
//   def apply(file:String) = Model(load(file))

//   def load(file:String) = {
//     val model = new ObjLoader().loadObj(Gdx.files.internal(file))
//     Mesh(model.meshes.get(0))
//   }
// }



// class GLPrimitive(var pose:Pose=Pose(), var scale:Vec3=Vec3(1), var mesh:GdxMesh, val drawFunc:()=>Unit) extends Drawable {
//   var color = RGBA(1,1,1,.6f)
//   override def draw(){
//     Shader.setColor(color)
//     val s = scale / 2f

//     MatrixStack.push()
//     MatrixStack.transform(pose,s)

//     Shader.setMatrices()
//     drawFunc()
    
//     MatrixStack.pop()
//   }
// }


class Trace3D( var size:Int ) extends Drawable {
  var thickness = 1f
  var smooth = false
  val mesh = Mesh()
  val model = Model(mesh)
  model.material = new BasicMaterial
  var data = Queue[Vec3]()
  val vel = Vec3()
  for( i<-(0 until size)){
    data.enqueue(Vec3())
    mesh.vertices += Vec3()
    mesh.colors += RGBA(1,1,1,1)
    // mesh.primitive = TriangleStrip
    mesh.primitive = LineStrip
  }
  setColors(Vec3(1),Vec3(0.2))
  var dirty = true

  def apply(v:Vec3) = {
    val u = Vec3(v)
    vel.lerpTo(u-data.last, 0.1f)
    data.enqueue(u)
    data.dequeue()

    mesh.vertices.clear
    mesh.vertices ++= data
    dirty = true
  }

  def setColors(c1:Vec3,c2:Vec3){
    for( i<-(0 until size)){
      val c = c2.lerp(c1, i/size.toFloat)
      mesh.colors(i) = RGBA(c,1f)
    }
    dirty = true  
  }

  override def init(){
    mesh.init
  }
  override def draw(){ 
    if( dirty ){
      mesh.update
      dirty = false
    }
    // if(smooth){
      // Gdx.gl.glEnable(GL20.GL_LINE_SMOOTH)
    // }
    val thick = map(vel.mag, 0f,0.01f,0f,6f)
    Gdx.gl.glLineWidth(thickness)
    model.draw()
  }

}

