
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._
import particle._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

Scene.alpha = 0.5
SceneGraph.root.depth = false

object Script extends SeerScript {
	implicit def f2i(f:Float) = f.toInt

	val mesh = Mesh()
	mesh.maxVertices = 1000
	mesh.maxIndices = 1000
	mesh.primitive = LineStrip
	val s = new SpringMesh(mesh,1.f)
	s.updateNormals = false
	val m = Model(s)

	val spider = Sphere().scale(0.05)
	s += Particle(Vec3(0,4,0))
	s += Particle(Vec3(0,3.9,0))
	s.pins += AbsoluteConstraint(s.particles.head,Vec3(0,4,0))
	s.springs += LinearSpringConstraint(s.particles.head, s.particles.last, 0.1f, 0.2f)		

	val cursor = Sphere().scale(0.05)
	var lpos = Vec2()
	var vel = Vec2()

	var t=0.f

	Gravity.set(0,-5,0)

	override def draw(){
		Shader("s1")

		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    S.shaders("s1").uniforms("time") = 1
    S.shaders("s1").uniforms("color") = RGB(0,0.6,0.6)

		// Shader.setMatrices
		Shader("s1").begin
		m.draw
		cursor.draw

		Shader("s1").end

	}

	override def animate(dt:Float){

		if(t==0.f){
			println("add")
			s += Particle(s.particles.last.position+Vec3(0,-.01,0))
			s.springs += LinearSpringConstraint(s.particles.takeRight(2).head, s.particles.last, 0.01f, 0.2f)		
		}else if( t > 0.f){
			s.springs.last.length += 0.0001f
		}
		t += dt

		if(t > 1.f){
			println("reset")
			t = 0.f
		}




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

		s.animate(dt)
	}

	Trackpad.clear
	Trackpad.connect
	Trackpad.bind( (i,f)=>{
		i match{
			// case 1 => s.applyForce( Vec3(f(0)-.5f,f(1)-.5f,0)*10.f)
			//case 3 => Gravity.set(Vec3(f(0)-.5f,f(1)-.5f,0)*10.f)
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
        // gl_FragColor = vec4(c,b);
        gl_FragColor = vec4(color,0.3); //vec4(c,b);
    }
  """
}
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})

Script
