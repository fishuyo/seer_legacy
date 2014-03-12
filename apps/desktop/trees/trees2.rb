# require 'java'

class TreeScript
	include_package "com.fishuyo.seer.util"
	include_package "com.fishuyo.seer.trees"
	include_package "com.fishuyo.seer.io.kinect"
	include_package "com.fishuyo.seer.video"

	$Rand = Java::com.fishuyo.seer.util.Random

	def initialize

		for i in 0...Main.trees.length()
			t = Main.trees[i]
			depth = 8
			tree0(i)
			t.setAnimate(false)
			t.setReseed(true)
			t.setDepth(depth)
			t.branch(depth)
		end


		Trees.gv.set(0.0,1.0,0.0)

		### Trackpad state ###
		@mx=0.0
		@my=0.0
		@mz=0.0 
		@rz=0.1
		@rx=0.0
		@ry=0.0

		@x=0.0
		@y=5.0
		@z=0.0

		@tree = Main.tree

		@shader = nil
		@time = 0.0

		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{|i,f|
			t = @tree
			if i == 1
				ur = SceneGraph.root.camera.nav.ur()
				uf = SceneGraph.root.camera.nav.uf()

				# Main.tree.root.applyForce( (v * 10.0 * Main.rms) )
				# t.root.applyForce( Vec3.apply((f[0]-0.5)*1.0*f[4],0,(f[1]-0.5)*f[4]))
				t.root.applyForce( ur*(f[0]-0.5)*2.0*f[4])
				t.root.applyForce( uf*(f[1]-0.5)*-2.0*f[4])

				# Shader.apply("composite")
				# Shader.shader.get.uniforms.update("u_blend0", f[0])
				# Shader.shader.get.uniforms.update("u_blend1", f[1])

			elsif i == 2
				@mx = @mx + f[2]*0.05  
				@x = @x + f[2]*0.05  
				@mz = @mz + f[3]*-0.05
				@z = @z + f[3]*-0.05
				Shader.lightPosition.set(@x*10,@y*10,@z*10)
			elsif i == 3
				@mx = @mx + f[2]*0.05
				@x = @x + f[2]*0.05  
				@my = @my + f[3]*0.05
				@y = @y + f[3]*0.05
				Shader.lightPosition.set(@x*10,@y*10,@z*10)

			elsif i == 4
				@rz = @rz + f[3]*0.05
				@rx = @rx + f[2]*0.05
			end

			if i > 1
				for i in 0...Main.trees.length()
					t = Main.trees[i]
					t.bAngle.y.setMinMax( 0.05, @mx,false )
					#t.bAngle.y.set(@mx)
				    t.sRatio.setMinMax( 0.05, @mz, false )
				    #t.sRatio.set( @mz )
				    t.bRatio.setMinMax( 0.05, @my, false )
				    #t.bRatio.set( @my )
				    t.sAngle.x.setMinMax( 0.05, @rx, false )
				    t.bAngle.x.setMinMax( 0.05, @rx, false )
				    #t.sAngle.x.set( @rx )
				    t.sAngle.z.setMinMax( 0.05, @rz, false )
				    t.bAngle.z.setMinMax( 0.05, @rz, false )
				    #t.sAngle.z.set( @rz )
				    #t.branch(depth)
				    t.refresh()
				end

			    # t.root.accel.zero
			    # t.root.euler.zero
			end
		})


		move = Vec3.apply(0,0,0)
		rot = Vec3.apply(0,0,0)
		nmove = Vec3.apply(0,0,0)
		nrot = Vec3.apply(0,0,0)

		Trees.setDamp(150.0)

		# Kinect.threshold.set( 0.0, 0.4, 0.0 )
		# Kinect.device.get().setTiltAngle(0.0)
		# Kinect.bgsub.updateBackgroundNextFrame()
		# Kinect.bgsub.setThreshold(10.0)
		# Kinect.setSizeThreshold( 20 )
		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("1", lambda{
			@tree = Main.trees[0]
		})
		Keyboard.bind("2", lambda{
			@tree = Main.trees[1]
		})
		Keyboard.bind("3", lambda{
			@tree = Main.trees[2]
		})
		Keyboard.bind("4", lambda{
			@tree = Main.trees[3]
		})

		Keyboard.bind("g", lambda{
			# Main.ground.clear()
			# Plane.generateMesh(Main.ground,100,100,100,100,Quat.up())
			Main.ground.vertices.foreach( lambda{|v| v.set(v.x,v.y+$Rand.float(-1,1).apply()*0.02*(v.x).abs,v.z) })
			Main.ground.recalculateNormals()
			Main.ground.update()
		})

		Keyboard.bind("l", lambda{
			@x=0.0; @y=5.0; @z=0.0
			Shader.lightPosition.set(0,30,0)
			# ScreenCapture.toggleRecord()
		})

		Mouse.clear()
		Mouse.use()
		Mouse.bind( "drag", lambda{ |i|
			x = i[0]
			y = i[1]
			ray = Camera.ray(x,y)
			t = ray.intersectQuad(Vec3.new(0,0,0), 50, 50, Quat.up() )
			if t.isDefined()
				pos = ray.apply(t.get)
				@tree.root.pose.pos.set(pos)
				@tree.refresh()
			else
			end
		})
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

	def tree0 i
		t = Main.trees[i]
		t.branchNum.setChoices([0,1,2].to_java :int)
		t.branchNum.setProb([0.0,1.0,0.0].to_java :float)
		t.sLength.setMinMax(0.5,0.5,false)

		t.bAngle.z.setMinMax(0.0,0.0,false)
		t.bAngle.y.setMinMax(0.0,1.2,true)
		t.bAngle.x.setMinMax(0.0,0.0,false)

		t.sAngle.z.setMinMax(1.0,1.0,false)
		t.sAngle.y.setMinMax(0.0,0.0,false)
		t.sAngle.x.setMinMax(0.0,0.0,false)
	end

	def animate dt
		if @shader == nil
			@shader = Shader.load("sky",vert(),frag())
		end

		@shader.uniforms.update("time",@time)
		@shader.uniforms.update("amp",0.05)
		@shader.uniforms.update("thick",0.05)

		# Main.cube2.rotate(0.0,0.01,0)
		# Main.atmossphere.rotate(0.0,0.01,0)
		@time += dt
		# @frame += 1

		t = Main.tree

		# puts Quat.up().toZ()

	end
end
TreeScript.new

