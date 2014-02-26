package com.fishuyo.seer
package skeleton

import graphics._
import io._
import maths._
import particle._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import com.badlogic.gdx.graphics.{Mesh => GdxMesh}
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.Gdx

object Main extends App with Animatable{

  SimpleAppRun.loadLibs()

  Scene.push(this)

  val ground = Cube().scale(2.f,0.1f,7.f).translate(0,-0.2f,3.5f)
  ground.color.set(0.f,.1f,.4f,.5f)

  val skeletons = new ListBuffer[Skeleton]()
  for( i <- (0 until 5)) skeletons += new Skeleton(i)
  skeletons(1).setColor(RGBA(1.f,1.f,1.f,.5f))
  skeletons(2).setColor(RGBA(0.f,.7f,.1f,.5f))
  skeletons(3).setColor(RGBA(.8f,.2f,.2f,.5f))
  skeletons(4).setColor(RGBA(.8f,.8f,.0f,.5f))

  val node = new RenderNode
  val fabric = new SpringMesh( Plane.generateMesh(.4f,.2f,20,10), 1.f) //Sphere()
  fabric.pins += AbsoluteConstraint( fabric.particles.takeRight(20).head, Vec3(-1,1,0))
  fabric.pins += AbsoluteConstraint( fabric.particles.takeRight(10).head, Vec3(1,1,0))
  fabric.pins += AbsoluteConstraint( fabric.particles.last, Vec3(1,1,0))
  val fabric2 = new SpringMesh( Plane.generateMesh(.4f,.2f,20,10), 1.f) //Sphere()
  fabric2.pins += AbsoluteConstraint( fabric2.particles.takeRight(20).head, Vec3(-1,1,0))
  fabric2.pins += AbsoluteConstraint( fabric2.particles.takeRight(10).head, Vec3(-1,1,0))
  fabric2.pins += AbsoluteConstraint( fabric2.particles.last, Vec3(1,1,0))

  val fabric3 = new SpringMesh( Plane.generateMesh(.2f,.4f,10,20), 1.f) //Sphere()
  fabric3.pins += AbsoluteConstraint( fabric3.particles.head, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.take(10).last, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.takeRight(10).head, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.last, Vec3(1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.take(100).last, Vec3(-1,1,0))
  fabric3.pins += AbsoluteConstraint( fabric3.particles.take(110).last, Vec3(1,1,0))
  // s.particles.takeRight(10).foreach( (p) => s.pins += AbsoluteConstraint(p, p.position))
  val model = Model(fabric)
  val model2 = Model(fabric2)
  val model3 = Model(fabric3)
  var drawFabric = false
  def drawFabric(b:Boolean){drawFabric = b}
  // fabric.mesh.primitive = Lines
  model.color.set(1,0,0,1)
  model2.color.set(1,0,0,1)
  model3.color.set(1,0,0,1)
  node.camera = Camera
  node.scene.push(fabric)
  // Scene.push(fabric)
  // SceneGraph.addNode(node)


  val live = new Ruby("skeleton.rb")

  SimpleAppRun()  


  override def init(){
    val compNode = new RenderNode
    compNode.shader = "composite"
    compNode.clear = false
    val quag = new Drawable {
      val m = Mesh(Primitive2D.quad)
      override def draw(){
        // Shader("composite").setUniformf("u_blend0", 0.25f)
        // Shader("composite").setUniformf("u_blend1", 0f.75f)
        // Shader("composite").setUniformMatrix("u_projectionViewMatrix", new Matrix4())
        m.draw()
      }
    }
    compNode.scene.push( quag )
    SceneGraph.root.outputTo(compNode)
    compNode.outputTo(compNode)
    compNode.outputTo(ScreenNode)

    live.init()
  }

  override def draw(){

    live.draw()
    ground.draw()
    
    skeletons.foreach( _.draw() )
    
    if( drawFabric ){
      model.draw()
      model2.draw()
      // model3.draw()
    }
  }

  override def animate(dt:Float){
    live.animate(dt)
    skeletons.foreach( _.animate(dt) )
    fabric.animate(dt)
    fabric2.animate(dt)
    // fabric3.animate(dt)
  }

}


class Skeleton(val id:Int) extends Animatable {

  var color = RGBA(0.f,.7f,.1f,.5f)

  var calibrating = false
  val loadingModel = Cube().scale(0.1f).translate(0,0.5f,0)
  val m = Cube().rotate(45.f.toRadians,0,45.f.toRadians)
  loadingModel.addPrimitive(m)
  m.color = color
  loadingModel.color = color

  var tracking = false
  val joints = Map[String,Model]()

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

  joints.values.foreach( _.color = color )

  val bones = new ListBuffer[Model]()
  for( i <- (0 until 8)) bones += Cylinder()
  bones.foreach( (b) => { b.color = color; b.scale.set(.015f,.015f,.15f) })


  override def draw(){
    if(calibrating) loadingModel.draw()
    if(tracking){ 
      joints.values.foreach(_.draw())
      // strings.foreach(_.draw())
      bones.foreach(_.draw())
    }
  }

  override def animate(dt:Float){
    loadingModel.rotate(0,0.10f,0)
    if(tracking){
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

      // bones(1).pins(0).set(joints("l_elbow").pose.pos)
      // bones(1).pins(1).set(joints("l_shoulder").pose.pos)
      // bones(2).pins(0).set(joints("r_hand").pose.pos)
      // bones(2).pins(1).set(joints("r_elbow").pose.pos)
      // bones(3).pins(0).set(joints("r_elbow").pose.pos)
      // bones(3).pins(1).set(joints("r_shoulder").pose.pos)
      // bones(4).pins(0).set(joints("l_foot").pose.pos)
      // bones(4).pins(1).set(joints("l_knee").pose.pos)
      // bones(5).pins(0).set(joints("l_knee").pose.pos)
      // bones(5).pins(1).set(joints("l_hip").pose.pos)
      // bones(6).pins(0).set(joints("r_foot").pose.pos)
      // bones(6).pins(1).set(joints("r_knee").pose.pos)
      // bones(7).pins(0).set(joints("r_knee").pose.pos)
      // bones(7).pins(1).set(joints("r_hip").pose.pos)
      // strings.foreach(_.animate(dt))
    }
  }

  def setColor(c:RGBA){
    color = c
    loadingModel.color = c
    m.color = c
    joints.values.foreach( _.color = color)
    bones.foreach( _.color = color)
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
    mesh.render( Shader(), GL10.GL_LINES)    
  }

  def applyForce( f: Vec3 ) = particles.foreach( _.applyForce(f) )

}


