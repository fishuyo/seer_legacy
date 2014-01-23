
package com.fishuyo.seer
package graphics

import maths._
import spatial._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}


object Quad {
  var quad = None:Option[Quad]
  var wireQuad = None:Option[Quad]
  def apply() = quad.getOrElse({ quad = Some(new Quad()); quad.get })
  def asLines() = wireQuad.getOrElse({ wireQuad = Some(new Quad("lines")); wireQuad.get })
}
class Quad(style:String="triangles") extends Drawable {
  var mesh:GdxMesh = _
  var primitive = GL10.GL_TRIANGLES
  // var drawFunc = () => {}

  style match {
    case "lines" =>
      mesh = new GdxMesh(true,4,5, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
      mesh.setVertices( Array[Float](
        -1,-1,0, 0,0,1,   0,0,
        1,-1,0,  0,0,1,   1,0,
        1,1,0,   0,0,1,   1,1,
        -1,1,0,  0,0,1,   0,1
      ))

      mesh.setIndices( Array[Short](
        0,1,2,3,0
      ))
      primitive = GL10.GL_LINE_STRIP
      // drawFunc = () => { mesh.render(Shader(), GL10.GL_LINE_STRIP)}

    case _ => 
      mesh = new GdxMesh(true,4,6, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
      mesh.setVertices( Array[Float](
        -1,-1,0, 0,0,1,   0,0,
        1,-1,0,  0,0,1,   1,0,
        1,1,0,   0,0,1,   1,1,
        -1,1,0,  0,0,1,   0,1
      ))

      mesh.setIndices( Array[Short](
        0,1,2, 0,2,3
      ))
      // drawFunc = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
  }
  override def draw(){
    mesh.render(Shader(), primitive)
  }

  def intersect(ray:Ray):Option[Vec3] = {
    val n = Vec3(0,0,1)
    val vertices = (Vec3(-1,-1,0), Vec3(1,-1,0), Vec3(1,1,0), Vec3(-1,1,0))
    val dn = ray.d dot n
    
    if( dn == 0) return None

    val t = -(( ray.o - vertices._1 ) dot n ) / dn
    if( t < 0.f) return None
    val x = ray(t)

    if( (((vertices._2 - vertices._1) cross ( x - vertices._1 )) dot n) < 0 ||
        (((vertices._3 - vertices._2) cross ( x - vertices._2 )) dot n) < 0 ||
        (((vertices._4 - vertices._3) cross ( x - vertices._3 )) dot n) < 0 ||
        (((vertices._1 - vertices._4) cross ( x - vertices._4 )) dot n) < 0 ) return None

    Some(x)
  }
}

object Primitive2D extends GLThis {


  def quad = {
    val mesh = new GdxMesh(true,4,6, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0))
    mesh.setVertices( Array[Float](
      -1,-1,0, 0,0,1,   0,0,
      1,-1,0,  0,0,1,   1,0,
      1,1,0,   0,0,1,   1,1,
      -1,1,0,  0,0,1,   0,1
    ))

    mesh.setIndices( Array[Short](
      0,1,2, 0,2,3
    ))
    val draw = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
    mesh //new GLPrimitive(Pose(),Vec3(1.f),mesh,draw)
  }


}


