
# ruby class for executing code during runtime
# automatically rerun on save

class LiveShader
	# import our Main class and some seer classes
	include_package "com.fishuyo.seer.examples.graphics.liveshader"
  	include_package "com.fishuyo.seer.util"
  	include_package "rx"

  	# this is a ruby class initialization function
  	# where we specify class member variables
	def initialize
		@t = 0.0
		@zoom = 1.0
		@mouse = Vec2.new(0,0)
		@shader = nil

		# bind callback to OSX Trackpad multitouch events
		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 
			if i == 1
				@mouse += Vec2.new(f[2],f[3]) * 0.05 * @zoom**8
			elsif i == 2
				@zoom += f[3] * -0.001
			end
		})

		# @mouse = new Java::rx.Rx({})

		Mouse.clear()
		Mouse.use()
	end

	#called once per frame from our app
	def animate(dt)
		if @shader == nil
			@shader = Shader.load("liveShader",vert(),frag())
		end

		@t += dt

		@shader.uniforms.update("time",@t)
		@shader.uniforms.update("zoom",@zoom)
		@shader.uniforms.update("mouse",@mouse)
	end

	# our custom vertex shader code
	def vert
		return "
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
		"
	end

	# our custom fragment shader code
	def frag
		return "
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
		"
	end
end

# we return a new instance of this class from this script
# as a handle for our application to reference
LiveShader.new

