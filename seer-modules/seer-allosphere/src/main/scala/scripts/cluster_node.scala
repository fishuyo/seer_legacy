import com.fishuyo.seer._
import allosphere._
import graphics._
import dynamic._
import maths._
import spatial._
import io._
import allosphere.livecluster.Node

import collection.mutable.ArrayBuffer

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

object ClusterScript extends SeerScript{
	
	var sim = true

  Node.mode = "omni"
  val c = Cube()
  val n = 3
  val cubes = for(z <- -n to n; y <- -n to n; x <- -n to n) yield Cube().translate(Vec3(x,y,z)*3.f)

  var t = 0.f
  var scale = 1.f
	val actor = Node.systemm.actorOf(Props( new Listener()), name = "node_script5")

	var publisher:ActorRef = _
	var subscriber:ActorRef = _
	Hostname() match {
		case "Thunder.local" => publisher = Node.systemm.actorOf(Props( new Simulator()), name = "simulator5")
		case _ => sim = false; subscriber = Node.systemm.actorOf(Props( new Renderer), name = "renderer5")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")

	override def preUnload(){
		actor ! Kill
		if(publisher != null) publisher ! Kill
		if(subscriber != null) subscriber ! Kill
	}

	var inited = false
	override def init(){
    Node.omniShader = Shader.load("omni", OmniShader.glsl + S.basic._1, S.basic._2 )
		inited = true
	}
  override def draw(){
  	Script.draw()
    // cubes.foreach(_.draw)
  }
  override def animate(dt:Float){
  	if(!inited) init()

  	if(sim){
  		Script.animate(dt)
  		t += dt
  		scale = math.sin(t)
  		publisher ! Script.mesh
  	}
  	cubes.foreach(_.scale.set(scale))

  }
}


import util._
import particle._
import trees._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import scala.collection.mutable.ListBuffer

import concurrent.duration._


Scene.alpha = .3
SceneGraph.root.depth = false

// Camera.nav.pos.set(0,1,4)
// Camera.nav.quat.set(1,0,0,0)


object Script extends SeerScript {

	implicit def f2i(f:Float) = f.toInt

  var dirty = true
  var update = false

  val mesh = Plane.generateMesh(10,10,50,50,Quat.up)
  mesh.primitive = Lines
  val model = Model(mesh)
  model.material = Material.specular
  model.material.color = RGB(0,0.5,0.7)

  val fabric = new SpringMesh(mesh,1.f)
  fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles(50), fabric.particles(50).position)
  // fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles.last, fabric.particles.last.position)
  Gravity.set(0,0,0)

  val sun = Sphere().scale(0.1f)

  var blend = GL20.GL_ONE_MINUS_SRC_ALPHA

  Schedule.clear
  val cycle = Schedule.cycle(200 seconds){ case t =>
  	val y = 10.f*math.cos(2*Pi*t)
  	val z = 10.f*math.sin(2*Pi*t)
  	Shader.lightPosition.set(Shader.lightPosition.x,y,z)
  	sun.pose.pos.set(Shader.lightPosition)
  }
  val cycle2 = Schedule.cycle(1 hour){ case t =>
  	val x = 2.f*math.cos(2*Pi*t)
  	Shader.lightPosition.x = x
  	sun.pose.pos.set(Shader.lightPosition)
  }

	val cursor = Sphere().scale(0.05)
	var lpos = Vec2()
	var vel = Vec2()

	// val part = fabric.particles(fabric.particles.length/2+25)
	// val tree = new Tree()
	// tree.setAnimate(true)
	// tree.setReseed(true)
	// tree.setDepth(8)
	// tree.branch(8)

	override def onLoad(){
	}

	override def draw(){
		FPS.print
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, blend)

		model.draw
		sun.draw
		cursor.draw
		// tree.draw
	}

  override def animate(dt:Float){
  	val x = Mouse.xy().x
  	// cycle.speed = x*100
  	// cycle2.speed = x*100

  	if( Mouse.status() == "drag"){
			vel = (Mouse.xy() - lpos)/dt
			// println(vel)
			// s.applyForce( Vec3(vel.x,vel.y,0)*10.f)
			val r = Camera.ray(Mouse.x()*Window.width, (1.f-Mouse.y()) * Window.height)
			fabric.particles.foreach( (p) => {
				val t = r.intersectSphere(p.position, 0.25f)
				if(t.isDefined){
					// val p = r(t.get)
					p.applyForce(Vec3(vel.x,vel.y,0)*150.f)
					cursor.pose.pos.set(r(t.get))
				}
			})
		}
		lpos = Mouse.xy()


		fabric.animate(1.f*2.f*dt)
		// tree.root.pose.pos.set(part.position)
		// tree.animate(dt)
  }


  // input events
  Keyboard.clear()
  Keyboard.use()
  Keyboard.bind("g", ()=>{
  		// Script.mesh.clear()
			// Plane.generateMesh(Script.mesh,100,100,100,100,Quat.up())
			Script.mesh.vertices.foreach{ case v => v.set(v.x,v.y+Random.float(-1,1).apply()*0.02*(v.x).abs,v.z) }
			Script.mesh.recalculateNormals()
			Script.mesh.update()
  })
  Keyboard.bind("1", ()=>{Script.mesh.primitive = Triangles})
  Keyboard.bind("2", ()=>{Script.mesh.primitive = Lines})
  Keyboard.bind("3", ()=>{Script.blend = GL20.GL_ONE_MINUS_SRC_ALPHA})
  Keyboard.bind("4", ()=>{Script.blend = GL20.GL_ONE})

  Mouse.clear
  Mouse.use

}



