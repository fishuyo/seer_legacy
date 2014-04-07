require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.spatial"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.util"
  include_package "com.fishuyo.seer.sphere"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end


Keyboard.clear()
Keyboard.use()
Keyboard.bind("n", lambda{
	Scene.push( GLPrimitive.cylinder(Pose[Main.cube.pose], Vec3.apply(1), 0.1, 0.01, 60) )
})



Trackpad.clear()
Trackpad.connect()

dx=0.0
dy=-5.5
dz=0.0
#[0.032167766 -0.028131885 -0.01904115]
rx=0.032167
ry=-0.0281318
rz=-0.019
Trackpad.bind( lambda{ |i,f| 

	if i == 6

	elsif i == 5
		#rx += f[3]*-0.001
	elsif i == 4
		#ry += f[2]*0.001
		#rz += f[3]*-0.001
	elsif i == 3
		#dx += f[2]*0.01
		#dy += f[3]*-0.01
	elsif i == 2
		#dx += f[2]*0.01
		#dz += f[3]*0.01
		down =false
	else
		down = false
	end

	# v = Vec3.new(rx,ry,rz)
	# Main.cams.pose.quat.fromEuler(v)

	# p = Main.spherePose.pos
	# Main.spherePose.pos.set(dx,dy,dz)

	# puts dy	
	# puts Main.cams.pose.quat.toEulerVec()
})



def animate(dt)

	Shader.setBlend(false)

	# puts "diff z"
	# puts Main.cameras[1].pose.pos - Main.cameras[8].pose.pos
	# puts Main.cameras[5].pose.pos - Main.cameras[12].pose.pos
	# puts "diff x"
	# puts Main.cameras[1].pose.pos - Main.cameras[5].pose.pos
	# puts Main.cameras[8].pose.pos - Main.cameras[12].pose.pos

	# puts "ur dot ur"
	# puts Main.cameras[1].pose.ur().dot( Main.spherePose.ur() )
	# puts Main.cameras[5].pose.ur().dot( Main.spherePose.ur() )
	# puts Main.cameras[8].pose.ur().dot( Main.spherePose.ur() )
	# puts Main.cameras[12].pose.ur().dot( Main.spherePose.ur() )
	# Main.sphereScale.set(10.0,10.0,10.0)
	#puts Main.cameras[0].pose.pos.x
end
