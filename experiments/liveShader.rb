

class LiveShader
	include_package "com.fishuyo.seer.examples.shader"
  	include_package "com.fishuyo.seer.util"

	def initialize
		@frame = 0

		@dx=0.05
		@dy=0.05
		@dz=0.0
		@ry=0.0


		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 

			if i == 1
				# Shader.shader.get.uniforms.update("u_blend0", f[0])
				# Shader.shader.get.uniforms.update("u_blend1", f[1])
				# Shader.shader.get.uniforms.update("u_blend0", 0.25)
				# Shader.shader.get.uniforms.update("u_blend1", 0.75)

			elsif i == 2
				@dx += f[2] * 0.01
				@dy += f[3] * 0.01

			end
		})

		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("n", lambda{
		})
		Keyboard.bind("c", lambda{
		})

		Shader.load("liveShader",vert(),frag())
		@shader = nil
		@t = 0.0

	end

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

	def frag
		return "
			#ifdef GL_ES
			    precision mediump float;
			#endif

			varying vec4 v_color;
			varying vec2 v_texCoord;

			uniform float amp;
			uniform float time;
			uniform float thick;
			uniform float frequency;

			void main() {
			  vec2 uv = 2. * v_texCoord - 1.;
			  
			  float c = 0.;
			  float thickness = thick / 10.0;
			  float freq = 2.0 + frequency;

			  for( float i = 0.; i < 8.; i++ ){
			    uv.x += sin( uv.y + time * freq ) * amp;
			    //uv.x = abs( 1./uv.x ) * amp;    
			    c += abs( thickness / uv.x );    
			  }

			  gl_FragColor = vec4( vec3(c,amp,amp),1 );
			}
		"
	end

	def animate(dt)
		if @shader == nil
			@shader = Shader.load("liveShader",vert(),frag())
		end

		@t += dt
		@frame += 1

		@shader.uniforms.update("time",@t)
		@shader.uniforms.update("amp",@dx)
		@shader.uniforms.update("thick",@dy)
		@shader.uniforms.update("frequency",0.0) #@t)
		# Shader.apply("liveShader")
		# Shader.shader.get.uniforms.update("time", Main.t)
	end

end
LiveShader.new
