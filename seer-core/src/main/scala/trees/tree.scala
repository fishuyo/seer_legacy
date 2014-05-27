
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
import com.badlogic.gdx.graphics.Texture._

import com.badlogic.gdx.graphics.GL20

object Trees extends Animatable {

  def setDamp(v:Float) = damp = v
  var damp = 100.f
	var t = 0.f
  var g = -10.f
  var gv = Vec3(0,-10.f,0)

  //var trees = TreeNode(Vec3(0), .1f) :: List()
  //trees(0).branch( 6, 45.f, .8f, 0 )
  //override def animate( dt: Float ) = trees.foreach( _.animate(dt) )
  //override def onDraw( gl: GL2 ) = trees.foreach( _.onDraw(gl) )
  //override def draw() = trees.foreach( _.draw() )

}

object Tree {
  def apply(pos:Vec3=Vec3(0)) = new Tree(){ root.pose.pos = pos; }
}

class Tree() extends Animatable {
  
  var root = TreeNode()

  val sLength = Randf(1.f,1.f,true)
  val sRatio = Randf(.8f,.8f,true)
  val bLength = Randf(1.f,1.f,true)
  val bRatio = Randf(.6f,.6f,true)
  val sThick = Randf(1.f,1.f,true)
  val taper = Randf(.9f,.9f,true)
  val sAngle = RandVec3( Vec3(0,0,0), Vec3(0,0,0), true)
  val bAngle = RandVec3( Vec3(0,10.f.toRadians,0), Vec3(0,10.f.toRadians,0), true)

  def update(mz:Float,rx:Float,ry:Float,rz:Float){
    bAngle.y.setMinMax( 0.05, ry,false )
    // ##bAngle.y.set(mx)
    sRatio.setMinMax( 0.05, mz, false )
    // ## sRatio.set( mz )
    bRatio.setMinMax( 0.05, mz, false )
    // ##bRatio.set( my )
    sAngle.x.setMinMax( 0.05, rx, false )
    bAngle.x.setMinMax( 0.05, rx, false )
    // ##sAngle.x.set( rx )
    sAngle.z.setMinMax( 0.05, rz, false )
    bAngle.z.setMinMax( 0.05, rz, false )
    // ##sAngle.z.set( rz )
    // ##branch(depth)
    refresh()
  }

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

  val branchNum = Chooser(Array(0,1,2),Array(0.f,1.f,0.f))

  var size = 0
  // var vertices:Array[Float] = null
  // var mesh:Mesh = null
  var textureID = 0;
  var textureID1 = 1;


  var animating = false
  def setAnimate(b:Boolean) = animating = b

  def branch(depth:Int=bDepth):Int = { if(reseed) Randf.gen.setSeed(seed+mseed); root.dist=sLength(); branch(root,depth) }

  def branch( n: TreeNode, depth:Int):Int = {
    var branches = List[TreeNode]()

    if( depth == 0 ) return 0

    //current branch continuation
    var dist = n.dist * sRatio()
    val q = Quat().fromEuler(sAngle())
    var quat = (n.pose.quat * q).normalize
    var pos = n.pose.pos + n.pose.quat.toZ()*n.dist
    var stem = TreeNode( Pose(pos,quat), dist, n.thick * n.taper, q )

    //branch n times
    var sign = -1
    val count = branchNum()
    for( i <- (0 until count) ){
      sign *= -1
      dist = n.dist * bRatio()
      val qb = Quat().fromEuler(bAngle()*sign)
      quat = (quat * qb).normalize
      //pos = n.pose.pos + quat.toZ()*n.dist

      branches = TreeNode( Pose(pos,quat), dist, n.thick * n.taper, qb ) :: branches
    }

    n.children = stem :: branches
    n.size = count + 1

    n.children.foreach( (t) => n.size += branch( t, depth - 1) )
    n.mass = n.size.toFloat
    n.size
  }

  override def init(){
    // val t = Texture("res/bark/bark_20091031_07_256x512_seamless.jpg")
    // val t1 = Texture("res/bark/mond.png")
    // textureID = t
    // textureID1 = t1

    // Texture(t).setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // Texture(t).setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    // Texture(t1).setFilter(TextureFilter.Linear, TextureFilter.Linear);
    // Texture(t1).setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
  }
  override def draw(){
    if( size != root.size){
      size = root.size
      // vertices = new Array[Float](3*2*size)
      // mesh = new Mesh(false,2*size,0,VertexAttribute.Position)
    }
    if( size == 0) return
    var idx = 0;

    // val t = Texture(textureID).getTextureObjectHandle
    // val t1 = Texture(textureID1).getTextureObjectHandle
    // // Texture(textureID).bind(t)
    // Texture(textureID1).bind(t1)
    // Shader().setUniformi("u_texture0", t1 );
    // //Shader().setUniformi("u_texture1", t1 );

    // Texture.bind(textureID1)

    root.draw() //vertices, idx)

    //gl.glLineWidth( 1.f )

    //mesh.setVertices(vertices)
    //mesh.render( Shader(), GL20.GL_LINES)

  }
  override def animate(dt:Float) = {

  	Trees.t += dt

    if( Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
      Trees.gv.x = Gdx.input.getAccelerometerY
      Trees.gv.y = -Gdx.input.getAccelerometerX
      //Trees.gv.z = -Gdx.input.getAccelerometerZ
    }

    if( dirty ){ branch(); dirty = false }    

    if(animating){
      root.animate(dt)
      root.solveConstraints()
    }
  }

}

object TreeNode {

	var taper=.8f
	var model = Model(Cylinder.generateMesh(1.f,taper,10)) //Primitive3D.cylinder(Pose(),Vec3(1), 1.f, taper, 10)

