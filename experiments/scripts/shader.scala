
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

object Script extends SeerScript {

	val node = new RenderNode
	node.shader = "newt"
	node.scene.push(Plane())
	SceneGraph.addNode(node) 

	// val quad = Plane()
	// quad.shader = "test"

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
		// quad.draw
	}

	override def animate(dt:Float){
		t += dt
		if( shader != null){
			shader.uniforms("time") = t/10.f
			shader.uniforms("zoom") = zoom
			shader.uniforms("mouse") = mouse
			shader.uniforms("resolution") = Vec2(1,1)
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
		    precision highp float;
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

	val fNewt = """
		#ifdef GL_ES
		precision highp float;
		#endif

		varying vec4 v_color;
		varying vec2 v_texCoord;

		uniform vec2 resolution;
		uniform float time;
		uniform float zoom;

		#define sLenght(a) dot((a),(a))

		vec2 cInv(vec2 c){
			float sl = dot(c,c);
			return vec2(c.x / sl, -c.y / sl);
		}

		vec2 cMul(vec2 a, vec2 b){
			vec4 t = a.xyxy * b.xyyx;
			return vec2(t.x - t.y, t.z + t.w);
		}

		void main()
		{
			//float zoom = (sin(time/4.0)+1.0) * 8.0 + 1.0;
		    vec2 p = -(zoom/2.0) + zoom * v_texCoord / resolution.xy;
		    p.x *= resolution.x/resolution.y;
			
			float t = time/2.0;
			
			//setting roots of a third grade poly using a Lissajous curve
		    vec2 root1 = vec2(sin(t + 0.00000000) , sin(2.0 * t + 0.00000000));
			vec2 root2 = vec2(sin(t + 2.09439510) , sin(2.0 * t + 2.09439510));
			vec2 root3 = vec2(sin(t + 4.18879020) , sin(2.0 * t + 4.18879020));
			
			const int maxIters = 16;
			float tolerance = 1e-8;
			vec2 r1,r2,r3;
			vec2 prevP = p;
			float iters = 0.0;
			
			r1 = p-root1;
			r2 = p-root2;
			r3 = p-root3;

			for(int iterations = 0; iterations < maxIters; iterations++ ){
				prevP = p;
				//newton-rhapson method iteration
				p = p - cInv( cInv(r1) + cInv(r2) + cInv(r3) );
				
				//check how near we are from the nearest root
				r1 = p-root1;
				r2 = p-root2;
				r3 = p-root3;
				if (min(sLenght(r1),min(sLenght(r2),sLenght(r3))) < tolerance ) {break;}
				iters += 1.0;
			}
			float co = iters;
			
			//the code could be more compact but repeating code makes changing the gradients easier
			float l1 = length(p-root1);
			float l2 = length(p-root2);
			float l3 = length(p-root3);
			float dist0,dist1;
			float tol = log(tolerance);
		    if (l1 <= l2 && l1 <= l3){
				dist0 = log(sLenght(prevP-root1));
				dist1 = log(sLenght(p-root1));
				if (dist1 < tol && dist0 > tol) { co += (tol - dist0) / (dist1 - dist0); }
				co = clamp(co/float(maxIters),0.0,1.0);
				
				//gradient for root1
				gl_FragColor = vec4(
					0.5+0.89*cos(6.2831855*co+0.0),
					0.5+0.48000002*cos(25.132742*co+2.0734513),
					0.5+0.38*cos(50.265484*co+4.1469026),
					1.0);
					
			}else if (l2 <= l1 && l2 <= l3){
				dist0 = log(sLenght(prevP-root2));
				dist1 = log(sLenght(p-root2));
				if (dist1 < tol && dist0 > tol) { co += (tol - dist0) / (dist1 - dist0); }
				co = clamp(co/float(maxIters),0.0,1.0);
				
				//gradient for root2 
				gl_FragColor = vec4(
					0.5+0.6*cos(2.0106194*co+-1.5079645),
					0.5+1.0*cos(4.5867257*co+2.576106),
					0.5+0.82*cos(3.015929*co+2.3247786),
					1.0);
					
			}else if (l3 <= l2 && l3 <= l1){
				dist0 = log(sLenght(prevP-root3));
				dist1 = log(sLenght(p-root3));
				if (dist1 < tol && dist0 > tol) { co += (tol - dist0) / (dist1 - dist0); }
				co = clamp(co/float(maxIters),0.0,1.0);
				
				//gradient for root3 
				gl_FragColor = vec4( .5+.5*cos(6.2831*co+0.0),
		                         .5+.5*cos(6.2831*co+0.4),
		                         .5+.5*cos(6.2831*co+0.7),
		                         1.0 );
								 
			}else{
				gl_FragColor = vec4(0.0 , 0.0, 0.0, 1.0 );
			}
		}
	"""

	Run(()=>{ shader = Shader.load("test",vert,frag)})
	Run(()=>{ shader = Shader.load("newt",vert,fNewt)})

}
Script
