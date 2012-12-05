
package com.fishuyo
package trees

import maths._
import graphics._

//import javax.media.opengl._
import java.nio.FloatBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.GL10

object Trees extends GLAnimatable {

  var g = -10.f
  var gv = Vec3(0,-10.f,0)

  var trees = TreeNode(Vec3(0), .1f) :: List()
  trees(0).branch( 6, 45.f, .8f, 0 )
  override def step( dt: Float ) = trees.foreach( _.step(dt) )
  //override def onDraw( gl: GL2 ) = trees.foreach( _.onDraw(gl) )
  override def draw() = trees.foreach( _.draw() )

}

object TreeNode {
  def apply( p:Vec3, d: Float ) = new TreeNode { pos = p; lPos = p; dist = d; pinned = true; } 
  def apply( p:TreeNode, ang: Float, d: Float, stif: Float =.5f ) = {
    val z_offset = util.Random.nextFloat * 2.f - 1.f
    val v = Vec3( math.cos( ang * math.Pi/180.f ), math.sin( ang * math.Pi/180.f ), 0.f ) * d + p.pos
    new TreeNode { parent = Some(p); pos = v; lPos = v; dist = d; stiff = stif; angles.z = ang; }
  }
}

class TreeRoot( val node:TreeNode ) extends GLAnimatable {
  var size = 0
  var vertices:Array[Float] = null
  var mesh:Mesh = null

  override def draw(){
    if( size != node.size){
      size = node.size
      vertices = new Array[Float](3*2*size)
      mesh = new Mesh(false,2*size,0,VertexAttribute.Position)
    }
    if( size == 0) return
    var idx = 0;
    node.draw(vertices, idx)
    //node.draw(mesh.getVerticesBuffer, idx)
    //gl11.glColor4f(1.f,1.f,1.f,1.f)
    gl.glLineWidth( 2.f )

    mesh.setVertices(vertices)
    mesh.render( Shader(), GL10.GL_LINES)

  }
  override def step(dt:Float) = {
    if( Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
      Trees.gv.x = Gdx.input.getAccelerometerY
      Trees.gv.y = -Gdx.input.getAccelerometerX
      //Trees.gv.z = -Gdx.input.getAccelerometerZ
    }

    //for( s <- (0 until 1) ) node.solveConstraints()
    //node.step(dt)
  }
}

class TreeNode extends GLAnimatable {

  var pos = Vec3(0)
  var lPos = Vec3(0)
  var accel = Vec3(0)
  var angles = Vec3(0,0,90.f)
  var mass = 1.f
  var damp = 10.f
  var dist = 1.f
  var stiff = 1.f
  var w = 1.f
  var tearThresh = 1.f
  var thick = 1.f
  var pinned = false
  var size = 0;

  var parent:Option[TreeNode] = None
  var children = List[TreeNode]()

  def draw( v:Array[Float], idx:Int ):Int = {
    //gl10.glColor4f(1.f,1.f,1.f,1.f)
    //gl.glLineWidth( thick )
    //gli.begin( GL10.GL_LINES )
    //children.foreach( (n) => { gli.vertex(pos.x, pos.y, pos.z); gli.vertex( n.pos.x, n.pos.y, n.pos.z ) } )
    //gli.end
    var i = idx
    children.foreach( (n) => {
    //gli.end
     v(i) = pos.x; v(i+1) = pos.y; v(i+2) = pos.z
     v(i+3) = n.pos.x; v(i+4) = n.pos.y; v(i+5) = n.pos.z
     //v.put(i,pos.x); v.put(i+1,pos.y); v.put(i+2,pos.z)
     //v.put(i+3,n.pos.x); v.put(i+4,n.pos.y); v.put(i+5,n.pos.z)
     i += 6
     i = n.draw(v,i)
    })
    i
    //children.foreach( (n) => n.draw(v,i) )
  }
  /*override def onDraw( gl: GL2 ) = {
    gl.glColor3f(1.f,1.f,1.f)
    gl.glLineWidth( thick )
    gl.glBegin( GL.GL_LINES )
    children.foreach( (n) => { gl.glVertex3f(pos.x, pos.y, pos.z); gl.glVertex3f( n.pos.x, n.pos.y, n.pos.z ) } )
    gl.glEnd

    children.foreach( (n) => n.onDraw(gl) )
  }*/

  override def step( dt: Float ) = {

    if( !pinned ){
      accel += Trees.gv //Vec3( 0, Trees.g, 0 )

      //verlet integration
      val v = pos - lPos
      accel -= v * ( damp / mass )
      lPos = pos
      pos = pos + v + accel * ( .5f * dt * dt )

      accel.zero
    }
  }

  def applyForce( f: Vec3 ) : Vec3 = {
    accel += f / mass
    children.foreach( (n) => n.applyForce( f * 2.f) )
    f
  }

  def solveConstraints() : Unit = {
    children.foreach( (n) => {

      n.solveConstraints()
      val d = pos - n.pos
      val mag = d.mag
      if( mag == 0.f ) return
      val diff = (dist - mag) / mag

      //tear here

      pos = pos + d * w * diff
      n.pos = n.pos - d * n.w * diff

      //todo angle constraints
    })

    if( pinned ) pos = lPos;
  }

  def pinTo( p: Vec3 ) = {
    pos = p
    lPos = p
    pinned = true
  }

  def branch( depth: Int, angle: Float=10.f, ratio: Float=.9f, t:Int=0 ) : Int = {

    thick = depth.toFloat
    if( depth == 0 ) return 0
    children = TreeNode( this, angles.z - angle, dist * ratio*ratio ) :: TreeNode(this, angles.z, dist*ratio) ::TreeNode( this, angles.z + angle, dist * ratio*ratio ) :: List[TreeNode]() //children
    size = 3;
    children.foreach( (n) => size += n.branch( depth - 1, angle, ratio) );
    size
  }

  def writePoints( file: String ) = {}

}

