
import com.fishuyo.seer._

import allosphere._
import allosphere.actor._

import graphics._
import dynamic._
import spatial._
import io._

import collection.mutable.ArrayBuffer

import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator


import allosphere.livecluster.Node


object RendererScript extends SeerScript {
	

  Node.mode = "omni"

  val c = Cube()
  val n = 3
  val cubes = for(z <- -n to n; y <- -n to n; x <- -n to n) yield Cube().translate(Vec3(x,y,z)*3.f)

  var t = 0.f
  var scale = 1.f

	val stateListener = system10g.actorOf(Props( new StateListener()), name = "statelistener")

	override def preUnload(){
		stateListener ! PoisonPill
	}

	var inited = false
	override def init(){
    Node.omniShader = Shader.load("omni", OmniShader.glsl + S.basic._1, S.basic._2 )
		inited = true
	}

  override def draw(){
    cubes.foreach(_.draw)
  }

  override def animate(dt:Float){
  	if(!inited) init()

  	cubes.foreach(_.scale.set(scale))
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
      Renderer.scale = f
    case a:Array[Float] =>
      Camera.nav.pos.set(a(0),a(1),a(2))
      Camera.nav.quat.set(a(3),a(4),a(5),a(6))
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



RendererScript