  def apply( p:Pose=Pose(Vec3(0),Quat().fromEuler(Vec3(-math.Pi/2,0,0))), d:Float=1.f, t:Float=.2f, q:Quat=Quat() ) = new TreeNode {
  	restPose = Pose(p);
  	relQuat = q;
  	pose = p;
  	lPose = Pose(p);
  	//lPos = p.pos; 
  	dist = d;
  	thick = t
  	mass = d*t*1.f;
  	euler = Vec3(0)
  	lEuler = Vec3(0)

  	r = 1/(dist*dist) * .1f
    w = Randf(0,3.14f)()
    f = 1.f/dist * 1.f

    //k = 10.f * thick*thick*thick / (4*dist*dist*dist)
    k = 10.f * thick/ (4*dist)
  	//model = GLPrimitive.cylinder(pose,Vec3(1,1,d), d*thick, d*thick*taper, 10)
 	} 
  /*def apply( p:TreeNode, ang: Float, d: Float ) = {
    
    val v = Vec3( math.cos( ang * math.Pi/180.f ), math.sin( ang * math.Pi/180.f ), 0.f ) * d + p.pos
    new TreeNode {  pos = v; lPos = v; dist = d; angles.z = ang; }
  }*/
}

class TreeNode extends Animatable {

  var pose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  var lPose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  var restPose = Pose(Vec3(0), Quat().fromEuler(Vec3(math.Pi/2,0,0)))
  var relQuat = Quat()

  //var pos = Vec3(0)
  // var lPos = Vec3(0)
  var accel = Vec3(0)
  var euler = Vec3(0.f,0.f,0.f)
  var lEuler = Vec3(0.f,0.f,0.f)
  var mass = 1.f
  var damp = 100.f

  var dist = 1.f
  
  var thick = 1.f
  var taper = .8f
  var pinned = false
  var size = 0;

  var r = 1/(dist*dist) * .1f
  var w = Randf(0,3.14f)()
  var f = 1.f/dist * 1.f
  var k = 10.f * thick*thick*thick / (dist*dist*dist)
  //var model:GLPrimitive = _

  //var parent:Option[TreeNode] = None
  var children = List[TreeNode]()

  override def draw( ){ //v:Array[Float], idx:Int ):Int = {

		TreeNode.model.pose = pose
		TreeNode.model.scale.set(dist*thick,dist*thick,dist) //*1.05f)
		TreeNode.model.draw();
    // var i = idx
     children.foreach( (n) => { n.draw() })//{
    //  v(i) = pose.pos.x; v(i+1) = pose.pos.y; v(i+2) = pose.pos.z
    //  v(i+3) = n.pose.pos.x; v(i+4) = n.pose.pos.y; v(i+5) = n.pose.pos.z
    //  i += 6
    //  i = n.draw(v,i)
    // })
    // i
    
  }

  override def animate( dt: Float ) = {

    children.foreach( _.animate(dt) )


  	//euler = (restPose.quat.inverse * pose.quat).toEulerVec()
  	val w = euler - lEuler
  	val ax = accel dot restPose.quat.toX
  	val ay = accel dot restPose.quat.toY
  	var dw = Vec3(ay, ax, 0)
  	
  	dw -= w * (Trees.damp / mass)
  	dw -= euler * k
  	lEuler.set(euler)
  	euler = euler + w + dw * (.5f*dt*dt)

  	pose.quat = restPose.quat * Quat().fromEuler(euler.x,euler.y,euler.z)

  	accel.zero


  	// sinusoid animation
  	//pose.quat = restPose.quat * Quat().fromEuler(Vec3(r*math.sin(f*Trees.t+w),r*math.sin(f*Trees.t+w),0))
    
  }

  def applyForce( f: Vec3 ) : Vec3 = {
    accel += f / mass
    children.foreach( (n) => n.applyForce( f ) )
    f
  }

  def solveConstraints() : Unit = {
    children.foreach( (n) => {

    	val pos = pose.pos + pose.quat.toZ()*dist
    	n.pose.pos = pos
    	n.restPose.quat = pose.quat * n.relQuat
    	n.pose.quat = n.restPose.quat * Quat().fromEuler(n.euler.x,n.euler.y,n.euler.z)

      n.solveConstraints()


      //tear here

      // pose.pos = pose.pos + d * diff
      // n.pose.pos = n.pose.pos - d * diff

    })

    //if( pinned ) pose.pos = lPos;
  }

  def pin( p:Vec3 = pose.pos ) = {
    //pose.pos = p
    //lPos = p
    //pinned = true
  }

  /*def branch( depth: Int, angle: Float=10.f, ratio: Float=.9f, t:Int=0 ) : Int = {

    thick = depth.toFloat
    if( depth == 0 ) return 0
    children = TreeNode( this, angles.z - angle, dist * ratio*ratio ) :: TreeNode(this, angles.z, dist*ratio) ::TreeNode( this, angles.z + angle, dist * ratio*ratio ) :: List[TreeNode]() //children
    size = 3;
    children.foreach( (n) => size += n.branch( depth - 1, angle, ratio) );
    size
  }*/

  // def writePoints( file: String ) = {}

  // def getBoundingBox() = {
  //   val p1 = pose.pos
  //   val p2 = pose.pos + pose.quat.toZ()*dist
  //   val min = 
  //   root.children.foreach( (n) => {
  //     val bb = n.getBoundingBox

  //   })
  //   (min,max)
  // }

  def getMaxHeight():Float = {
    val p1 = pose.pos.y
    val p2 = (pose.pos + pose.quat.toZ()*dist).y
    var max = (if( p1 < p2) p2 else p1)
    children.foreach( (n) => {
      val m = n.getMaxHeight
      if(m > max) max = m
    })
    max
  }

}

