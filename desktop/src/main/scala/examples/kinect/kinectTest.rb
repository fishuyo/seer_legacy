require 'java'

module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.util"
  include_package "com.fishuyo.test"
  include_package "com.fishuyo.parsers.eisenscript"
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
	r = Randf.apply(-2.0,2.0,false)
	m = Model.new(Pose.apply(Main.cube.pose), Vec3.apply(0.1,0.1,0.1))
	m.add( Sphere.asLines() )
	GLScene.push( m )
	for i in 0..30
		p = Pose.apply()
		p.pos.set(0,1,0)
		s = Vec3.apply(r.apply,0.9,0.9)
		m = m.transform(p,s)
		m.add( Sphere.asLines() )
	end
})
Keyboard.bind("m", lambda{
	m = EisenScriptParser.apply("
			set maxdepth 50
			r0

			rule r0 {
			  3 * { rz 120  } R1
			  3 * { rz 120 } R2
			}

			rule R1 {
			  { x 1.3 rx 1.57 rz 6 ry 3 s 0.99 } R1
			  { s 1 } grid
			}

			rule R2 {
			  { x -1.3 rz 6 ry 3 s 0.99 } R2
			  { s 1 }  grid
			}
		")
	# m = EisenScriptParser.apply("
	# 		set maxdepth 50
	# 		R1
	# 		rule R1 {
	# 		  { x 0.9 rz 6 ry 6 s 0.99 } R1
	# 		  { s 0.5 } R2
	# 		}
	# 		rule R2 {
	# 			{ry 45 y 2 s 0.9} R1
	# 			grid
	# 		}
	# 	")
	m.pose.set(Main.cube.pose)
	m.scale.set(0.1,0.1,0.1)
	# sputs m
	GLScene.push( m )
})
Keyboard.bind("b", lambda{
	# d = Texture.apply(0).getTextureData()
	# Texture.apply(0).load(d)
})



Trackpad.clear()
Trackpad.connect()

dx=0.0
dy=0.0
dz=0.0
Trackpad.bind( lambda{ |i,f| 

	if i == 1

	elsif i == 3
		dx += f[2]*0.01
		dy += f[3]*0.01
		#Main.cube.pose.pos.set(dx,dy,dz)

	elsif i == 2
		dx += f[2]*0.01
		dz += f[3]*-0.01
		#Main.cube.pose.pos.set(dx,dy,dz)
		# Main.pix.setColor(1,0,0.3,0.05)
		# Main.pix.fillCircle(dz,dx,f[4]*4)

		down =false
	else
		down = false
	end
})

Kinect.connect()
#Kinect.startVideo()
Kinect.startDepth()
Kinect.setBGImage()


def step(dt)

	for i in 0..9000
		# Texture.apply(0).getTextureData().buffer.put(i,1.0)
	end

	Kinect.threshold.set(0.0,0.4,0.0)
	Texture.apply(0).draw(Kinect.depthPix,0,0)
	Texture.apply(0).draw(Kinect.videoPix,0,480)


	z = Randf.apply(-0.5,0.5,false)

	r = Randf.apply(-0.01,0.01,false)
	d = GLScene.drawable
	
	# return if d.size < 10

	for i in 2 .. d.size
		#d.remove(i)
	# 	o = d[i]
	# 	s = o.scale.x + r.apply()
	# 	o.scale.set(s,s,s)
	end
end
