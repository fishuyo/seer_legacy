
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._
import particle._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import scala.util.matching.Regex

import de.sciss.osc._

object ScriptV extends SeerScript {
  
  Camera.nav.quat.set(Quat.forward)
  Camera.nav.pos.set(0,.4,4)

  Keyboard.clear
  Keyboard.use
  Keyboard.bind("p", () =>{
    println(Camera.nav.pos)
  })
  Keyboard.bind("g", () =>{
    SceneGraph.root.outputs.clear
    ScreenNode.inputs.clear
    SceneGraph.root.outputTo(ScreenNode)
  })
  Keyboard.bind("f", () =>{
    Run(()=>{
      SceneGraph.root.outputs.clear
      ScreenNode.inputs.clear

      val feedback = new RenderNode
      feedback.shader = "composite"
      feedback.clear = false
      feedback.scene.push(Plane())
      SceneGraph.root.outputTo(feedback)
      feedback.outputTo(feedback)
      feedback.outputTo(ScreenNode)
    })
  })
  // override def onLoad(){
    // Run(()=>{
    //   val feedback = new RenderNode
    //   feedback.shader = "composite"
    //   feedback.clear = false
    //   feedback.scene.push(Plane())
    //   SceneGraph.root.outputTo(feedback)
    //   feedback.outputTo(feedback)
    //   feedback.outputTo(ScreenNode)
    // })
  // }

	val skeletons = ArrayBuffer[Skeleton]()
  for( i <- 0 until 4) skeletons += new QuadMan(i)
	// for( i <- 0 until 4) skeletons += new StickMan(i)

  val musicians = ArrayBuffer[Thing]()
  for( i <- 0 until 6){
    val t = new Thing
    musicians += t
    val theta = i/(6.f-1) * Pi
    val r = 3.3f
    t.model.pose.pos.x = r*math.cos(theta)
    t.model.pose.pos.y = r*math.sin(theta)
    t.model.pose.pos.z = -1.f
    // val c = Random.vec3()
    // t.color = RGB(c.x,c.y,c.z)
    t.color = RGB(1,0,0)
  }


  //uniforms
  var t = 0.f
  var alpha = 0.25f
  var beta = 0.985f
  var maxz = 0.f

	override def draw(){
    // Sphere().draw()
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    musicians.zipWithIndex.foreach{ case (m,i) =>
      S.shaders("s1").uniforms("time") = 1.f
      S.shaders("s1").uniforms("color") = m.color
      S.shaders("s1").uniforms("rms") = m.rms
      S.shaders("s2").uniforms("color") = m.color
      S.shaders("s2").uniforms("rms") = m.rms 
      m.draw
    }

		skeletons.foreach( (s) => {
      S.shaders("s1").uniforms("time") = t
      S.shaders("s1").uniforms("color") = s.color
      S.shaders("s1").uniforms("rms") = 1.f
      s.draw
    })
	}

	override def animate(dt:Float){
    val z = skeletons(0).joints("torso").z
    if( z > maxz){ maxz = z; println(maxz)}
    beta = map(z,0.f,2.7f, 0.85, 0.9999f)

    Shader("composite")
    val fb = Shader.shader.get
    fb.uniforms("u_blend0") = alpha
    fb.uniforms("u_blend1") = beta

    t += dt
    skeletons.foreach(_.animate(dt))
		musicians.foreach(_.animate(dt))
	}

	override def preUnload(){
		recv.clear()
		recv.disconnect()
    recv2.clear()
    recv2.disconnect()
	}

  val recv = new OSCRecv
  recv.listen(8008)
  val regex = "/(.*)/joint/(.*)".r
  recv.bindp {
    case Message(regex(idS,name),x:Float,y:Float,z:Float) =>
      val id = idS.toInt - 1
      skeletons(id).joints(name).lerpTo(Vec3(x,y,z), 0.5f)
      skeletons(id).tracking = true
    case Message("/calibrating", id:Int) => 
      skeletons(id-1).calibrating = true
    case Message("/tracking", id:Int) => 
      skeletons(id-1).calibrating = false
      skeletons(id-1).tracking = true
    case Message("/lost", id:Int) => 
      skeletons(id-1).calibrating = false
      skeletons(id-1).tracking = false
    case _ => ()
  }

