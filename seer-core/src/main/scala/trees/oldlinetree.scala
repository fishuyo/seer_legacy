
package com.fishuyo.seer
package trees

import maths._
import graphics._
import spatial._
import util._

//import javax.media.opengl._
import java.nio.FloatBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}
import com.badlogic.gdx.graphics.GL10

object LineTrees extends Animatable {

  var g = -10.f
  var gv = Vec3(0,-10.f,0)

  //var trees = LineTreeNode(Vec3(0), .1f) :: List()
  //trees(0).branch( 6, 45.f, .8f, 0 )
  //override def animate( dt: Float ) = trees.foreach( _.animate(dt) )
  //override def onDraw( gl: GL2 ) = trees.foreach( _.onDraw(gl) )
  //override def draw() = LineTrees.foreach( _.draw() )

}

object LineTree {
  def apply(pos:Vec3=Vec3(0)) = new LineTree(){ root.pose.pos = pos; }
}

class LineTree() extends Animatable {
  
  var root = LineTreeNode()

  val sLength = Randf(1.f,1.f,true)
  val sRatio = Randf(.8f,.8f,true)
  val bLength = Randf(1.f,1.f,true)
  val bRatio = Randf(.6f,.6f,true)
  val sThick = Randf(1.f,1.f,true)
  val taper = Randf(.9f,.9f,true)
  val sAngle = RandVec3( Vec3(0,0,0), Vec3(0,0,0), true)
  val bAngle = RandVec3( Vec3(0,10.f.toRadians,0), Vec3(0,10.f.toRadians,0), true)

  def set(r:Randf, s:Float=1.f) = (v:Seq[Float]) => {r.set(v(0)*s); dirty=true}
  def setMin( r:Randf, s:Float=1.f) = (v:Float) => {r.min = v*s; dirty=true}
  def setMax( r:Randf, s:Float=1.f) = (v:Float) => {r.max = v*s; dirty=true}
  def setMinMax( r:Randf, s:Float=1.f) = (v1:Float,v2:Float) => {r.min = v1*s; r.max = v2*s; dirty=true}
  var dirty = true
  def refresh() = dirty = true

  var seed:Long = scala.util.Random.nextLong
  var mseed:Long = 0
  var reseed = true
  def setReseed(b:Boolean) = reseed = b
  def setSeed() = (f:Float) => {mseed = (100*f).toLong; dirty = true}

  var bDepth:Int = 8
  def setDepth(i:Int) = bDepth = i

  val branchNum = Chooser(Array(0,1,2),Array(0.f,0,1.f))

  var size = 0
  var vertices:Array[Float] = null
  var mesh:GdxMesh = null

  var animating = false
  def setAnimate(b:Boolean) = animating = b

  def branch(depth:Int=bDepth):Int = { if(reseed) Randf.gen.setSeed(seed+mseed); root.dist=sLength(); branch(root,depth) }

  def branch( n: LineTreeNode, depth:Int):Int = {
    var branches = List[LineTreeNode]()

    if( depth == 0 ) return 0

    //current branch continuation
    var dist = n.dist * sRatio()
    var quat = (n.pose.quat * Quat().fromEuler(sAngle())).normalize
    var pos = n.pose.pos - quat.toZ()*dist
    var stem = LineTreeNode( Pose(pos,quat), dist, n.thick * sThick() )

    //branch n times
    var sign = -1
    val count = branchNum()
    for( i <- (0 until count) ){
      sign *= -1
      dist = n.dist * bRatio()
      quat = (quat * Quat().fromEuler(bAngle()*sign)).normalize
      pos = n.pose.pos - quat.toZ()*dist

      branches = LineTreeNode( Pose(pos,quat), dist, n.thick * sThick() ) :: branches
    }

    n.children = stem :: branches
    n.size = count + 1

    n.children.foreach( (t) => n.size += branch( t, depth - 1) )
    n.mass = n.size.toFloat
    n.size
  }

