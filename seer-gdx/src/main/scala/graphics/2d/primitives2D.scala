
package com.fishuyo.seer
package graphics

import spatial._
import spatial._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

object Plane extends ModelGenerator {
  override def generateMesh():Mesh = generateMesh(2,2,2,2,Quat())
  def generateMesh(w:Float=2f,h:Float=2f,nx:Int=2,ny:Int=2,normal:Quat=Quat()):Mesh = generateMesh(new Mesh(),w,h,nx,ny,normal)
  def generateMesh(mesh:Mesh, w:Float,h:Float,nx:Int,ny:Int,normal:Quat):Mesh = {

    implicit def int2short(i:Int) = i.toShort

    mesh.primitive = Triangles
    val dx = w / (nx-1).toFloat
    val dy = h / (ny-1).toFloat
    val bl = normal.toX() * -w/2f + normal.toY * -h/2f
    val vx = normal.toX() * dx
    val vy = normal.toY() * dy
    for(y <-(0 until ny); x <-(0 until nx)){
      mesh.vertices += bl + vx*x + vy*y
      mesh.texCoords += Vec2( x.toFloat/(nx-1), y.toFloat/(ny-1) )
      mesh.normals += normal.toZ()
    }
    for(y <-(0 until ny-1); x <-(0 until nx-1)){
      val i = y*nx + x
      mesh.indices ++= List(i,i+1,i+nx)
      mesh.indices ++= List(i+1,i+nx+1,i+nx)
    }
    
    for(y <-(0 until ny); x <-(0 until nx)){
      val i = y*nx + x
      if( x < nx-1 ) mesh.wireIndices ++= List(i,i+1)
      if( y < ny-1 ){
        mesh.wireIndices ++= List(i,i+nx)
        // if( x == nx-1) mesh.wireIndices ++= List(i+1,i+nx+1)
      }
    }
    // mesh.init
    mesh
  }
}

object Circle extends ModelGenerator {
  override def generateMesh():Mesh = generateMesh(1f,30)
  def generateMesh(r:Float=1f, nt:Int=30):Mesh = generateMesh(new Mesh(),r,nt)
  def generateMesh(mesh:Mesh, r:Float, nt:Int):Mesh = {
    mesh.primitive = TriangleFan
    val theta = 2*Pi/nt
    mesh.vertices += Vec3(0,0,0)
    for(i <-(0 to nt)){
      val x = r * math.cos(i * theta)
      val y = r * math.sin(i * theta)
      mesh.vertices += Vec3(x,y,0)
    }
    mesh
  }
}


// object Quad {
//   var quad = None:Option[Quad]
//   var wireQuad = None:Option[Quad]
//   def apply() = quad.getOrElse({ quad = Some(new Quad()); quad.get })
//   def asLines() = wireQuad.getOrElse({ wireQuad = Some(new Quad("lines")); wireQuad.get })
// }
// class Quad(style:String="triangles") extends Drawable {
//   var mesh:GdxMesh = _
//   var primitive = GL20.GL_TRIANGLES
//   // var drawFunc = () => {}

//   style match {
//     case "lines" =>
//       mesh = new GdxMesh(true,4,5, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
//       mesh.setVertices( Array[Float](
//         -1,-1,0, 0,0,1,   0,0,
//         1,-1,0,  0,0,1,   1,0,
//         1,1,0,   0,0,1,   1,1,
//         -1,1,0,  0,0,1,   0,1
//       ))

//       mesh.setIndices( Array[Short](
//         0,1,2,3,0
//       ))
//       primitive = GL20.GL_LINE_STRIP
//       // drawFunc = () => { mesh.render(Shader(), GL20.GL_LINE_STRIP)}

//     case _ => 
//       mesh = new GdxMesh(true,4,6, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
//       mesh.setVertices( Array[Float](
//         -1,-1,0, 0,0,1,   0,0,
//         1,-1,0,  0,0,1,   1,0,
//         1,1,0,   0,0,1,   1,1,
//         -1,1,0,  0,0,1,   0,1
//       ))

//       mesh.setIndices( Array[Short](
//         0,1,2, 0,2,3
//       ))
//       // drawFunc = () => { mesh.render(Shader(), GL20.GL_TRIANGLES)}
//   }
//   override def draw(){
//     mesh.render(Shader(), primitive)
//   }

//   def intersect(ray:Ray):Option[Vec3] = {
//     val n = Vec3(0,0,1)
//     val vertices = (Vec3(-1,-1,0), Vec3(1,-1,0), Vec3(1,1,0), Vec3(-1,1,0))
//     val dn = ray.d dot n
    
//     if( dn == 0) return None

//     val t = -(( ray.o - vertices._1 ) dot n ) / dn
//     if( t < 0f) return None
//     val x = ray(t)

//     if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
//         (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
//         (((vertices._4 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ||
//         (((vertices._1 - vertices._4) cross ( x - vertices._4 )) dot n) < 0 ) return None

//     Some(x)
//   }
// }



// object Primitive2D extends GLThis {
//   def quad = {
//     val mesh = new GdxMesh(true,4,6, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
//     mesh.setVertices( Array[Float](
//       -1,-1,0, 0,0,1,   0,0,
//       1,-1,0,  0,0,1,   1,0,
//       1,1,0,   0,0,1,   1,1,
//       -1,1,0,  0,0,1,   0,1
//     ))

//     mesh.setIndices( Array[Short](
//       0,1,2, 0,2,3
//     ))
//     val draw = () => { mesh.render(Shader(), GL20.GL_TRIANGLES)}
//     mesh //new GLPrimitive(Pose(),Vec3(1f),mesh,draw)
//   }
// }


