
import com.fishuyo.seer._

import allosphere._
import allosphere.actor._

import graphics._
import dynamic._
import spatial._
import io._
import particle._

import collection.mutable.ArrayBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

import ClusterSystem.{ system, system10g }
// import ClusterSystem.{ test2_10g => system10g }

import allosphere.livecluster.Node

Scene.alpha = .3f
SceneGraph.root.depth = false

object RendererScript extends SeerScript {
  

  Node.mode = "omni"

  val c = Cube()
  val nc = 3
  val cubes = for(z <- -nc to nc; y <- -nc to nc; x <- -nc to nc) yield {
    val c = Cube().translate(Vec3(x,y,z)*3f).scale(0.01)
    c.material = Material.specular
    c.material.color = RGB(1,0,1)
    c
  }

  val n = 40
  val mesh = Plane.generateMesh(10,10,n,n,Quat.up)
  mesh.primitive = Lines
  val model = Model(mesh)
  model.material = Material.specular
  model.material.color = RGB(0,0.5,0.7)
  // mesh.vertices.foreach{ case v => v.set(v.x,v.y+Random.float(-1,1).apply()*0.05*(v.x).abs,v.z) }
  mesh.vertices.foreach{ case v => v.set(v.x,v.y+math.sin(v.x*v.z)*0.1,v.z) }
  val fabricVertices0 = mesh.vertices.clone

  val fabric = new SpringMesh(mesh,1f)
  fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles(n), fabric.particles(n).position)
  // fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles.last, fabric.particles.last.position)
  Gravity.set(0,0,0)
  mesh.primitive = Triangles

  var vertices = Array[Float]()

  val cursor = Sphere().scale(0.05)

  var t = 0f
  var scale = 1f

  val stateListener = system10g.actorOf(Props( new StateListener()), name = "statelistener")

  override def preUnload(){
    stateListener ! PoisonPill
  }

  var inited = false
  var rs:Seq[Int] = _

  override def init(){
    Node.omniShader = Shader.load("omni", OmniShader.glsl + S.basic._1, S.basic._2 )

    Node.omni.mStereo = 1
    Node.omni.mMode = StereoMode.ACTIVE
    Node.lens.eyeSep = 0.05
    // Node.omni.renderFace(0) = true
    // Node.omni.renderFace(1) = true
    // Node.omni.renderFace(2) = true
    // Node.omni.renderFace(3) = true
    // Node.omni.renderFace(4) = true
    // Node.omni.renderFace(5) = true

    inited = true

    rs = for( i <- 0 until 5) yield util.Random.int(0,5)()
  }

  override def draw(){

    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )

    // val s = Cube()
    // s.scale(30f,30f,30f)
    // s.material = Material.basic
    // s.material.color = RGB(0/255f,191f/255,255/255f)
    // s.translate(Camera.nav.pos)
    // s.draw()


    // val ps = for(i <- 0 until 50) yield {
    //   val p = Plane()
    //   p.rotate(Pi/2,0,0)
    //   p.scale(1,10,10)
    //   p.material = Material.basic
    //   if( i % 2 == 0) p.material.color = RGB(0,1,0)
    //   else p.material.color = RGB(1,0,0)
    //   p.translate(i*2,0,0)
    //   p
    // }
    // ps.foreach( _.draw )

    // val cs = for(i <- 0 until 5) yield {
    //   val p = Cube()
    //   var l = List(p)
    //   p.material = Material.specular
    //   p.material.color = RGB(1,0,1)
    //   p.translate(i*20f,0.75f,5*math.sin(t))
    //   p.scale(0.5,0.5,0.5)
      
    //   val r = rs(i)
    //   for(j <- 0 until r){
    //     val c = Cube()
    //     c.material = Material.specular
    //     c.material.color = RGB(1,0,1)

    //     c.translate(i*20f, 0.75 + r * 0.6, 5*math.sin(r*0.8*t))
    //     c.scale(0.5,0.5,0.5)
    //     l = c :: l
    //   }
    //   l
    // }
    // cs.flatten.foreach( _.draw )

    // val p = Plane()
    // p.material = Material.basic 
    // p.material.color = RGB(0,0.6,0.2)
    // p.translate(0,0,-1)
    // p.rotate(Pi/2,0,0)
    // p.scale(100,100,1)
    // p.draw

    // val p = Plane().draw
    // p.rotate(Pi/2,0,0).scale(10f)
    // p.draw()
    model.draw
    // cubes.foreach(_.draw)
  }

  override def animate(dt:Float){
    if(!inited) init()

    Node.lens.eyeSep = 0.1 //math.sin(t)

    if(vertices.length > 0){
    //   mesh.clear
    //   mesh.vertices ++= vertices
    //   mesh.recalculateNormals
    //   mesh.update
      mesh.gdxMesh.get.setVertices(vertices)
    }


    cubes.foreach((c) => {
      c.scale.set(math.sin(t)*0.5)
      c.rotate(0.01,0.02,0)
    })

  }
}


class StateListener extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }

  val mediator = DistributedPubSubExtension(system10g).mediator
  mediator ! Subscribe("state", self)
 
  def receive = {
    case SubscribeAck(Subscribe("state", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case f:Float =>
      RendererScript.t = f
    case a:Array[Float] if a.length == 7 =>
      Camera.nav.pos.set(a(0),a(1),a(2))
      Camera.nav.quat.set(a(3),a(4),a(5),a(6))
    case a:Array[Float] =>
      RendererScript.vertices = a //a.grouped(3).map((g)=>Vec3(g(0),g(1),g(2))).toArray
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
        v_color = u_color;

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
        // colorMixed = vec4(1,0,1,1);

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

        // gl_FragColor = vec4(1,0,0,1); //(1.0 - u_fade);

      }
    """
  )
 }



RendererScript

