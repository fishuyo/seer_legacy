
package com.fishuyo
package trees

import maths._
import graphics._

import javax.media.opengl._

import com.badlogic.gdx.graphics.GL10

object Trees extends GLAnimatable {

  var g = -10.f

  var trees = TreeNode(Vec3(0), .1f) :: List()
  trees(0).branch( 6, 45.f, .8f, 0 )
  override def step( dt: Float ) = trees.foreach( _.step(dt) )
  override def onDraw( gl: GL2 ) = trees.foreach( _.onDraw(gl) )
  override def draw() = trees.foreach( _.draw() )

}

object TreeNode {
  def apply( p:Vec3, d: Float ) = new TreeNode { pos = p; lPos = p; dist = d; pinned = true; } 
  def apply( p:TreeNode, ang: Float, d: Float, stif: Float =.5f ) = {
    val v = Vec3( math.cos( ang * math.Pi/180.f ), math.sin( ang * math.Pi/180.f ), util.Random.nextFloat * 2.f - 1.f ) * d + p.pos
    new TreeNode { parent = Some(p); pos = v; lPos = v; dist = d; stiff = stif; angles.z = ang; }
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

  var parent:Option[TreeNode] = None
  var children = List[TreeNode]()

  override def draw() = {
    gl10.glColor4f(1.f,1.f,1.f,1.f)
    gl.glLineWidth( thick )
    gli.begin( GL10.GL_LINES )
    children.foreach( (n) => { gli.vertex(pos.x, pos.y, pos.z); gli.vertex( n.pos.x, n.pos.y, n.pos.z ) } )
    gli.end

    children.foreach( (n) => n.draw() )
  }
  override def onDraw( gl: GL2 ) = {
    gl.glColor3f(1.f,1.f,1.f)
    gl.glLineWidth( thick )
    gl.glBegin( GL.GL_LINES )
    children.foreach( (n) => { gl.glVertex3f(pos.x, pos.y, pos.z); gl.glVertex3f( n.pos.x, n.pos.y, n.pos.z ) } )
    gl.glEnd

    children.foreach( (n) => n.onDraw(gl) )
  }

  override def step( dt: Float ) = {

    if( !pinned ){
      accel += Vec3( 0, Trees.g, 0 )

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

      pos += d * w * diff
      n.pos -= d * n.w * diff

      //todo angle constraints
    })

    if( pinned ) pos = lPos;
  }

  def pinTo( p: Vec3 ) = {
    pos = p
    lPos = p
    pinned = true
  }

  def branch( depth: Int, angle: Float=10.f, ratio: Float=.9f, t:Int=0 ) : Unit = {

    thick = depth.toFloat
    if( depth == 0 ) return
    children = TreeNode( this, angles.z - angle, dist * ratio ) :: TreeNode( this, angles.z + angle, dist * ratio ) :: children
    children.foreach( _.branch( depth - 1) );
  }

  def writePoints( file: String ) = {}

}