  val recv2 = new OSCRecv
	recv2.listen(9000)
  recv2.bindp {
    case Message("/rms", id:Int, rms:Float) =>
      musicians(id).rms = rms
    case Message("/connect", id:Int, joint:String, r:Float, g:Float, b:Float) =>
      musicians(id).joint = joint
      musicians(id).color = RGB(r,g,b)
      println("/connect " + id)
    case Message("/fade", a:Float, b:Float) =>
      alpha = a; beta = b;
    case Message("/fade", a:Float) =>
      alpha = a;
    case _ => ()
  }


  Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})
  Run(()=>{ S.shaders("s2") = Shader.load("s2",S.vert,S.frag2)})


}


Scene.alpha = 0.9
SceneGraph.root.depth = false



object S {
  val shaders = Map[String,Shader]()
  val vert = """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;

    varying vec4 v_color;
    varying vec2 v_texCoord;

    void main() {
      gl_Position = u_projectionViewMatrix * a_position;
      v_texCoord = a_texCoord0;
      v_color = a_color;
    }
  """
  val frag = """
    #ifdef GL_ES
        precision mediump float;
    #endif

    varying vec4 v_color;
    varying vec2 v_texCoord;

    uniform float time;
    uniform float rms;
    uniform vec3 color;
  """
  val frag1 = frag + """
    void main(){
        vec2 uv = 2. * v_texCoord - 1.;
        float d = pow(uv.x,2.0) + pow(uv.y,2.0);

        float t = 0.5*(sin(time)+1.0);
        float b = clamp(t - d + 0.3, 0.0,1.0);
        vec3 c = mix( b*color, b*color+b*vec3(0,0.25,0), 1.0-b);
        gl_FragColor = rms*vec4(c,b);
    }
  """
  val frag2 = frag + """
    void main(){
        vec2 uv = 2. * v_texCoord - 1.;
        float d = pow(uv.x,2.0); // + pow(uv.y,2.0);

        float t = rms; //0.5*(sin(time)+1.0);
        float b = clamp(t - d + 0.1, 0.0,1.0);
        vec3 c = mix( b*color, b*color+b*vec3(0,0.25,0), 1.0-b);
        gl_FragColor = vec4(c,b);
    }
  """
}

class Thing extends Animatable {

  var rms = 0.0f
  var joint = "r_hand"
  var color = RGB(1,1,1)
  val model = Sphere().scale(0.25f)
  val model2 = Sphere().scale(0.05f)
  model.shader = "s1"
  model2.shader = "s1"
  var rot = Random.vec3()*0.007f

  val fabric = new SpringMesh( Plane.generateMesh(.2f,3.0f+Random.float(),10,30), 1.f)
  fabric.pins += AbsoluteConstraint( fabric.particles.take(10).head, model.pose.pos)
  fabric.pins += AbsoluteConstraint( fabric.particles.take(10).last, model.pose.pos)
  fabric.pins += AbsoluteConstraint( fabric.particles.takeRight(10).head, Vec3(1))
  fabric.pins += AbsoluteConstraint( fabric.particles.last, Vec3(1))
  // fabric.particles.take(10).foreach( (p) => {fabric.pins += AbsoluteConstraint(p, model.pose.pos)})

  val fModel = Model(fabric)
  fModel.shader = "s2"

  override def draw(){ model.draw; fModel.draw; model2.draw }
  override def animate(dt:Float){
    model.rotate(rot.x,rot.y,rot.z)
    model2.rotate(rot.x,rot.y,rot.z)
    val p = ScriptV.skeletons(0).joints(joint)
    fabric.pins(2).q.position.set(p)
    fabric.pins(3).q.position.set(p)
    model2.pose.pos.set(p)
    fabric.animate(dt)
  }
}


