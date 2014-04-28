
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

object Script extends SeerScript {

	// val node = new RenderNode
	// node.shader = "test"
	// node.scene.push(Plane())
	// SceneGraph.addNode(node) 

	var shader:Shader = null
	var t = 0.f
	var zoom = 1.f
	var mouse = Vec2()

	Trackpad.clear()
	Trackpad.connect()
	Trackpad.bind(  (i,f) => { 
		if(i == 1){
			mouse += Vec2(f(2),f(3)) * 0.05f * math.pow(zoom,8)
		}else if(i == 2){
			zoom += f(3) * -0.001f
		}
	})

	override def draw(){
	}

	override def animate(dt:Float){
		t += dt
		if( shader != null){
			shader.uniforms("time") = t
			shader.uniforms("zoom") = zoom
			shader.uniforms("mouse") = mouse
		}
	}

	override def onUnload(){
		SceneGraph.roots -= node
	}

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
		uniform float zoom;
		uniform vec2 mouse;

		void main(){

		    vec2 p = -1.0 + 2.0 * v_texCoord.xy;

		    float zoo = zoom;
		    zoo = pow( zoo,8.0);
		    vec2 cc = p*zoo + mouse;

		    vec2 z  = vec2(0.0);
		    float m2 = 0.0;
		    float co = 0.0;

		    for( int i=0; i<256; i++ )
		    {
		        if( m2<1024.0 )
		        {
		            z = cc + vec2( z.x*z.x - z.y*z.y, 2.0*z.x*z.y );
		            m2 = dot(z,z);
		            co += 1.0;
		        }
		    }

		    co = co + 1.0 - log2(.5*log2(m2));

		    co = sqrt(co/256.0);
		    gl_FragColor = vec4( .5+.5*cos(6.2831*co+0.0),
		                         .5+.5*cos(6.2831*co+sin(time)),
		                         .5+.5*cos(6.2831*co+cos(time)),
		                         1.0 );
		}
	"""

	Run(()=>{ shader = Shader.load("test",vert,frag)})

}
Script
