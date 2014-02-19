
package com.fishuyo.seer
package graphics

import maths._
import spatial._


import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Queue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

import com.badlogic.gdx.graphics.g3d.loader._

trait Primitive {
  var mesh:Option[Mesh] = None
  def apply():Model = Model(mesh.getOrElse({mesh = Some(generateMesh()); mesh.get}))
  def generateMesh() = Mesh()
}


object Sphere extends Primitive {
  var radius = 1.f
  var bands = 30

  override def generateMesh():Mesh = generateMesh(radius, bands)
  def generateMesh(radius:Float=1.f, bands:Int=30):Mesh = generateMesh(new Mesh(), radius, bands)
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
        var u = 1.f - (long.toFloat / bands)
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
        mesh.indices += first.toShort
        mesh.indices += second.toShort
        mesh.indices += (first + 1).toShort
        mesh.indices += second.toShort
        mesh.indices += (second + 1).toShort
        mesh.indices += (first + 1).toShort
      }
    }
    mesh.init
    mesh
  }

}
class Sphere extends Model {
  var mesh = Sphere.generateMesh()
  addPrimitive(mesh)
}

object Cube extends Primitive {

  override def generateMesh():Mesh = generateMesh(new Mesh())
  def generateMesh( mesh:Mesh ):Mesh = {
    mesh.primitive = Triangles
    for( n<-(0 to 2); i<-List(-1,1); j<-List(-1,1); k<-List(-1,1)){
      val u = (j+1)/2
      val v = (1-k)/2
      mesh.texCoords += Vec2(u,v)      
      n match {
        case 0 => mesh.vertices += Vec3(i,j,k)  // left/right
                  mesh.normals += Vec3(i,0,0)
        case 1 => mesh.vertices += Vec3(k,i,j)  // top/bottom
                  mesh.normals += Vec3(0,i,0)
        case 2 => mesh.vertices += Vec3(j,k,i)  // front/back
                  mesh.normals += Vec3(i,0,0)
      }
    }
    
    for( f<-(0 until 6); i<-List(0,1,2,1,2,3)) mesh.indices += (4*f+i).toShort
    for( f<-(0 until 6); i<-List(0,1,1,3,3,2,2,0)) mesh.wireIndices += (4*f+i).toShort

    mesh.init
    mesh
  }
}
class Cube extends Model {
  var mesh = Cube.generateMesh()
  addPrimitive(mesh)
}


object Cylinder extends Primitive {
  var radius1 = 1.f
  var radius2 = 1.f
  var rings = 2
  var count = 30

  override def generateMesh():Mesh = generateMesh(new Mesh(),radius1,radius2,rings,count)
  def generateMesh(r1:Float=1.f, r2:Float=1.f, rings:Int=2, vertCount:Int=30):Mesh = generateMesh(new Mesh(),r1,r2,rings,vertCount)
  def generateMesh(mesh:Mesh, r1:Float, r2:Float, rings:Int, vertCount:Int):Mesh = {
    mesh.primitive = TriangleStrip
    
    val indxCount = vertCount+2
    var theta = 0.0

    //TODO add rings
    for (j <- (0 until vertCount)){
      val r = (if(j % 2 == 0) r1 else r2)
      val x = math.cos(theta).toFloat
      val y = math.sin(theta).toFloat
      val u = (if(j % 2 == 0) 1.f else 0.f)
      val v = j*1.f / vertCount

      mesh.normals += Vec3(x,y,(r1-r2)/2.f)
      mesh.texCoords += Vec2(u,v)
      mesh.vertices += Vec3(r*x,r*y,(if(j % 2 == 0) 0.f else 1.f))

      theta += 2 * math.Pi / (vertCount)
    }

    for( i<-(0 until vertCount)){
      if( i % 2 == 0){
        mesh.wireIndices += i.toShort
        mesh.wireIndices += ((i+1) % vertCount).toShort
      }
      mesh.wireIndices += ((i) % vertCount).toShort
      mesh.wireIndices += ((i+2) % vertCount).toShort
    }
    for( i<-(0 until indxCount)) mesh.indices += (i % vertCount).toShort
    
    mesh.init
    mesh
  }
}
class Cylinder extends Model {
  var mesh = Cylinder.generateMesh()
  addPrimitive(mesh)
}


object OBJ {
  def apply(file:String) = Model(load(file))

  def load(file:String) = {
    val model = new ObjLoader().loadObj(Gdx.files.internal(file))
    Mesh(model.meshes.get(0))
  }
}



class GLPrimitive(var pose:Pose=Pose(), var scale:Vec3=Vec3(1), var mesh:GdxMesh, val drawFunc:()=>Unit) extends Drawable {
  var color = RGBA(1,1,1,.6f)
  override def draw(){
    Shader.setColor(color)
    val s = scale / 2.f

    MatrixStack.push()
    MatrixStack.transform(pose,s)

    Shader.setMatrices()
    drawFunc()
    
    MatrixStack.pop()
  }
}


class Trace3D( var size:Int ) extends Drawable {
  var color1 = Vec3(1.f,0.f,0.f)
  var color2 = Vec3(0.f,0.f,1.f)
  var pose = Pose()
  var scale = Vec3(1.f)
  val mesh = new GdxMesh(false,size,0, VertexAttribute.Position, VertexAttribute.ColorUnpacked)
  var data = Queue[Vec3]()
  for( i<-(0 until size)) data.enqueue(Vec3())
  val vertices = new Array[Float](size*(3+4))
  var dirty = true

  def apply(v:Vec3) = {
    val u = Vec3(v)
    data.enqueue(u)
    data.dequeue()

    for( i<-(0 until size)){
      val c = color1.lerp(color2, i/size.toFloat)
      vertices(7*i) = data(i).x
      vertices(7*i+1) = data(i).y
      vertices(7*i+2) = data(i).z
      vertices(7*i+3) = c.x
      vertices(7*i+4) = c.y
      vertices(7*i+5) = c.z
      vertices(7*i+6) = 1.f
    }
    dirty = true
  }

  override def draw(){ 
    if( dirty ){
      mesh.setVertices( vertices )
      dirty = false
    }
    Shader.setColor(RGBA(color1,1.f))
    MatrixStack.push()
    MatrixStack.transform(pose,scale)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
    MatrixStack.pop()
  }

}

