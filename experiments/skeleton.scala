package com.fishuyo.seer
package skeleton

import graphics._
import io._
import maths._
import particle._
import dynamic._
import audio._
import util._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import com.badlogic.gdx.graphics.{Mesh => GdxMesh}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.Gdx

object Main extends App with Animatable{

  DesktopApp.loadLibs()
  Scene.push(this)

  val ground = Plane.generateMesh(20,20,100,100,Quat.up) //Cube().scale(2.f,0.1f,7.f).translate(0,-0.2f,3.5f)
  val groundM = Model(ground)
  groundM.material = new SpecularMaterial
  groundM.material.color.set(0.f,.1f,.4f,.5f)

  val trace = new Trace3D(100)

  val skeletons = new ListBuffer[Skeleton]()
  for( i <- (0 until 5)) skeletons += new Skeleton(i)
  skeletons(1).setColor(RGBA(1.f,1.f,1.f,.5f))
  skeletons(2).setColor(RGBA(0.f,.7f,.1f,.5f))
  skeletons(3).setColor(RGBA(.8f,.2f,.2f,.5f))
  skeletons(4).setColor(RGBA(.8f,.8f,.0f,.5f))


  val stream = new ListBuffer[Skeleton]()
  for( i<-(0 until 10)) stream += new Skeleton(i)

  val fabricNode = new RenderNode
  val fabric = new SpringMesh( Plane.generateMesh(.4f,.6f,20,30), 1.f) //Sphere()
  fabric.particles.grouped(20).zipWithIndex.foreach{ case (xs,i) => xs.foreach(_.mass = (i+1)/30.f) }
  fabric.springs.foreach( _.updateWeights())
  fabric.pins += AbsoluteConstraint( fabric.particles.takeRight(20).head, Vec3(-1,1,0))
  fabric.pins += AbsoluteConstraint( fabric.particles.takeRight(10).head, Vec3(1,1,0))
  fabric.pins += AbsoluteConstraint( fabric.particles.last, Vec3(1,1,0))
  val fabric2 = new SpringMesh( Plane.generateMesh(.4f,.6f,20,30), 1.f) //Sphere()
  fabric2.particles.grouped(20).zipWithIndex.foreach{ case (xs,i) => xs.foreach(_.mass = (i+1)/30.f) } 
  fabric2.springs.foreach( _.updateWeights())
  fabric2.pins += AbsoluteConstraint( fabric2.particles.takeRight(20).head, Vec3(-1,1,0))
  fabric2.pins += AbsoluteConstraint( fabric2.particles.takeRight(10).head, Vec3(-1,1,0))
  fabric2.pins += AbsoluteConstraint( fabric2.particles.last, Vec3(1,1,0))

  val fabric3 = new SpringMesh( Plane.generateMesh(.2f,.4f,10,20), 1.f) //Sphere()
  fabric3.pins += AbsoluteConstraint( fabric3.particles.head, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.take(10).last, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.takeRight(10).head, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.last, Vec3(1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles(90), Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles(99), Vec3(1,1,0))
  val fabricM = Model(fabric)
  val fabricM2 = Model(fabric2)
  val fabricM3 = Model(fabric3)
  var drawFabric = false
  def drawFabric(b:Boolean){drawFabric = b}
  // fabric.mesh.primitive = Lines
  // fabric2.mesh.primitive = Lines
  fabricM.material = Material.specular
  fabricM.material.color.set(1,0,0,1)
  fabricM2.material = Material.specular
  fabricM2.material.color.set(1,0,0,1)
  fabricM3.material = Material.specular
  fabricM3.material.color.set(1,0,0,1)
  fabricNode.camera = Camera
  fabricNode.scene.push(fabric)
  fabricNode.scene.push(fabric2)
  // SceneGraph.addNode(node)

  var t = 0.f
  var skeletonTrail = false
  var trailDelay = 0.5f
  var trailOffset = Vec3(0,0,-.25f)

  val live = new Ruby("skeleton.rb")

  DesktopApp.run()  


  override def init(){

    val skyNode = new RenderNode
    skyNode.shader = "sky"
    skyNode.depth = false
    skyNode.scene.push( Plane() )
    // SceneGraph.prependNode(skyNode)
    SceneGraph.root.nodes.prepend(skyNode)

    val feedback = new RenderNode
    feedback.shader = "composite"
    feedback.clear = false
    feedback.scene.push(Plane())


    // SceneGraph.root.outputTo(feedback)
    // feedback.outputTo(feedback)
    // feedback.outputTo(ScreenNode)

    live.init()
  }

  override def draw(){

    live.draw()
    groundM.draw()
    // trace.draw()
    
    skeletons.foreach( _.draw() )
    if( skeletonTrail ){
      skeletons.foreach( s => {
        MatrixStack.push()
        stream.foreach( x => {
          MatrixStack.translate(trailOffset)
          x.draw()
        })
        MatrixStack.pop()
      })
    }
    if( drawFabric ){
      fabricM.draw()
      fabricM2.draw()
      // fabricM3.draw()
    }
  }

  override def animate(dt:Float){
    t += dt
    live.animate(dt)
    skeletons.foreach( _.animate(dt) )
    if( skeletonTrail && t > trailDelay ){
      skeletons.foreach( s => {
        val xs = (s :: stream.toList).zip(stream).reverse
        xs.foreach( ss => {ss._2.joints = ss._1.joints.clone; ss._2.tracking = true } )
      })
      t = 0.f
    }
    fabric.animate(dt)
    fabric2.animate(dt)
    // fabric3.animate(dt)
  }


  def warpGround(){
      ground.vertices.foreach( (v) => v.set(v.x,v.y+ Random.float(-1,1)()*0.02*(v.x).abs, v.z) )
      ground.recalculateNormals()
      ground.update()
  }

  def resetGround(){
    ground.clear()
    Plane.generateMesh(ground,20,20,100,100,Quat.up)
  }

}


class Skeleton(val id:Int) extends Animatable {

