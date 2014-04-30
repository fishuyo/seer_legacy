

class Grass

  	include_package "com.fishuyo.seer"
	include_package "com.fishuyo.seer.io"
	include_package "com.fishuyo.seer.maths"
	include_package "com.fishuyo.seer.maths.particle"
	include_package "com.fishuyo.seer.spatial"
	include_package "com.fishuyo.seer.graphics"
	include_package "com.fishuyo.seer.util"
	include_package "com.fishuyo.seer.examples.particleSystems.grass"


	def initialize
		@frame = 0
		@t = 0.0
		@duration = 0.0
		@wind = true
		@gusting = false
			  		
		@gen = Java::com.fishuyo.seer.util.Random.float(-0.001,0.001)
		@mag = 0.0

		@dx1=0.0
		@dy1=0.0
		@dx2=0.0
		@dy2=0.0
		@dl=0.1

		Mouse.clear()
		Mouse.use()
		Mouse.bind("drag", lambda{|i| 
			x = (i[0] / DesktopApp.app.width) * 20.0
			y = (1 - i[1] / DesktopApp.app.height) * 20.0

			# // Main.strings().apply(0).pins().apply(0).position().set(x,y,0)
			
		})

		Trackpad.connect()
		Trackpad.clear()
		Trackpad.bind(lambda{|i,f|
			s=5.0
			
			if i == 2
				@dx1 += f[2]*0.01
				@dy1 += f[3]*0.01
				r1 = Java::com.fishuyo.seer.util.Random.float(-0.0005,0.001*f[2])
				r2 = Java::com.fishuyo.seer.util.Random.float(-0.0005,0.001*f[3])
				Main.blades.foreach( lambda{|b| b.applyForce(Vec3.new(-r2[], 0, -r1[]) ) })

			elsif i == 3
				@dx2 += f[2]*0.01
				@dy2 += f[3]*0.01
				f = Java::com.fishuyo.seer.util.Random.float(0.0,-0.001)
				Main.blades.foreach( lambda{|b| b.applyForce(Vec3.new(0,0, f[]) ) })

			elsif i == 4
				@dl += f[3]*0.001

			elsif i == 5
			end
		})
	end

	def animate(dt)
		@t += dt

		#Shader.bg.set(0.5,0.2,0.65,1)
		Java::com.fishuyo.seer.util.Random.seed()

		if @wind 
			if @t > @duration 
				puts @t
				@t = 0
				@duration = com.fishuyo.seer.util.Random.float.apply()

				if com.fishuyo.seer.util.Random.float.apply() < 0.8
			  		@gusting = true
			  		@mag = @gen[] # com.fishuyo.seer.util.Random.float(-0.001,-0.01).apply()
			  		# puts @mag
				else 
					@gusting = false
				end
			end

			if @gusting
				Main.blades.foreach( lambda{|b| b.applyForce(Vec3.new(0,0,@gen[]) ) })
			end
		end
	end

	def draw
	end

end
Grass.new
