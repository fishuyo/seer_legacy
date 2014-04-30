

class Skel
	include_package "com.fishuyo.seer.skeleton"
  	include_package "com.fishuyo.seer.util"

	def initialize
		@frame = 0

		@dx=0.0
		@dy=0.0
		@dz=0.0
		@ry=0.0
		@t=0.0
		@time=0.0

		@skyshader = nil
		@skyload=false
		@skyvert=""
		@skyfrag=""
		@bodyshader = nil
		@bodyload=false
		@bodyvert=""
		@bodyfrag=""

		@color = Vec3.new(0,0.5,0.5)
		@newColor = Vec3.new(0,0.5,0.5)
		@dur = 5.0

		@rand = com.fishuyo.seer.util.Random

		# SceneGraph.root.camera.nav.pos.set(0,0.5,0)

		OSC.clear()
		OSC.disconnect()
		OSC.listen(7110)
		# OSC.connect("127.0.0.1", 8008)
		OSC.connect("192.168.0.255", 8008)
		# OSC.connect("192.168.1.255", 8008)

		Main.drawFabric(true)

		OSC.bind("/new_user", lambda{|f| puts "new user"; Main.skeletons.apply(f[0]).calibrating(true) })
		OSC.bind("/user/1", lambda{|f| Main.skeletons.apply(0).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
		OSC.bind("/user/2", lambda{|f| Main.skeletons.apply(1).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
		OSC.bind("/user/3", lambda{|f| Main.skeletons.apply(2).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
		OSC.bind("/user/4", lambda{|f| Main.skeletons.apply(3).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
		OSC.bind("/new_skel", lambda{|f| puts "calibrated"; s = Main.skeletons.apply(f[0]); s.calibrating(false); s.tracking(true) })
		OSC.bind("/lost_user", lambda{|f| puts "lost user"; s = Main.skeletons.apply(f[0]); s.calibrating(false); s.tracking(false) })
		
		OSC.bind("/joint", lambda{|f|
			id = f[1]
			s = Main.skeletons.apply(id)
			s.tracking(true)
			s.setShader("")
			s.setShader("body")
			j = s.joints.apply(f[0])
			x=2*f[2]-1
			y=1.0-f[3]
			z=f[4]
			j.pose.pos.set(2*f[2]-1,(1.0-f[3]),f[4])
			pos = Vec3.new(x,y,z)
			# j.apply(pos)
			# puts j.vel.mag if f[0] == "l_hand"
			id = 1
			if f[0] == "head"
				OSC.endBundle()
				OSC.startBundle(30)
				OSC.send("/l_fabric", Main.fabric.averageVelocity()*10.0, Main.fabric.averageSpringLength())
				OSC.send("/r_fabric", Main.fabric2.averageVelocity()*10.0, Main.fabric2.averageSpringLength())
			end
			OSC.send("/" + id.to_s + "/joint/" + f[0], 2*f[2]-1, 1-f[3], f[4])

			if f[0] == "r_hand"
				Main.fabric.pins.apply(0).set( pos )
			elsif f[0] == "r_elbow"
				Main.fabric.pins.apply(1).set( pos )
			elsif f[0] == "r_shoulder"
				Main.fabric.pins.apply(2).set( pos )
			elsif f[0] == "l_hand"
				Main.fabric2.pins.apply(0).set( pos )
			elsif f[0] == "l_elbow"
				Main.fabric2.pins.apply(1).set( pos )
			elsif f[0] == "l_shoulder"
				Main.fabric2.pins.apply(2).set( pos )
			elsif f[0] == "r_hip"
				Main.fabric3.pins.apply(0).set( pos )
			elsif f[0] == "l_hip"
				Main.fabric3.pins.apply(1).set( pos )
			elsif f[0] == "r_knee"
				Main.fabric3.pins.apply(4).set( pos )
			elsif f[0] == "l_knee"
				Main.fabric3.pins.apply(5).set( pos )
			elsif f[0] == "r_foot"
				Main.fabric3.pins.apply(2).set( pos )
			elsif f[0] == "l_foot"
				Main.fabric3.pins.apply(3).set( pos )
			end
		})

		OSC.bind("/skyshader", lambda{|f| @skyload=true; @skyvert=f[0]; @skyfrag=f[1]})
		OSC.bind("/skyshader/uniforms", lambda{|f| @skyshader.uniforms.update(f[0],f[1]) })
		OSC.bind("/bodyshader", lambda{|f| @bodyload=true; @bodyvert=f[0]; @bodyfrag=f[1]})
		OSC.bind("/bodyshader/uniforms", lambda{|f| @bodyshader.uniforms.update(f[0],f[1]) })
		
		OSC.bind("/ground/warp", lambda{|f| Main.warpGround() })
		OSC.bind("/ground/reset", lambda{|f| Main.resetGround() })
		OSC.bind("/ground/color", lambda{|f| @t=0.0; @newColor = Vec3.new(f[0],f[1],f[2]) })
		OSC.bind("/ground/transitionTime", lambda{|f| @dur = f[0] })


		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 

			if i == 1
				Shader.apply("composite")
				# Shader.shader.get.uniforms.update("u_blend0", f[0])
				# Shader.shader.get.uniforms.update("u_blend1", f[1])
				# Shader.shader.get.uniforms.update("u_blend0", 0.25)
				# Shader.shader.get.uniforms.update("u_blend1", 0.75)

			elsif i == 2
				@dx += f[2] * 0.01
				@dy += f[3] * 0.01
				Main.fabric.pins.apply(0).set( Vec3.new(@dx,@dy,0) )
			elsif i == 3
				@dx += f[2] * 0.01
				@dz += f[3] * 0.01
			end

			Main.trace.apply(Vec3.new(@dx,@dy,@dz))
		})

		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("g", lambda{
			Main.warpGround()
		})
		Keyboard.bind("c", lambda{
			Main.resetGround()
		})
		Keyboard.bind("l", lambda{
			OSC.connect("localhost",7110)
			OSC.send("/skyshader", vert(), frag2() )
		})


	end

	def init
		Shader.apply("composite")
		Shader.shader.get.uniforms.update("u_blend0", 0.20)
		Shader.shader.get.uniforms.update("u_blend1", 0.80)
	end

	def animate(dt)
		if @skyshader == nil
			@skyshader = Shader.load("sky",vert(),frag())
			@skyshader.uniforms.update("amp",0.05)
			@skyshader.uniforms.update("thick",0.05)
		elsif @skyload
			@skyload = false
			@skyshader = Shader.load("sky",@skyvert,@skyfrag)
		end

		if @bodyshader == nil
			@bodyshader = Shader.load("body",vert(),frag2())
			@bodyshader.uniforms.update("amp",0.05)
			@bodyshader.uniforms.update("thick",0.05)
		elsif @bodyload
			@bodyload = false
			@bodyshader = Shader.load("body",@bodyvert,@bodyfrag)
		end

		@skyshader.uniforms.update("time",@time)

		@bodyshader.uniforms.update("time",@time)


		@time += dt
		@t += dt
		@frame += 1

		# puts Main.fabric.averageVelocity() * 10.0
		# puts (Main.skeletons.apply(1).joints.apply("r_hip").pose.pos - Main.skeletons.apply(1).joints.apply("r_knee").pose.pos).mag()

		# Main.node.scene.alpha(1.0)
		# Scene.alpha(1.0)
		# puts Main.node.scene.alpha
    	# node.scene.alpha = math.abs(math.sin(DesktopApp.app.frameCount/100.f).toFloat)

		@dur = 5.0
		if @t < @dur
			c = @color.lerp(@newColor, @t/@dur)
			Main.groundM.material.color.set(c.x,c.y,c.z)
		else
			f = @rand.float
			@color = @newColor
			@newColor = Vec3.new(f[],f[],f[])
			# @newColor = Vec3.new(f[]*0.1,f[]*0.3,f[]*0.5)
			# @newColor = Vec3.new(0.0,0.2,0.2)
			@t = 0.0
		end
	end

	def draw
		# puts @frame
	end

	def vert
		return "
			attribute vec4 a_position;
			attribute vec2 a_texCoord0;
			attribute vec4 a_color;

			uniform mat4 u_projectionViewMatrix;

			varying vec4 v_color;
			varying vec2 v_texCoord;
			varying vec3 v_pos;

			void main() {
			  v_pos = a_position.xyz;
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
			varying vec3 v_pos;

			uniform float amp;
			uniform float time;
			uniform float thick;
			uniform float frequency;

			void main() {
			  vec2 uv;
			  vec2 uv1 = 2. * v_texCoord - 1.;
			  uv.x = uv1.y;
			  uv.y = uv1.x;
			  
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

	def frag2
		return "
			#ifdef GL_ES
			    precision mediump float;
			#endif

			varying vec4 v_color;
			varying vec2 v_texCoord;
			varying vec3 v_pos;

			uniform float amp;
			uniform float time;
			uniform float thick;
			uniform float frequency;

			void main() {
			  vec2 uv;
			  vec2 uv1 = 2. * v_texCoord - 1.;
			  uv.x = uv1.y;
			  uv.y = uv1.x;
			  			  
			  float c = 0.;
			  float thickness = thick / 10.0;
			  float freq = 2.0 + frequency;

			  for( float i = 0.; i < 8.; i++ ){
			    uv.x += sin( uv.y + time * freq ) * amp;
			    uv.x = abs( 1./uv.x ) * amp;    
			    c += abs( thickness / uv.x );    
			  }

			  //c = cos(uv.x) + sin(uv.y);
			  //c = c*c;

			  gl_FragColor = vec4(vec3(c,amp,c),1 );
			}
		"
	end

end
Skel.new
