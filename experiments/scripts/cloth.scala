
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._
import particle._

import parsers._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

Scene.alpha = 0.5
SceneGraph.root.depth = false

object Script extends SeerScript {
	implicit def f2i(f:Float) = f.toInt

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


	val mesh = Plane.generateMesh(8,2,80,40)
	mesh.primitive = Lines
	val s = new SpringMesh(mesh,1.f)

	for(p <- s.particles.takeRight(80)){ 
		s.pins += AbsoluteConstraint(p,p.position+Vec3(0,4,Random.float()*0.01f))
	}
	s.updateNormals = false
	val m = Model(s)

	val cursor = Sphere().scale(0.05)

	var lpos = Vec2()
	var vel = Vec2()

	Gravity.set(0,0,0)

	override def draw(){
		Shader("s1")

		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    S.shaders("s1").uniforms("time") = 1
    S.shaders("s1").uniforms("color") = RGB(0,0.6,0.6)

		// Shader.setMatrices
		Shader("s1").begin
		m.draw
		cursor.draw
		model.draw


		Shader("s1").end

	}

	override def animate(dt:Float){
		if( Mouse.status() == "drag"){
			vel = (Mouse.xy() - lpos)/dt
			// println(vel)
			// s.applyForce( Vec3(vel.x,vel.y,0)*10.f)
			val r = Camera.ray(Mouse.x()*Window.width, (1.f-Mouse.y()) * Window.height)
			s.particles.foreach( (p) => {
				val t = r.intersectSphere(p.position, 0.25f)
				if(t.isDefined){
					// val p = r(t.get)
					p.applyForce(Vec3(vel.x,vel.y,0)*150.f)
					cursor.pose.pos.set(r(t.get))
				}
			})
		}
		lpos = Mouse.xy()

		if(dirty){
			model = Model()
			modelGenerator.buildModel(model)
			dirty = false
			model.scale(0.2).translate(0,5,0)
		}

		s.animate(dt)
	}

	var rotz = 10.0
	var roty = 90.0
	Trackpad.clear
	Trackpad.connect
	Trackpad.bind( (i,f)=>{
		i match{
			// case 1 => s.applyForce( Vec3(f(0)-.5f,f(1)-.5f,0)*10.f)
			case 4 => Gravity.set(Vec3(f(0)-.5f,f(1)-.5f,0)*10.f)
			case 3 => roty += f(3); modelGenerator.set("roty",roty); dirty = true
								rotz += f(2); modelGenerator.set("rotz",rotz); dirty = true

			case _ => ()
		}
	})

	Mouse.clear
	Mouse.use

	Keyboard.clear()
	Keyboard.use()
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

      Shader("composite")
      Shader.shader.get.uniforms("u_blend0") = 0.2
      Shader.shader.get.uniforms("u_blend1") = 0.9
    })
  })


	// VRPN.clear
	// VRPN.bind("b",(p) => {
	// })
}


object S {
  val shaders = collection.mutable.Map[String,Shader]()
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
        float b = clamp(t - d + 0.4, 0.2,1.0);
        vec3 c = mix( b*color, b*color+b*vec3(0,0.25,0), 1.0-b);
        gl_FragColor = vec4(c,b);
        // gl_FragColor = vec4(color,0.3); //vec4(c,b);
    }
  """
}
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})

Script
