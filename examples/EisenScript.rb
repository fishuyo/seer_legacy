
# ruby class for executing code during runtime
# automatically rerun on save

class EisenScript
	# import our Main class and some seer classes
	include_package "com.fishuyo.seer.examples.graphics.eisenscript"
  	include_package "com.fishuyo.seer.parsers"
  	include_package "com.fishuyo.seer.util"

  	# this is a ruby class initialization function
  	# where we specify class member variables
	def initialize
		@t = 0.0

		# bind callback to OSX Trackpad multitouch events
		Trackpad.clear()
		Trackpad.connect()
		Trackpad.bind( lambda{ |i,f| 
			if i == 1
			elsif i == 2
			end
		})

		Mouse.clear()
		Mouse.use()

		parser = EisenScriptParser.apply(tree())
		@model = Model.new()
		@model.material(BasicMaterial.new())
		@model.material.color.set(0,1,0,1)
		parser.buildModel(@model)

		Main.setModel(@model)
		# Main.model.pose.pos.set(-50,0,0)

	end

	def getModel
		puts @model
		# puts @model.children[0].children[0].material.color #getLeaves().foreach( lambda{|m| puts m.material; puts m.material.color })
	end

	def draw()
		@model.draw()
	end

	#called once per frame from our app
	def animate(dt)
		@t += dt
	end

	def tree
		return "
			set maxdepth 80
			set maxobjects 2000
			//{ h 0.1 sat 0.7 } spiral
			4 * { ry 90 } spiral

			// 8 * { ry 45 h 0.1 sat 0.89 } spiral
			 
			rule spiral w 10 {
				{ s 0.25 1 0.25 } box
				{ y 0.9 rz 10 s 0.94 h 0.01 sat 0.9} spiral
			}
			rule spiral w 10 {
				{ s 0.25 1 0.25 } box
				{ y 0.9 rz 1 s 0.93 h 0.01 sat 0.9} spiral
			}
			 
			rule spiral w 1 {
				spiral
				{ ry 180 h .1} spiral
			}
			rule spiral w 1 {
				{ ry -90 h .1} spiral
				{ ry 90  h -.1} spiral
			}
		"
	end

	def tree2
		return "
			set maxobjects 400
			{ h 30 sat 0.7 } spiral
			{ ry 180 h 30 sat 0.7 } spiral
			 
			rule spiral w 50 {
			box
			{ y 0.4 rx 1 s 0.995 b 0.995 } spiral
			}
			 
			rule spiral w 1 {
			spiral
			{ ry 180 h 3 } spiral
			}
		"
	end
	def test
		return "
			set maxobjects 40
			spiral
			 
			rule spiral {
				box
				{ y 0.4 s 0.995 b 0.99 sat 0.97 h 0.001 } spiral
			}
		"
	end
end

# we return a new instance of this class from this script
# as a handle for our application to reference
EisenScript.new