class Renderer extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("state", self)
 
  def receive = {
    case SubscribeAck(Subscribe("state", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case f:Float =>
      ClusterScript.scale = f
    case a:Array[Float] =>
    	println("receive mesh")
    	Script.mesh.gdxMesh.get.setVertices(a)
  }
}

class Simulator extends Actor {
  import DistributedPubSubMediator.Publish
  // activate the extension
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
 
  def receive = {
    case f:Float =>
      mediator ! Publish("state", f)
    case m:Mesh => 
    	val numV = m.gdxMesh.get.getNumVertices
			val sizeV = m.gdxMesh.get.getVertexSize / 4 // number of floats per vertex
			val verts = new Array[Float](numV*sizeV)
			m.gdxMesh.get.getVertices(verts)
			println("publish mesh")
    	mediator ! Publish("state", verts)
  }
}

class Listener extends Actor {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("io", self)
 
  def receive = {
    case SubscribeAck(Subscribe("io", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case f:Float =>
      // ClusterScript.scale = f
    case pos:Array[Float] => Camera.nav.pos.set(pos(0),pos(1),pos(2))
  }
}

object S {
 val basic = (
    // Vertex Shader
    """
      attribute vec3 a_position;
      attribute vec3 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform int u_hasColor;
      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_viewMatrix;
      uniform mat4 u_modelMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec4 u_cameraPosition;

      uniform vec3 u_lightPosition;

      varying vec4 v_color;
      varying vec3 v_normal, v_pos, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
      varying float v_fog;

      void main(){
        // if( u_hasColor == 0){
        if( a_color.xyz == vec3(0,0,0)){
          v_color = u_color;
        } else {
          v_color = a_color;
        }

        vec4 pos = u_modelViewMatrix * vec4(a_position,1);
        v_pos = vec3(pos) / pos.w;

        v_normal = vec3(u_normalMatrix * vec4(a_normal,0));
        
        v_eyeVec = normalize(-pos.xyz);

        v_lightDir = vec3(u_viewMatrix * vec4(u_lightPosition,0));

        v_texCoord = a_texCoord0;
        gl_Position = omni_render(u_modelViewMatrix * vec4(a_position,1));
        // gl_Position = u_projectionViewMatrix * vec4(a_position,1); 
      }
    """,
    // Fragment Shader
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;

      uniform float u_alpha;
      uniform float u_fade;
      uniform float u_textureMix;
      uniform float u_lightingMix;
      uniform vec4 u_lightAmbient;
      uniform vec4 u_lightDiffuse;
      uniform vec4 u_lightSpecular;
      uniform float u_shininess;

      varying vec2 v_texCoord;
      varying vec3 v_normal;
      varying vec3 v_eyeVec;
      varying vec3 v_lightDir;
      varying vec4 v_color;
      varying vec3 v_pos;

      void main() {
        
        vec4 colorMixed;
        if( u_textureMix > 0.0){
          vec4 textureColor = texture2D(u_texture0, v_texCoord);
          colorMixed = mix(v_color, textureColor, u_textureMix);
        }else{
          colorMixed = v_color;
        }

        vec4 final_color = colorMixed * u_lightAmbient;

        vec3 N = normalize(v_normal);
        vec3 L = normalize(v_lightDir);

        float lambertTerm = dot(N,L);
        final_color += u_lightDiffuse * colorMixed * max(lambertTerm,0.0);

        float specularTerm = 0.0;

        //phong
        vec3 R = reflect(-L, N);
        vec3 E = normalize(v_eyeVec); //normalize(-v_pos);
        //float specAngle = max(dot(R,E), 0.0);
        //specularTerm = pow(specAngle, 8.0);

        //blinn
        float halfDotView = max(0.0, dot(N, normalize(L + E)));
        specularTerm = pow(halfDotView, 20.0);
        specularTerm = specularTerm * smoothstep(0.0,0.2,lambertTerm);

        final_color += u_lightSpecular * specularTerm;
        gl_FragColor = mix(colorMixed, final_color, u_lightingMix);
        gl_FragColor *= (1.0 - u_fade);
        gl_FragColor.a *= u_alpha;

        gl_FragColor = vec4(1,1,1,1); //(1.0 - u_fade);

      }
    """
  )
 }



ClusterScript