  var color = RGBA(0.f,.7f,.1f,.5f)

  var calibrating = false
  val loadingModel = Cube().scale(0.1f).translate(0,0.5f,0)
  val m = Cube().rotate(45.f.toRadians,0,45.f.toRadians)
  loadingModel.addPrimitive(m)
  m.material.color = color
  loadingModel.material.color = color

  var tracking = false
  var joints = Map[String,Model]()
  // var joints = Map[String,Trace3D]()

  joints += "head" -> Sphere().scale(.05f,.065f,.05f)
  joints += "neck" -> Sphere().scale(.02f)
  joints += "torso" -> Sphere().scale(.07f,.10f,.05f)
  joints += "r_shoulder" -> Sphere().scale(.02f)
  joints += "r_elbow" -> Sphere().scale(.02f)
  joints += "r_hand" -> Sphere().scale(.02f)
  joints += "l_shoulder" -> Sphere().scale(.02f)
  joints += "l_elbow" -> Sphere().scale(.02f)
  joints += "l_hand" -> Sphere().scale(.02f)
  joints += "r_hip" -> Sphere().scale(.03f)
  joints += "r_knee" -> Sphere().scale(.02f)
  // joints += "r_ankle" -> Sphere().scale(.02f)
  joints += "r_foot" -> Sphere().scale(.02f)
  joints += "l_hip" -> Sphere().scale(.03f)
  joints += "l_knee" -> Sphere().scale(.02f)
  // joints += "l_ankle" -> Sphere().scale(.02f)
  joints += "l_foot" -> Sphere().scale(.02f)
  
  // joints += "head" -> new Trace3D(100)
  // joints += "neck" -> new Trace3D(100)
  // joints += "torso" -> new Trace3D(100)
  // joints += "r_shoulder" -> new Trace3D(100)
  // joints += "r_elbow" -> new Trace3D(100)
  // joints += "r_hand" -> new Trace3D(100)
  // joints += "l_shoulder" -> new Trace3D(100)
  // joints += "l_elbow" -> new Trace3D(100)
  // joints += "l_hand" -> new Trace3D(100)
  // joints += "r_hip" -> new Trace3D(100)
  // joints += "r_knee" -> new Trace3D(100)
  // // joints += "r_ankle" -> new Trace3D(100)
  // joints += "r_foot" -> new Trace3D(100)
  // joints += "l_hip" -> new Trace3D(100)
  // joints += "l_knee" -> new Trace3D(100)
  // // joints += "l_ankle" -> new Trace3D(100)
  // joints += "l_foot" -> new Trace3D(100)

  joints.values.foreach( _.material.color = color )

  val bones = new ListBuffer[Model]()
  for( i <- (0 until 8)) bones += Cylinder()
  bones.foreach( (b) => { b.material.color = color; b.scale.set(.015f,.015f,.15f) })


  def setShader(s:String){
    joints.values.foreach(_.shader = s)
    bones.foreach(_.shader = s)
  }

  def setJoints(s:Skeleton){
    joints = s.joints.clone
  }

