
package com.fishuyo
package trees

import maths._
import graphics._

//import javax.media.opengl._
import java.nio.FloatBuffer
import scala.collection.mutable.ListBuffer

import com.badlogic.gdx._
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.glutils.VertexBufferObject

object Fabric extends GLAnimatable {

  var g = -10.f
  var gv = Vec3(0.f,-10.f,0.f)
  var fabrics = List[Fabric]( new Fabric )

  def apply( p: Vec3, w: Float, h: Float, d: Float, m:String="xy") = new Fabric(p,w,h,d,m)

  override def step( dt: Float ) = fabrics.foreach( _.step(dt) )
  //override def onDraw( gl: GL2 ) = fabrics.foreach( _.onDraw(gl) )
  override def draw( ) = fabrics.foreach( _.draw() )


}

class Fabric( var pos:Vec3=Vec3(0), var width:Float=1.f, var height:Float=1.f, var dist:Float=.05f, mode:String="xy") extends GLAnimatable {

  var stiff = 1.f
  var particles = ListBuffer[VParticle]()
  var field:VecField3D = null

  val nx = (width / dist).toInt
  val ny = (height / dist).toInt

  val links = 2*nx*ny - nx - ny

  for( j <- ( 0 until ny ); i <- ( 0 until nx)){

    var x=0.f; var y=0.f; var p:VParticle = null

    mode match {
      case "xy" | "yx" => x = pos.x - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = VParticle( Vec3(x,y,pos.z), dist )
      case "zy" | "yz" => x = pos.z - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = VParticle( Vec3(pos.x,y,x), dist )
      case "xz" | "zx" => x = pos.x - width/2 + i*dist; y = pos.z + height/2 - j*dist; p = VParticle( Vec3(x,pos.y,y), dist )
      case _ => x = pos.x - width/2 + i*dist; y = pos.y + height/2 - j*dist; p = VParticle( Vec3(x,y,pos.z), dist )
    }

    if( i != 0 ) p.linkTo( particles(particles.length-1) )
    if( j != 0 ) p.linkTo( particles( (j-1) * nx + i) )
    if( (mode == "xz" || mode == "zx") && j > ny/4 && j < 3*ny/4 && i < 3*nx/4 && i>nx/4) p.pinTo( p.pos )
    else if( (mode != "xz" && mode != "zx") && j == 0 ) p.pinTo( p.pos )
    particles += p
  }
  
  var xt=0.f

  var vertices = new Array[Float](3*2*links)
  //var vbo:VertexBufferObject = null //new VertexBufferObject( false, 2*links, VertexAttribute.Position )
  var mesh:Mesh = null

  override def step( dt: Float ) = {

    if( Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
      Fabric.gv.x = -Gdx.input.getAccelerometerX
      Fabric.gv.y = -Gdx.input.getAccelerometerY
      //Fabric.gv.z = -Gdx.input.getAccelerometerZ
    }
    //val ts = .015f
    //val steps = ( (dt+xt) / ts ).toInt
    //xt = dt - steps * ts

    //for( t <- (0 until steps)){
      for( s <- (0 until 3) ) particles.foreach( _.solve() )
      if( field != null ) particles.foreach( (p:VParticle) => { p.applyForce( field(p.pos) ) } )
      particles.foreach( _.step(dt) )
    //}

  }

  override def draw() {
    //if( vbo == null ) vbo = new VertexBufferObject( false, 2*links, VertexAttribute.Position )
    if( mesh == null) mesh = new Mesh(false,2*links,0,VertexAttribute.Position)
    var i = 0;
    particles.foreach( (p) => i = p.draw(vertices, i) )
    //particles.foreach( (p) => i = p.draw(mesh.getVerticesBuffer, i) )
    gl11.glColor4f(1.f,1.f,1.f,1.f)
    gl.glLineWidth( 1.f )
    //vbo.setVertices( vertices, 0, vertices.length )
    //vbo.bind
    //gl11.glDrawArrays( GL10.GL_LINES, 0, vertices.length)
    mesh.setVertices(vertices)
    mesh.render( GL10.GL_LINES)
    
  }
  //override def onDraw( gl: GL2) = particles.foreach( _.onDraw(gl) )
  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

  def addField( f: VecField3D ) = field = f

}

object VParticle {
  def apply( p:Vec3, d: Float, s: Float = 1.f ) = new VParticle { pos = p; lPos = p; dist = d; stiff = s; } 
}

class VParticle extends GLAnimatable{
  var pos = Vec3(0)
  var lPos = Vec3(0)
  var accel = Vec3(0)
  var mass = 1.f
  var damp = 20.f
  var dist = 1.f
  var stiff = 1.f
  var thick = 1.f
  var w = .5f
  var tearThresh = 1.f
  var pinned = false
  var pinPos = Vec3(0)

  var links = List[VParticle]()

  def draw(v: Array[Float], idx:Int) : Int = {
    //gl10.glColor4f(1.f,1.f,1.f,1.f)
    //gl.glLineWidth( thick )
    //gli.begin( GL10.GL_LINES )
    var i = idx
    links.foreach( (n) => { //gli.vertex(pos.x, pos.y, pos.z); gli.vertex( n.pos.x, n.pos.y, n.pos.z ) } )
    //gli.end
     v(i) = pos.x; v(i+1) = pos.y; v(i+2) = pos.z
     v(i+3) = n.pos.x; v(i+4) = n.pos.y; v(i+5) = n.pos.z
     //v.put(i,pos.x); v.put(i+1,pos.y); v.put(i+2,pos.z)
     //v.put(i+3,n.pos.x); v.put(i+4,n.pos.y); v.put(i+5,n.pos.z)
     i += 6
    })
    return i
  }
  /*override def onDraw( gl: GL2 ) = {
    gl.glColor3f(1.f,1.f,1.f)
    gl.glLineWidth( thick )
    gl.glBegin( GL.GL_LINES )
    links.foreach( (n) => { gl.glVertex3f(pos.x, pos.y, pos.z); gl.glVertex3f( n.pos.x, n.pos.y, n.pos.z ) } )
    gl.glEnd
  }*/

  override def step( dt: Float ) = {

    if( !pinned ){
      accel += Fabric.gv

      //verlet integration
      val v = pos - lPos
      accel -= v * ( damp / mass )
      lPos = pos
      pos = pos + v + accel * ( .5f * dt * dt )

      accel.zero
    }
  }

  def applyForce( f: Vec3 ) = accel += (f / mass)

  def solve() : Unit = {
    links.foreach( (n) => {

      //n.solveConstraints()
      val d = pos - n.pos
      val mag = d.mag
      if( mag == 0.f ) return
      val diff = (dist - mag) / mag

      //tear here

      pos = pos + d * w * diff
      n.pos = n.pos - d * n.w * diff
    })

    if( pinned ) pos = lPos
  }

  def linkTo( p: VParticle ) = (links = links :+ p )

  def pinTo( p: Vec3 ) = {
    pos = p
    lPos = p
    pinPos = p
    pinned = true
  }


  def writePoints( file: String ) = {}

}

