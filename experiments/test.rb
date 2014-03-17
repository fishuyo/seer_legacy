

class Test
	include_package "com.fishuyo.seer.test"
  	# include_package "com.fishuyo.seer.t.util"
  	include_package "com.fishuyo.seer.video"


	def initialize
		@frame = 0

		@dx=0.0
		@dy=0.0
		@dz=0.0
		@ry=0.0

		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 

			if i == 1
				# Main.node.scene.fade(f[1])
				# Scene.fade(f[0])
				Main.s.applyForce(Vec3.new((f[0]-0.5)*10,0,(f[1]-0.5)*10,))

			elsif i == 3
				@dx += f[2]*0.01
				@dy += f[3]*0.01
				Main.cube.pose.pos.set(@dx,@dy,@dz)
				Text.setSmoothing(@dy)

			elsif i == 2
				@dx += f[2]*0.01
				@dz += f[3]*-0.01
				Main.cube.pose.pos.set(@dx,@dy,@dz)
				Main.movet(@dx*100,-@dz*100)
				# Main.pix.setColor(1,0,0.3,0.05)
				# Main.pix.fillCircle(@dz,@dx,f[4]*4)

			elsif i == 4
				@ry += f[3]*-0.1
				# $Sine.a(@ry)
			else
			end
		})

		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("n", lambda{
			cube = Cube.apply(4)
			Scene.push(cube)
			cube.pose.pos.set( Main.cube.pose.pos + com.fishuyo.seer.util.Random.vec3.apply()*0.1)
			f = com.fishuyo.seer.util.Random.float
			cube.color.set(RGBA.new(f[],f[],f[],0.1))
		})
		Keyboard.bind("c", lambda{
			Scene.drawable.clear()
		})
		Keyboard.bind("r", lambda{
			Main.s.particles.foreach( lambda{|p| p.reset() })
		})
		Keyboard.bind("l", lambda{
			ScreenCapture.toggleRecord()	
		})

		Camera.nav.pos.set(3.2720168,4.3370976,-0.3198568)
		Camera.nav.quat.set(0.6508481,-0.70408297, 0.19278999, -0.20855892)

	end

	def animate(dt)
		# puts "hi"
		@frame += 1
		Main.node.scene.alpha(1.0)
		f = com.fishuyo.seer.util.Random.float(-1,1)

		Shader.lightPosition.set(1,1,-2)
		# Main.cubes.foreach( lambda{|c|
		# 	# c.scale.set(0.1)
		# 	c.scale(1,1+f[]*0.005,1)
		# })
		# Scene.alpha(1.0)
		# puts Main.node.scene.alpha
    # node.scene.alpha = math.abs(math.sin(SimpleAppRun.app.frameCount/100.f).toFloat)

		# Shader.blend_=(false)
		# Main.cube.rotate(3,0,0)
		# Main.wire.rotate(0,0,-0.1)
	end

	def draw
		puts Main.y
		# puts @frame
	end

end
Test.new