  override def draw(){
    if( size != root.size){
      size = root.size
      vertices = new Array[Float](3*2*size)
      mesh = new GdxMesh(false,2*size,0,VertexAttribute.Position)
    }
    if( size == 0) return
    var idx = 0;
    root.draw(vertices, idx)

    gl.glLineWidth( 1.f )

    mesh.setVertices(vertices)
    mesh.render( Shader(), GL10.GL_LINES)

  }
  override def animate(dt:Float) = {
    if( Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
      LineTrees.gv.x = Gdx.input.getAccelerometerY
      LineTrees.gv.y = -Gdx.input.getAccelerometerX
      //LineTrees.gv.z = -Gdx.input.getAccelerometerZ
    }

    if( dirty ){ branch(); dirty = false }    

    if(animating){
      for( s <- (0 until 3) ) root.solveConstraints()
      root.animate(dt)
    }
  }
}

object LineTreeNode {
  def apply( p:Pose=Pose(Vec3(0),Quat().fromEuler(Vec3(math.Pi/2,0,0))), d:Float=1.f, t:Float=1.f ) = new LineTreeNode { pose = p; lPos = p.pos; dist = d; thick = t } 
  /*def apply( p:LineTreeNode, ang: Float, d: Float ) = {
    
    val v = Vec3( math.cos( ang * math.Pi/180.f ), math.sin( ang * math.Pi/180.f ), 0.f ) * d + p.pos
    new LineTreeNode {  pos = v; lPos = v; dist = d; angles.z = ang; }
  }*/
}

class LineTreeNode extends Animatable {

  var pose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  var pos = Vec3(0)
  var lPos = Vec3(0)
  var accel = Vec3(0)
  var angles = Vec3(0,0,90.f)
  var mass = 1.f
  var damp = 1000.f

  var dist = 1.f
  
  var thick = 1.f
  var pinned = false
  var size = 0;

  //var parent:Option[LineTreeNode] = None
  var children = List[LineTreeNode]()

  def draw( v:Array[Float], idx:Int ):Int = {

    var i = idx
    children.foreach( (n) => {
     v(i) = pose.pos.x; v(i+1) = pose.pos.y; v(i+2) = pose.pos.z
     v(i+3) = n.pose.pos.x; v(i+4) = n.pose.pos.y; v(i+5) = n.pose.pos.z
     i += 6
     i = n.draw(v,i)
    })
    i
    
  }

  override def animate( dt: Float ) = {

    if( !pinned ){
      accel += LineTrees.gv //Vec3( 0, LineTrees.g, 0 )

      //verlet integration
      val v = pose.pos - lPos
      accel -= v * ( damp / mass )
      lPos = pose.pos
      pose.pos = pose.pos + v + accel * ( .5f * dt * dt )

      accel.zero
    }
    children.foreach( _.animate(dt) )
  }

  def applyForce( f: Vec3 ) : Vec3 = {
    accel += f / mass
    children.foreach( (n) => n.applyForce( f * 2.f) )
    f
  }

  def solveConstraints() : Unit = {
    children.foreach( (n) => {

      n.solveConstraints()
      val d = pose.pos - n.pose.pos
      val mag = d.mag
      if( mag == 0.f ) return
      val diff = (n.dist - mag) / mag

      //tear here

      pose.pos = pose.pos + d * diff
      n.pose.pos = n.pose.pos - d * diff

      //todo angle constraints
    })

    if( pinned ) pose.pos = lPos;
  }

  def pin( p:Vec3 = pose.pos ) = {
    pose.pos = p
    lPos = p
    pinned = true
  }

  /*def branch( depth: Int, angle: Float=10.f, ratio: Float=.9f, t:Int=0 ) : Int = {

    thick = depth.toFloat
    if( depth == 0 ) return 0
    children = LineTreeNode( this, angles.z - angle, dist * ratio*ratio ) :: LineTreeNode(this, angles.z, dist*ratio) ::LineTreeNode( this, angles.z + angle, dist * ratio*ratio ) :: List[LineTreeNode]() //children
    size = 3;
    children.foreach( (n) => size += n.branch( depth - 1, angle, ratio) );
    size
  }*/

  def writePoints( file: String ) = {}

}