  override def draw(){
    if(calibrating) loadingModel.draw()
    if(tracking){ 
      joints.values.foreach(_.draw())
      bones.foreach(_.draw())
    }
  }

  override def animate(dt:Float){
    loadingModel.rotate(0,0.10f,0)
    if(tracking){
      // joints.values.foreach( j => {
// 
      // })
      bones(0).pose.pos.set(joints("l_shoulder").pose.pos)
      var a = joints("l_elbow").pose.pos - joints("l_shoulder").pose.pos
      bones(0).scale.z = a.mag()
      bones(0).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(1).pose.pos.set(joints("l_elbow").pose.pos)
      a = joints("l_hand").pose.pos - joints("l_elbow").pose.pos
      bones(1).scale.z = a.mag()
      bones(1).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(2).pose.pos.set(joints("r_shoulder").pose.pos)
      a = joints("r_elbow").pose.pos - joints("r_shoulder").pose.pos
      bones(2).scale.z = a.mag()
      bones(2).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(3).pose.pos.set(joints("r_elbow").pose.pos)
      a = joints("r_hand").pose.pos - joints("r_elbow").pose.pos
      bones(3).scale.z = a.mag()
      bones(3).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(4).pose.pos.set(joints("l_hip").pose.pos)
      a = joints("l_knee").pose.pos - joints("l_hip").pose.pos
      bones(4).scale.z = a.mag()
      bones(4).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(5).pose.pos.set(joints("l_knee").pose.pos)
      a = joints("l_foot").pose.pos - joints("l_knee").pose.pos
      bones(5).scale.z = a.mag()
      bones(5).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(6).pose.pos.set(joints("r_hip").pose.pos)
      a = joints("r_knee").pose.pos - joints("r_hip").pose.pos
      bones(6).scale.z = a.mag()
      bones(6).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

      bones(7).pose.pos.set(joints("r_knee").pose.pos)
      a = joints("r_foot").pose.pos - joints("r_knee").pose.pos
      bones(7).scale.z = a.mag()
      bones(7).pose.quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    }
  }

  def setColor(c:RGBA){
    color = c
    loadingModel.material.color = c
    m.material.color = c
    // joints.values.foreach( _.material.color = color)
    bones.foreach( _.material.color = color)
  }
  def calibrating(v:Boolean){ calibrating = v }
  def tracking(v:Boolean){ tracking = v }
}




class SpringString( var pos:Vec3=Vec3(0), var length:Float=0.2f, var dist:Float=.01f, var stiff:Float=1.f) extends Animatable {

  var particles = ListBuffer[Particle]()
  var links = ListBuffer[LinearSpringConstraint]()
  var pins = ListBuffer[AbsoluteConstraint]()

  val numLinks = (length / dist).toInt

  var damping = 20.f

  for( i<-(0 to numLinks)){
    val p = Particle(pos)
    if( i > 0){
      links += LinearSpringConstraint(p,particles(i-1),dist,stiff)
    }
    particles += p
  }

  pins += AbsoluteConstraint(particles(0), Vec3(pos))
  pins += AbsoluteConstraint(particles.last, Vec3(pos))

  var vertices = new Array[Float](3*2*numLinks)
  var mesh:GdxMesh = null

  override def animate( dt: Float ) = {

    for( s <- (0 until 5) ){ 
      links.foreach( _.solve() )
      links = links.filter( (l) => !l.isTorn )
      pins.foreach( _.solve() )
    }

    particles.foreach( (p) => {
      // p.applyForce(Gravity)
      p.applyDamping(damping)
      p.step() 
    })
  }

  override def draw() {
    Shader.setColor(RGBA(0,1,0,1))
    MatrixStack.clear()
    Shader.setMatrices()
    if( mesh == null) mesh = new GdxMesh(false,2*numLinks,0,VertexAttribute.Position)
    var i = 0
    var off = 0
    for( i<-(0 until links.size)){
      // if( !links(i).isTorn ){
        val p = links(i).p.position
        val q = links(i).q.position
        val v = i+off
        vertices(6*v) = p.x
        vertices(6*v+1) = p.y
        vertices(6*v+2) = p.z
        vertices(6*v+3) = q.x
        vertices(6*v+4) = q.y
        vertices(6*v+5) = q.z
      // } else off -= 1
    }
    mesh.setVertices(vertices,0,links.size*6)
    Gdx.gl.glLineWidth(8)
    mesh.render( Shader(), GL20.GL_LINES)    
  }

  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

}


