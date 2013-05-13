require 'java'

module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.util"
  include_package "com.fishuyo.test"
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
	GLScene.push( GLPrimitive.cube(Pose[Main.cube.pose], Vec3.apply(1) ) )
})



Trackpad.clear()
Trackpad.connect()

dx=0.0
dy=0.0
dz=0.0
Trackpad.bind( lambda{ |i,f| 

	if i == 3
		dx += f[2]*0.01
		dy += f[3]*-0.01
		Main.cube.pose.pos.set(dx,dy,dz)

	elsif i == 2
		dx += f[2]*0.01
		dz += f[3]*0.01
		Main.cube.pose.pos.set(dx,dy,dz)

		down =false
	else
		down = false
	end
})



def step(dt)

	z = Randf.apply(-0.5,0.5,false)

	r = Randf.apply(-0.01,0.01,false)
	d = GLScene.drawable
	
	return if d.size < 10

	for i in 10 .. d.size
		o = d[i]
		s = o.scale.x + r.apply()
		o.scale.set(s,s,s)
	end
end
