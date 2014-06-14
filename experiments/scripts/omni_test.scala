
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import allosphere._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map



object Script extends SeerScript {

	override def draw(){

		Omni.draw
	}

	override def animate(dt:Float){


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
      // omni.configure("../seer-modules/seer-allosphere/calibration","gr02")
      omni.configure("../../../calibration-current",java.net.InetAddress.getLocalHost().getHostName())
      omni.onCreate

    }		
	}

	override def draw(){
		
		if( omniShader == null){ init()}
		val vp = Viewport(Window.width, Window.height)

		omni.drawWarp(vp)
		// omni.drawDemo(lens,Camera.nav,vp)

		// onDrawOmni()

		// omni.drawSphereMap(t, lens, Camera.nav, vp)

		// if (omniEnabled) {
			// omni.onFrame(this, lens, Camera.nav, vp);
		// } else {
			// omni.onFrameFront(this, lens, Camera.nav, vp);
		// }
	}

	override def onDrawOmni(){
		Shader("omni").begin
		omni.uniforms(omniShader);

		val c = Cube()
		c.scale(10.f)
		c.draw
		// Script.pos1.draw
		// Script.neg3.draw
		// Script.diff.draw
		
		Shader("omni").end
	}

}

Script
