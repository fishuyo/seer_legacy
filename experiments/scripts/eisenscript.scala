
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import spatial._
import io._
import util._

import parsers._

import collection.mutable.Map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

Scene.alpha = 1 //.5
SceneGraph.root.depth = true //false

object Script extends SeerScript {

	var modelGenerator = EisenScriptParser("""
		set maxdepth 40
		set maxobjects 1000

		var rotz = 10.0
		var roty = 90.0

		//{ h 0.1 sat 0.7 } spiral
		4 * { ry 90 } spiral

		// 8 * { ry 45 h 0.1 sat 0.89 } spiral
		 
		rule spiral w 10 {
			{ y 0.9 rz rotz s 0.94 h 0.01 sat 0.9} spiral
			{ s 0.25 1 0.25 } cylinder	
		}
		rule spiral w 10 {
			{ y 0.9 ry roty rz rotz s 0.93 h 0.01 sat 0.9} spiral
			{ s 0.25 1 0.25 } cylinder	
		}
		 
		rule spiral w 1 {
			spiral
			{ ry 180 h .1} spiral
		}
		// rule spiral w 1 {
			// { ry -90 h .1} spiral
			// { ry 90  h -.1} spiral
		// }

	""")

	var model = Model()
	model.material = new SpecularMaterial
	model.material.color.set(1,0,0,1)

	var dirty = true;
	var alpha = 0.2;
	var beta = 0.8;
	var t = 0.f

	override def draw(){
		// Shader("s1")

		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    S.shaders("s1").uniforms("time") = 1
    S.shaders("s1").uniforms("color") = RGB(0,0.6,0.6)

		// Shader.setMatrices
		// Shader("s1").begin
		model.draw
		// Shader("s1").end

	}

	override def animate(dt:Float){
		t += dt

		Shader("composite")
    val fb = Shader.shader.get
    fb.uniforms("u_blend0") = alpha
    fb.uniforms("u_blend1") = beta

		if(dirty){
			model = Model()
			modelGenerator.buildModel(model)
			dirty = false
		}
		FPS.print
	}


	var rotz = 10.0
	var roty = 90.0
	Trackpad.clear()
	Trackpad.connect()
	Trackpad.bind( (i,f)=>{ 
		i match {
			case 1 =>
			case 2 => rotz += f(2); modelGenerator.set("rotz",rotz); dirty = true
			case 3 => roty += f(3); modelGenerator.set("roty",roty); dirty = true
			case _ => ()
		} 
	})

	Keyboard.clear()
	Keyboard.use()
	Keyboard.bind("g", ()=>{dirty=true})
	Keyboard.bind("p", () =>{
    println(Camera.nav.pos)
  })
  Keyboard.bind("v", () =>{
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

	var B = Pose()
	VRPN.clear
	VRPN.bind("b",(p)=>{
		B = B.lerp(p,0.05)
		roty = B.pos.y*100
		rotz = B.pos.z*10
		modelGenerator.set("rotz",rotz)
		modelGenerator.set("roty",roty)
		dirty = true
	})

}



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
    uniform vec3 color;
  """
  val frag1 = frag + """
    void main(){
        vec2 uv = 2. * v_texCoord - 1.;
        float d = pow(uv.x,2.0) + pow(uv.y,2.0);

        float t = 0.5*(sin(time)+1.0);
        float b = clamp(t - d + 0.3, 0.0,1.0);
        vec3 c = mix( b*color, b*color+b*vec3(0,0.25,0), 1.0-b);
        gl_FragColor = vec4(c,b);
    }
  """
}
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})
// Run(()=>{ S.shaders("s2") = Shader.load("s2",S.vert,S.frag2)})


Script
