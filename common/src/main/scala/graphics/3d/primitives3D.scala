
package com.fishuyo
package graphics
import maths._
import spatial._


import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Queue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._

import com.badlogic.gdx.graphics.g3d.loader._
//import com.badlogic.gdx.graphics.g3d.loaders.wavefront._
//import com.badlogic.gdx.graphics.g3d.model.still._





object Sphere{
	var sphere = None:Option[Sphere]
	var wireSphere = None:Option[Sphere]
	var pointSphere = None:Option[Sphere]
	def apply() = sphere.getOrElse({ sphere = Some(new Sphere()); sphere.get })
	def asLines() = wireSphere.getOrElse({ wireSphere = Some(new Sphere(1.f,30,"lines")); wireSphere.get })
	def asPoints() = pointSphere.getOrElse({ pointSphere = Some(new Sphere(1.f,30,"points")); pointSphere.get })
}
class Sphere(val radius:Float=1.f, val bands:Int=30, val style:String="triangles") extends GLDrawable {
  val vert = new ListBuffer[Float]
  val indx = new ListBuffer[Short]
  var mesh:Mesh = _
  var drawFunc = () => {}

  for ( lat <- (0 to bands)){
    var theta = lat * Math.Pi / bands
    var sinTheta = Math.sin(theta)
    var cosTheta = Math.cos(theta)

    for (long <- (0 to bands)){
      var phi = long * 2 * Math.Pi / bands
      var sinPhi = Math.sin(phi)
      var cosPhi = Math.cos(phi)
      var x = cosPhi * sinTheta
      var y = cosTheta
      var z = sinPhi * sinTheta
      var u = 1.0 - (long / bands)
      var v = lat / bands
      vert += x.toFloat
      vert += y.toFloat
      vert += z.toFloat
      vert += u.toFloat
      vert += v.toFloat
      vert += radius * x.toFloat
      vert += radius * y.toFloat
      vert += radius * z.toFloat
    }
  }