object Bone {
	def apply() = new Bone(Vec3(),Quat(),0.f)
	def apply(p:Vec3,q:Quat,l:Float) = new Bone(p,q,l)
}
class Bone( var pos:Vec3, var quat:Quat, var length:Float)


class Skeleton(val id:Int) extends Animatable {

  val color = RGB(1,0,0)
  var calibrating = false
  var tracking = false

  var joints = Map[String,Vec3]()

  joints += "head" -> Vec3(0)
  joints += "neck" -> Vec3(0)
  joints += "torso" -> Vec3(0)
  joints += "r_shoulder" -> Vec3(0)
  joints += "r_elbow" -> Vec3(0)
  joints += "r_hand" -> Vec3(0)
  joints += "l_shoulder" -> Vec3(0)
  joints += "l_elbow" -> Vec3(0)
  joints += "l_hand" -> Vec3(0)
  joints += "r_hip" -> Vec3(0)
  joints += "r_knee" -> Vec3(0)
  joints += "r_foot" -> Vec3(0)
  joints += "l_hip" -> Vec3(0)
  joints += "l_knee" -> Vec3(0)
  joints += "l_foot" -> Vec3(0)

  val bones = ListBuffer[Bone]()
  for( i <- (0 until 8)) bones += Bone()

  def setJoints(s:Skeleton){
    joints = s.joints.clone
  }

  def updateBones(){
    bones(0).pos.set(joints("l_shoulder"))
    var a = joints("l_elbow") - joints("l_shoulder")
    bones(0).length = a.mag()
    bones(0).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(1).pos.set(joints("l_elbow"))
    a = joints("l_hand") - joints("l_elbow")
    bones(1).length = a.mag()
    bones(1).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(2).pos.set(joints("r_shoulder"))
    a = joints("r_elbow") - joints("r_shoulder")
    bones(2).length = a.mag()
    bones(2).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(3).pos.set(joints("r_elbow"))
    a = joints("r_hand") - joints("r_elbow")
    bones(3).length = a.mag()
    bones(3).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(4).pos.set(joints("l_hip"))
    a = joints("l_knee") - joints("l_hip")
    bones(4).length = a.mag()
    bones(4).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(5).pos.set(joints("l_knee"))
    a = joints("l_foot") - joints("l_knee")
    bones(5).length = a.mag()
    bones(5).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(6).pos.set(joints("r_hip"))
    a = joints("r_knee") - joints("r_hip")
    bones(6).length = a.mag()
    bones(6).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

    bones(7).pos.set(joints("r_knee"))
    a = joints("r_foot") - joints("r_knee")
    bones(7).length = a.mag()
    bones(7).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)
	}

}

class StickMan(override val id:Int) extends Skeleton(id) {

  val loadingModel = Cube().scale(0.1f).translate(0,0.5f,0)
  val m = Cube().rotate(45.f.toRadians,0,45.f.toRadians)
  loadingModel.addPrimitive(m)
  m.material.color = color
  loadingModel.material.color = color

  var jointModels = Map[String,Model]()

  jointModels += "head" -> Sphere().scale(.05f,.065f,.05f)
  jointModels += "neck" -> Sphere().scale(.02f)
  jointModels += "torso" -> Sphere().scale(.07f,.10f,.05f)
  jointModels += "r_shoulder" -> Sphere().scale(.02f)
  jointModels += "r_elbow" -> Sphere().scale(.02f)
  jointModels += "r_hand" -> Sphere().scale(.02f)
  jointModels += "l_shoulder" -> Sphere().scale(.02f)
  jointModels += "l_elbow" -> Sphere().scale(.02f)
  jointModels += "l_hand" -> Sphere().scale(.02f)
  jointModels += "r_hip" -> Sphere().scale(.03f)
  jointModels += "r_knee" -> Sphere().scale(.02f)
  jointModels += "r_foot" -> Sphere().scale(.02f)
  jointModels += "l_hip" -> Sphere().scale(.03f)
  jointModels += "l_knee" -> Sphere().scale(.02f)
  jointModels += "l_foot" -> Sphere().scale(.02f)
  
