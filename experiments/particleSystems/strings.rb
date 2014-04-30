
class StringTest
	include_package "com.fishuyo.seer"
	include_package "com.fishuyo.seer.io"
	include_package "com.fishuyo.seer.maths"
	include_package "com.fishuyo.seer.maths.particle"
	include_package "com.fishuyo.seer.spatial"
	include_package "com.fishuyo.seer.graphics"
	include_package "com.fishuyo.seer.examples.particleSystems.strings"

	def initialize

		@tscale = 1.0

		Mouse.clear()
		Mouse.use()
		Mouse.bind("drag", lambda{|i|
			x = (i[0] / DesktopApp.app.width) * 20.0
			y = (1 - i[1] / DesktopApp.app.height) * 20.0

			Main.strings[0].pins[0].q.position.set(x,y,0)
		})

		Keyboard.clear()
		Keyboard.use()
		Keyboard.bind("t", lambda{
			@tscale = -@tscale
		})

		dx1=0.0
		dy1=0.0
		dx2=0.0
		dy2=0.0
		dl=0.1

		Trackpad.connect()
		Trackpad.clear()
		Trackpad.bind(lambda{|i,f|
			s=5.0
			if i == 1
				# // Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
				# // Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
				# // Main.strings().apply(2).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
				# // Main.strings().apply(3).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
				# // Main.strings().apply(4).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
				Shader.apply("composite")
				Shader.shader.get.uniforms.update("u_blend0", f[0])
				Shader.shader.get.uniforms.update("u_blend1", f[1])

			elsif i == 2
				dx1 += f[2]*0.01
				dy1 += f[3]*0.01
				Main.strings[0].pins[0].q.position.lerpTo( Vec3.new(dx1,dy1,0), 0.1)
			elsif i == 3
				dx2 += f[2]*0.01
				dy2 += f[3]*0.01
				Main.strings[0].pins[1].q.position.lerpTo(Vec3.new(dx2,dy2,0), 0.1)

			elsif i == 4
				dl += f[3]*0.001
				Main.strings[0].links.foreach(lambda{|l| l.length(dl) })
			end
		})
		
		OSC.clear()
		OSC.disconnect()
		OSC.listen(7110)
		OSC.bind("/joint", lambda{|f|
			id = f[1]
			x=2*f[2]-1
			y=1.0-f[3]
			z=f[4]
			len=z*0.01

			pos = Vec3.new(x,y,z)

			if f[0] == "r_hand"
				# Main.strings[0].pins[0].q.position.lerpTo( pos, 0.1)
				# Main.strings[0].links.foreach(lambda{|l| l.length(len) })
			elsif f[0] == "r_elbow"
			elsif f[0] == "r_shoulder"
			elsif f[0] == "l_hand"
				Main.strings[0].pins[1].q.position.lerpTo( pos, 0.1)
				Main.strings[0].links.foreach(lambda{|l| l.length(len) })
				
			elsif f[0] == "l_elbow"
			elsif f[0] == "l_shoulder"
			elsif f[0] == "r_hip"
			elsif f[0] == "l_hip"
			elsif f[0] == "r_knee"
			elsif f[0] == "l_knee"
			elsif f[0] == "r_foot"
			elsif f[0] == "l_foot"
			end
		})
		# // Leap.connect()
		# // Leap.clear()
		# // Leap.bind(function(frame){

		# // 	if( frame.hands().isEmpty() ) return
		# // 	var hand = frame.hands().get(0)
		# // 	if( hand.fingers().isEmpty() ) return
		# // 	var count = hand.fingers().count()

		# // 	var s = 0.01

		# // 	for( i=0; i < count; i++){
		# // 		var finger = hand.fingers().get(i)
		# // 		var pos = finger.tipPosition()
		# // 		Main.strings().apply(i).pins().apply(0).position().lerpTo(Vec3(s*pos.getX(),s*pos.getY(),s*pos.getZ()), 0.1)
		# // 	}

		# // })
	end

	def animate dt
		# // println("hi")
		string = Main.strings[0]
		# string.damping_=(20.0)

		Gravity.set(0,0,0)
		Shader.lightAmbient.set( RGBA.apply(0,1,1,1))

		Main.strings.foreach( lambda{|s| s.animate(dt * @tscale)} )

		# p = Main.strings().apply(0).pins().apply(0).position()
		# p = Vec3(p.x(),p.y(),p.z()+50)
		# // DesktopApp.app().camera().nav().pos().lerpTo(p, 0.05)
	end
end
StringTest.new