  style match {
  	case "points" =>
		  mesh = new Mesh(true, vert.size/8, 0, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
		  drawFunc = () => { mesh.render(Shader(), GL10.GL_POINTS)}

  	case "lines" =>
		  for ( lat <- (0 until bands)){
		    for ( long <- (0 until bands)){
		     var first = (lat * (bands + 1)) + long
		     var second = first + bands + 1
		     indx += first.toShort
		     indx += second.toShort
		     indx += second.toShort
		     indx += (first + 1).toShort
		     indx += (first + 1).toShort
		     indx += first.toShort
		     indx += second.toShort
		     indx += (second + 1).toShort
		     indx += (second + 1).toShort
		     indx += (first + 1).toShort
		     indx += (first + 1).toShort
		     indx += second.toShort
		   }
		  }
		  mesh = new Mesh(true, vert.size/8, indx.size, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
  		mesh.setIndices(indx.toArray)
		  drawFunc = () => { mesh.render(Shader(), GL10.GL_LINES)}

  	case _ => //"triangles"
		  for ( lat <- (0 until bands)){
		    for ( long <- (0 until bands)){
		     var first = (lat * (bands + 1)) + long
		     var second = first + bands + 1
		     indx += first.toShort
		     indx += second.toShort
		     indx += (first + 1).toShort
		     indx += second.toShort
		     indx += (second + 1).toShort
		     indx += (first + 1).toShort
		   }
		  }
		  mesh = new Mesh(true, vert.size/8, indx.size, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
  		mesh.setIndices(indx.toArray)
		  drawFunc = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
  }

  mesh.setVertices(vert.toArray)

  override def draw(){
    drawFunc()
  }
}

object Cube{
	var cube = None:Option[Cube]
	var wireCube = None:Option[Cube]
	def apply() = cube.getOrElse({ cube = Some(new Cube()); cube.get })
	def asLines() = wireCube.getOrElse({ wireCube = Some(new Cube("lines")); wireCube.get })
}
class Cube(style:String="triangles") extends GLDrawable {
	var mesh:Mesh = _
  var drawFunc = () => {}

	style match {
		case "lines" =>
  		mesh = new Mesh(true,8,24, VertexAttribute.Position )
		  mesh.setIndices( Array[Short](
		    0,1,1,3,3,2,2,0,
		    4,5,5,7,7,6,6,4,
		    0,4,1,5,2,6,3,7
		  ))
		  mesh.setVertices( Array[Float](
		    -1,1,1,   
		    1,1,1,    
		    -1,-1,1,  
		    1,-1,1,   

		    -1,1,-1,  
		    1,1,-1,    
		    -1,-1,-1,
		    1,-1,-1
		  ))
			drawFunc = () => { mesh.render(Shader(), GL10.GL_LINES)}
		case _ => 
  		mesh = new Mesh(true,24,36, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0) )
  		mesh.setIndices( Array[Short](
		    0,1,2, 1,2,3, //f
		    4,5,6, 5,6,7, //b
		    8,9,10, 9,10,11, //l
		    12,13,14, 13,14,15, //r
		    16,17,18, 17,18,19, //t
		    20,21,22, 21,22,23 //b
		  ))
		  mesh.setVertices( Array[Float](
		    -1,1,1,   0,0,1, 0,0,
		    1,1,1,    0,0,1, 1,0,
		    -1,-1,1,  0,0,1, 0,1,
		    1,-1,1,   0,0,1, 1,1,

		    -1,1,-1,  0,0,-1, 1,0,
		    1,1,-1,   0,0,-1, 0,0,
		    -1,-1,-1, 0,0,-1, 1,1,
		    1,-1,-1,  0,0,-1, 0,1,

		    -1,1,-1,  -1,0,0, 1,0,
		    -1,1,1,   -1,0,0, 0,0,
		    -1,-1,-1, -1,0,0, 1,1,
		    -1,-1,1,  -1,0,0, 0,1,

		    1,1,1,    1,0,0, 1,0,
		    1,1,-1,   1,0,0, 0,0,
		    1,-1,1,   1,0,0, 1,1,
		    1,-1,-1,  1,0,0, 0,1,

		    -1,1,-1,  0,1,0, 0,1,
		    1,1,-1,   0,1,0, 1,1,
		    -1,1,1,   0,1,0, 0,0,
		    1,1,1,    0,1,0, 1,0,

		    -1,-1,-1, 0,-1,0, 1,1,
		    1,-1,-1,  0,-1,0, 0,1,
		    -1,-1,1,  0,-1,0, 1,0,
		    1,-1,1,   0,-1,0, 0,0
		  ))
			drawFunc = () => { mesh.render(Shader(), GL10.GL_TRIANGLES)}
	}
  override def draw(){
    drawFunc()
  }
}

object Cylinder{
	var cylinder = None:Option[Cylinder]
	def apply() = cylinder.getOrElse({ cylinder = Some(new Cylinder()); cylinder.get })
}
class Cylinder(r1:Float=1.f, r2:Float=1.f, vertCount:Int=30) extends GLDrawable {
  val vert = new ListBuffer[Float]
  val indx = new ListBuffer[Short]

  val indxCount = vertCount+2
  var theta = 0.0
  var drawMode = GL10.GL_TRIANGLE_STRIP

  for (j <- (0 until vertCount)){
    val r = (if( 3*j % 2 == 0) r1 else r2)
    val x = math.cos(theta).toFloat
    val y = math.sin(theta).toFloat
    val u = (if(3 *j % 2 == 0) 1.f else 0.f)
    val v = j*1.f / vertCount

    vert += x
    vert += y
    vert += (r1-r2) / (2.f) //0.f

    vert += u
    vert += v

    vert += r*x
    vert += r*y
    vert += (if( 3*j % 2 == 0) 0.f else 2.f) //-1.f)
    theta += 2 * math.Pi / (vertCount)
  }

  for ( j <- (0 until indxCount)) {
    indx += (j % vertCount).toShort
  }

  val mesh = new Mesh(true, vert.size/6, indx.size, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
  mesh.setVertices(vert.toArray)
  mesh.setIndices(indx.toArray)

	override def draw(){
		mesh.render(Shader(), drawMode)
	}  
}

object OBJ {
  def apply(s:String) = Model(new OBJ(s))
}
class OBJ( file:String ) extends GLDrawable {
  val model = new ObjLoader().loadObj(Gdx.files.internal(file))
  override def draw() = model.meshes.get(0).render(Shader(), GL10.GL_TRIANGLES)
}


object Primitive3D extends GLThis { 
def cube( p:Pose = new Pose(), s:Vec3=Vec3(1)) = {
  val mesh = new Mesh(true,24,36, VertexAttribute.Position, VertexAttribute.Normal, VertexAttribute.TexCoords(0) )
  mesh.setVertices( Array[Float](
    -1,1,1,   0,0,1, 0,0,
    1,1,1,    0,0,1, 1,0,
    -1,-1,1,  0,0,1, 0,1,
    1,-1,1,   0,0,1, 1,1,

    -1,1,-1,  0,0,-1, 1,0,
    1,1,-1,   0,0,-1, 0,0,
    -1,-1,-1, 0,0,-1, 1,1,
    1,-1,-1,  0,0,-1, 0,1,

    -1,1,-1,  -1,0,0, 1,0,
    -1,1,1,   -1,0,0, 0,0,
    -1,-1,-1, -1,0,0, 1,1,
    -1,-1,1,  -1,0,0, 0,1,

    1,1,1,    1,0,0, 1,0,
    1,1,-1,   1,0,0, 0,0,
    1,-1,1,   1,0,0, 1,1,
    1,-1,-1,  1,0,0, 0,1,

    -1,1,-1,  0,1,0, 0,1,
    1,1,-1,   0,1,0, 1,1,
    -1,1,1,   0,1,0, 0,0,
    1,1,1,    0,1,0, 1,0,

    -1,-1,-1, 0,-1,0, 1,1,
    1,-1,-1,  0,-1,0, 0,1,
    -1,-1,1,  0,-1,0, 1,0,
    1,-1,1,   0,-1,0, 0,0
  ))

  mesh.setIndices( Array[Short](
    0,1,2, 1,2,3, //f
    4,5,6, 5,6,7, //b
    8,9,10, 9,10,11, //l
    12,13,14, 13,14,15, //r
    16,17,18, 17,18,19, //t
    20,21,22, 21,22,23 //b
  ))

  val draw = () => {

    // draw the cube
    mesh.render(Shader(), GL10.GL_TRIANGLES)
    //gl10.glPopMatrix()
  }
  new GLPrimitive(p,s,mesh,draw)
}

def cylinder(p:Pose=Pose(), s:Vec3=Vec3(1), r1:Float=1.f, r2:Float=1.f, vertCount:Int=60) = {
  val vert = new ListBuffer[Float]
  val indx = new ListBuffer[Short]

  val indxCount = vertCount+2
  var theta = 0.0

  for (j <- (0 until vertCount)){
    val r = (if( 3*j % 2 == 0) r1 else r2)
    val x = math.cos(theta).toFloat
    val y = math.sin(theta).toFloat
    val u = (if(3 *j % 2 == 0) 1.f else 0.f)
    val v = j*1.f / vertCount

    vert += x
    vert += y
    vert += (r1-r2) / (s.z) //0.f

    vert += u
    vert += v

    vert += r*x
    vert += r*y
    vert += (if( 3*j % 2 == 0) 0.f else 2.f) //-1.f)
    theta += 2 * math.Pi / (vertCount)
  }

  for ( j <- (0 until indxCount)) {
    indx += (j % vertCount).toShort
  }

  val mesh = new Mesh(true, vert.size/6, indx.size, VertexAttribute.Normal, VertexAttribute.TexCoords(0), VertexAttribute.Position)
  mesh.setVertices(vert.toArray)
  mesh.setIndices(indx.toArray)

  val draw = () => {
    mesh.render(Shader(), GL10.GL_TRIANGLE_STRIP)
  }
  new GLPrimitive(p,s,mesh,draw)
}

def fromObj( file:String ) = {

  val model = new ObjLoader().loadObj(Gdx.files.internal(file))
  val draw = () => { model.meshes.get(0).render(Shader(), GL10.GL_TRIANGLES)}
  new GLPrimitive(Pose(),Vec3(1.f),null,draw)
}
}



class Trace3D( var size:Int ) extends GLDrawable {
  var color1 = Vec3(1.f,0.f,0.f)
  var color2 = Vec3(0.f,0.f,1.f)
  var pose = Pose()
  var scale = Vec3(1.f)
  val mesh = new Mesh(false,size,0, VertexAttribute.Position, VertexAttribute.ColorUnpacked)
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
    Shader.setColor(color1,1.f)
    MatrixStack.push()
    MatrixStack.transform(pose,scale)
    Shader.setMatrices()
    mesh.render(Shader(), GL10.GL_LINE_STRIP)
    MatrixStack.pop()
  }

}