  jointModels.values.foreach( (m) => {
  	m.material = Material.basic
  	m.material.color = color 
  })

  val boneModels = new ListBuffer[Model]()
  for( i <- (0 until 8)) boneModels += Cylinder()
  boneModels.foreach( (b) => {
  	b.material = Material.basic
  	b.material.color = color
  	b.scale.set(.015f,.015f,.15f) 
  })

  def setShader(s:String){
    jointModels.values.foreach(_.shader = s)
    boneModels.foreach(_.shader = s)
  }

  override def draw(){
    if(calibrating) loadingModel.draw()
    if(tracking){ 
      jointModels.values.foreach(_.draw())
      boneModels.foreach(_.draw())
    }
  }

  override def animate(dt:Float){
    loadingModel.rotate(0,0.10f,0)
    updateBones()

    jointModels.foreach{ case(name,m) => 
    	m.pose.pos.set( joints(name) )
    }
    boneModels.zip(bones).foreach{ case (m,b) =>
    	m.pose.pos.set( b.pos )
    	m.pose.quat.set( b.quat )
    	m.scale.z = b.length
    }
  }

  def setColor(c:RGBA){
    color.set(c)
    loadingModel.material.color = c
    m.material.color = c
    // joints.values.foreach( _.material.color = color)
    boneModels.foreach( _.material.color = color)
  }
}

class QuadMan(override val id:Int) extends Skeleton(id) {

  var jointModels = Map[String,Model]()
  jointModels += "head" -> Plane().scale(.05f,.065f,.05f)
  jointModels += "neck" -> Plane().scale(.02f)
  jointModels += "torso" -> Plane().scale(.07f,.10f,.05f)
  jointModels += "r_shoulder" -> Plane().scale(.02f)
  jointModels += "r_elbow" -> Plane().scale(.02f)
  jointModels += "r_hand" -> Plane().scale(.02f)
  jointModels += "l_shoulder" -> Plane().scale(.02f)
  jointModels += "l_elbow" -> Plane().scale(.02f)
  jointModels += "l_hand" -> Plane().scale(.02f)
  jointModels += "r_hip" -> Plane().scale(.03f)
  jointModels += "r_knee" -> Plane().scale(.02f)
  jointModels += "r_foot" -> Plane().scale(.02f)
  jointModels += "l_hip" -> Plane().scale(.03f)
  jointModels += "l_knee" -> Plane().scale(.02f)
  jointModels += "l_foot" -> Plane().scale(.02f)
  
  jointModels.values.foreach( (m) => {
  	m.material = Material.basic
  	m.material.color = color
  	m.shader = "s1"
  })

  val boneModels = new ListBuffer[Model]()
  for( i <- (0 until 8)) boneModels += Cylinder() //Plane()
  boneModels.foreach( (b) => {
  	b.material = Material.basic
  	b.material.color = color
    b.shader = "s1"
    b.scale.set(.015f,.015f,.15f) 
  	// b.scale.set(.5f) 
  })

  def setShader(s:String){
    jointModels.values.foreach(_.shader = s)
    boneModels.foreach(_.shader = s)
  }

  override def draw(){
    if(tracking){ 
      jointModels.values.foreach(_.draw())
      boneModels.foreach(_.draw())
    }
  }

  override def animate(dt:Float){
    updateBones()

    jointModels.foreach{ case(name,m) => 
    	m.pose.pos.set( joints(name) )
    }    
    boneModels.zip(bones).foreach{ case (m,b) =>
    	m.pose.pos.set( b.pos )
    	m.pose.quat.set( b.quat )
    	m.scale.z = b.length
    }
  }

  def setColor(c:RGBA){
    color.set(c)
    // joints.values.foreach( _.material.color = color)
    boneModels.foreach( _.material.color = color)
  }
}

ScriptV
