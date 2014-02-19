

class Skel
	include_package "com.fishuyo.seer.skeleton"
  	include_package "com.fishuyo.seer.util"

	def initialize
		@frame = 0

		@dx=0.0
		@dy=0.0
		@dz=0.0
		@ry=0.0


		SceneGraph.root.camera.nav.pos.set(0,0.5,0)

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
			j = s.joints.apply(f[0])
			j.pose.pos.set(2*f[2]-1,(1.0-f[3]),f[4])
			id = 1
			if f[0] == "head"
				OSC.endBundle()
				OSC.startBundle()
			end
			OSC.send("/" + id.to_s + "/joint/" + f[0], 2*f[2]-1, 1-f[3], f[4])

			if f[0] == "r_hand"
				Main.fabric.pins.apply(0).set( j.pose.pos )
			elsif f[0] == "r_elbow"
				Main.fabric.pins.apply(1).set( j.pose.pos )
			elsif f[0] == "r_shoulder"
				Main.fabric.pins.apply(2).set( j.pose.pos )
			elsif f[0] == "l_hand"
				Main.fabric2.pins.apply(0).set( j.pose.pos )
			elsif f[0] == "l_elbow"
				Main.fabric2.pins.apply(1).set( j.pose.pos )
			elsif f[0] == "l_shoulder"
				Main.fabric2.pins.apply(2).set( j.pose.pos )
			end
		})

		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 

			if i == 1
				Shader.apply("composite")
				# Shader.shader.get.uniforms.update("u_blend0", f[0])
				# Shader.shader.get.uniforms.update("u_blend1", f[1])
				# Shader.shader.get.uniforms.update("u_blend0", 0.25)
				# Shader.shader.get.uniforms.update("u_blend1", 0.75)
			end
		})

		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("n", lambda{
		})
		Keyboard.bind("c", lambda{
		})


	end

	def init
		Shader.apply("composite")
		Shader.shader.get.uniforms.update("u_blend0", 0.20)
		Shader.shader.get.uniforms.update("u_blend1", 0.80)
	end

	def animate(dt)
		# puts "hi"

		@frame += 1
		# puts (Main.skeletons.apply(1).joints.apply("r_hip").pose.pos - Main.skeletons.apply(1).joints.apply("r_knee").pose.pos).mag()
		# OSC.send("/1/joint/r_hand", 1.1, 2.2, 3.3)

		# Main.node.scene.alpha(1.0)
		# Scene.alpha(1.0)
		# puts Main.node.scene.alpha
    # node.scene.alpha = math.abs(math.sin(SimpleAppRun.app.frameCount/100.f).toFloat)

		# Shader.blend_=(false)
		# Main.cube.rotate(3,0,0)
		# Main.wire.rotate(0,0,-0.1)
	end

	def draw
		# puts @frame
	end

end
Skel.new
