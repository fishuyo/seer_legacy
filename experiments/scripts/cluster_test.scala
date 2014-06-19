
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._


import akka.actor.Props

import allosphere._
import allosphere.actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

import de.sciss.osc.Message


object Script extends SeerScript {

	val sim = false
	if(sim) OSC.connect("192.168.0.255", 8008)

	val actor = system.actorOf(Props(new Node), name = "node")

	override def preUnload(){
		recv.clear()
		recv.disconnect()
	}

	override def draw(){
		Omni.draw
	}

	override def animate(dt:Float){
		if(sim){
			val pos = Camera.nav.pos
			OSC.send("/camera/pos", pos.x, pos.y, pos.z)
		}	
	}

  val recv = new OSCRecv
  // recv.listen(8008)
  recv.bindp {
    case Message("/camera/pos", x:Float, y:Float, z:Float) => 
    	Camera.nav.pos.set(x,y,z)
   	case Message("/camera/quat", w:Float, x:Float, y:Float, z:Float) => 
    	Camera.nav.quat.set(w,x,y,z)
    case _ => ()
  }
}

Camera.nav.pos.set(0,0,0)
Camera.nav.quat.set(1,0,0,0)
Scene.alpha = .5
SceneGraph.root.depth = false
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})
Run(()=>{ S.shaders("r") = Shader.load("r",S.vert,S.r)})
Run(()=>{ S.shaders("g") = Shader.load("g",S.vert,S.g)})
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE) })
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA) })


// shader code
object S {
  val shaders = Map[String,Shader]()
  val vert = """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;

    varying vec4 v_color;
    varying vec2 v_texCoord;
    varying vec3 v_pos;

    void main() {
      gl_Position = u_projectionViewMatrix * a_position;
      v_texCoord = a_texCoord0;
      v_color = a_color;
      v_pos = a_position.xyz;
    }
  """
  val vOmni = """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;
    uniform mat4 u_modelViewMatrix;

    varying vec4 v_color;
    varying vec2 v_texCoord;
    varying vec3 v_pos;

    void main() {
      gl_Position = omni_render(u_modelViewMatrix * a_position);
      v_texCoord = a_texCoord0;
      v_color = a_color;
      v_pos = a_position.xyz;
    }
  """
  val frag = """
    #ifdef GL_ES
        precision mediump float;
    #endif

    varying vec4 v_color;
    varying vec3 v_pos;
    varying vec2 v_texCoord;
  """
  val frag1 = frag + """

	  vec4 hsv_to_rgb(float h, float s, float v, float a) {
	        float c = v * s;
	        h = mod((h * 6.0), 6.0);
	        float x = c * (1.0 - abs(mod(h, 2.0) - 1.0));
	        vec4 color;

	        if (0.0 <= h && h < 1.0) {
	                color = vec4(c, x, 0.0, a);
	        } else if (1.0 <= h && h < 2.0) {
	                color = vec4(x, c, 0.0, a);
	        } else if (2.0 <= h && h < 3.0) {
	                color = vec4(0.0, c, x, a);
	        } else if (3.0 <= h && h < 4.0) {
	                color = vec4(0.0, x, c, a);
	        } else if (4.0 <= h && h < 5.0) {
	                color = vec4(x, 0.0, c, a);
	        } else if (5.0 <= h && h < 6.0) {
	                color = vec4(c, 0.0, x, a);
	        } else {
	                color = vec4(0.0, 0.0, 0.0, a);
	        }

	        color.rgb += v - c;

	        return color;
		}

    void main(){
    	 	float hue = 0.7 * (1.0-(v_pos.y));
        gl_FragColor = hsv_to_rgb(hue,1.,1.,0.8); //vec4(1,1,1,0.1);
    }
  """
  val g = frag + """
    void main(){
    		float c = 0.7+v_pos.y;
        gl_FragColor = vec4(0,1,0,0.25);
    }
  """
  val r = frag + """
    void main(){
    		float c = 0.7+v_pos.y;
        gl_FragColor = vec4(1,0,0,0.25);
    }
  """
}

object Omni extends Animatable with OmniDrawable {
	
	val omni = new OmniStereo
	var omniEnabled = true

	val lens = new Lens()
	lens.near = 0.01
	lens.far = 40.0
	lens.eyeSep = 0.03

	var omniShader:Shader = _

  var mode = "omni"

	// omni.mStereo = 1
	// omni.mMode = omni.StereoMode.ACTIVE

	override def init(){
    if( omniShader == null){
    	// mCubeProgram = Shader.load("cubeProgram",OmniShader.vGeneric, OmniShader.fCube)
			// mWarpProgram = Shader.load("warpProgram",OmniShader.vGeneric, OmniShader.fWarp)
      omniShader = Shader.load("omni", OmniShader.glsl + S.vOmni, S.frag1 )
      omni.configure("../seer-modules/seer-allosphere/calibration","gr02")
      // omni.configure("../../../calibration-current",java.net.InetAddress.getLocalHost().getHostName())
      omni.onCreate

      omni.mStereo = 0
      omni.mMode = StereoMode.MONO

    }		
	}

	override def draw(){
		
		if( omniShader == null){ init()}
		val vp = Viewport(Window.width, Window.height)

		// omni.drawWarp(vp)
		// omni.drawDemo(lens,Camera.nav,vp)

		// onDrawOmni()

		// omni.drawSphereMap(t, lens, Camera.nav, vp)

		// if (omniEnabled) {
			omni.onFrame(this, lens, Camera.nav, vp);
		// } else {
			// omni.onFrameFront(this, lens, Camera.nav, vp);
		// }
	}

	override def onDrawOmni(){
		Shader("omni").begin
		omni.uniforms(omniShader);

		val c = Cube().translate(1,1,0)
		val c2 = Cube()
		c.draw
		c2.draw
		
		Shader("omni").end
	}

}


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor
 
class Node extends Actor with ActorLogging {
 
  val cluster = Cluster(system)
 
  // subscribe to cluster changes, re-subscribe when restart 
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }
  override def postStop(): Unit = cluster.unsubscribe(self)
 
  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}


Script
