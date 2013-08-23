require 'java'

module M
  include_package "scala.math"
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.io.leap"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.audio"
  include_package "com.fishuyo.util"
  include_package "com.fishuyo.test"
  include_package "com.fishuyo.parsers"
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

Leap.clear()
Leap.connect()
Leap.bind( lambda{ |frame|
	return if frame.hands().isEmpty()
	hand = frame.hands().get(0)
	normal = hand.palmNormal()
	dir = hand.direction()

	quat = Quat.apply(1,0,0,0).fromEuler(Vec3.apply( dir.pitch(), -dir.yaw(), normal.roll() ))
	Main.cube.pose.quat.set(quat)
})

Keyboard.bind("m", lambda{
	Main.modelBuilder_=( Some[EisenScriptParser.apply("
			set maxdepth 50
			r0
			var scale = 0.99
			var rotx = 1.57
			var rotz = 6.0

			rule r0 {
			  3 * { rz 120  } R1
			  3 * { rz 120 } R2
			}

			rule R1 {
			  { x 1.3 rx rotx rz 6 ry 3 s scale } R1
			  { s 1 } sphere
			}

			rule R2 {
			  { x -1.3 rz rotz ry 3 s scale } R2
			  { s 1 }  sphere
			}
		")])
	Main.model_=( Some[ Main.modelBuilder.get().buildModel() ])
	Main.model.get().pose.set(Main.cube.pose)
	Main.model.get().scale.set(0.1,0.1,0.1)
})
Keyboard.bind("h", lambda{
	Main.modelBuilder_=( Some[EisenScriptParser.apply("
			set maxdepth 4
			var gap = 0.5
			var roty = 0.0
			tree
			rule tree {
			   { x gap s 0.49 } tree
			   { x gap y 0 s 0.49 rz 90  } tree
			   { x gap y 0 s 0.49 rz -90 } tree
			   { x gap y 0 s 0.49 ry -90 } tree
			   { x gap y 0 s 0.49 ry 90  } tree
			plus
			}

			rule plus {
			   { x 0.25 s 0.25 1 1 rz roty } d
			}

			rule d {
			   { s 0.05 5 0.05 } box
			}
		")])

	Main.model_=( Some[ Main.modelBuilder.get().buildModel() ])
	Main.model.get().pose.set(Main.cube.pose)
	Main.model.get().scale.set(0.1,0.1,0.1)
})
Keyboard.bind("i", lambda{
	Main.modelBuilder_=( Some[EisenScriptParser.apply("
		set maxdepth 10
		var roty = 0.0
		R1
		rule R1 {
			{ y 0.842 x 0.5915 s 0.5 rz -60 ry -25 }R1
			{ y 2.092 x -0.3415 s 0.866 rz 30 ry roty }R1
			{ ry 20 s 0.25 1.0 0.25 }box
		}
		")])

	Main.model_=( Some[ Main.modelBuilder.get().buildModel() ])
	Main.model.get().pose.set(Main.cube.pose)
	Main.model.get().scale.set(0.1,0.1,0.1)
})
Keyboard.bind("j", lambda{
	Main.modelBuilder_=( Some[EisenScriptParser.apply("
		set maxdepth 10
		var roty = 0.0
		branch

		rule branch md 20 w 3
		{
		    {s 0.95 y 1  hue 20 ry 90} branch
		    box
		}

		rule branch md 12
		{
		    {s 0.85 y 1 rz 25 hue 20} branch
		    {s 0.85 y 1 rz -25  hue 20} branch
		    box
		}
		")])

	Main.model_=( Some[ Main.modelBuilder.get().buildModel() ])
	Main.model.get().pose.set(Main.cube.pose)
	Main.model.get().scale.set(0.1,0.1,0.1)
})
Keyboard.bind("b", lambda{
	# d = Texture.apply(0).getTextureData()
	# Texture.apply(0).load(d)
})

$Sine = Sine.new(440.0,1.0 )

Trackpad.clear()
Trackpad.connect()

dx=0.0
dy=0.0
dz=0.0
ry=0.0

Trackpad.bind( lambda{ |i,f| 

	if i == 1

	elsif i == 3
		dx += f[2]*0.01
		dy += f[3]*0.01
		Main.cube.pose.pos.set(dx,dy,dz)

	elsif i == 2
		dx += f[2]*0.01
		dz += f[3]*-0.01
		Main.cube.pose.pos.set(dx,dy,dz)
		# Main.pix.setColor(1,0,0.3,0.05)
		# Main.pix.fillCircle(dz,dx,f[4]*4)

		down =false
	elsif i == 4
		ry += f[3]*-0.1
		$Sine.a(ry)
	else
		down = false
	end
})

# Kinect.connect()
# Kinect.startVideo()
# Kinect.startDepth()

def draw()
	Main.model.get().draw()
end


def step(dt)
	Main.cube.scale.set(0.1)
	Main.cube.color.set(RGBA.new(1,0,0,0.2))
	
	r = Randf.apply(-0.001,0.001,false)
	$Sine.a( $Sine.amp+r[] )
	v = $Sine.amp #scala.math.sin(dt)
	rotx = 1.57 + v*1.0
	roty = v
	rotz = v*10.0
	Main.modelBuilder.get().set("scale",0.9+v/100)
	Main.modelBuilder.get().set("rotx",rotx)
	Main.modelBuilder.get().set("roty",roty)
	Main.modelBuilder.get().set("rotz",rotz)
	m = Main.model.get()
	p = m.pose
	s = m.scale
	Main.model_=( Some[ Main.modelBuilder.get().buildModel() ])
	Main.model.get().pose.set(p)
	Main.model.get().scale.set(s)


	frame = SimpleAppRun.app.frameCount % (600)
	#puts frame
	if frame == 0
		r = Randf.apply(-2.0,2.0,false)
		m = Model.new(Pose.apply(Camera.nav), Vec3.apply(0.1,0.1,0.1))
		m.add( Sphere.asLines() )
		GLScene.push( m )
		for i in 0..30
			p = Pose.apply()
			p.pos.set(0,1,0)
			s = Vec3.apply(r.apply,0.9,0.9)
			m = m.transform(p,s)
			m.add( Sphere.asLines() )
		end
	end
	

	for i in 0..9000
		# Texture.apply(0).getTextureData().buffer.put(i,1.0)
	end

	# Texture.apply(0).draw(Kinect.depthPix,0,0)
	# Texture.apply(0).draw(Kinect.videoPix,0,480)


	z = Randf.apply(-0.5,0.5,false)

	d = GLScene.drawable
	
	if d.size > 1000
		d.remove(999)

	end

	for i in 2 .. d.size
		#d.remove(i)
	# 	o = d[i]
	# 	s = o.scale.x + r.apply()
	# 	o.scale.set(s,s,s)
	end
end

# TransTrack.clear()
# TransTrack.bind("point", lambda{|i,f|
# 	o = i# - 655361
	
# })
# TransTrack.bind("rigid_body", lambda{|i,f|
# 	# puts i
# 	if i == 3
# 		Main.model.get().pose.pos.lerpTo(Vec3.new(f[0],f[1],f[2]),0.05)
# 		q = Quat.new(f[6],f[3],f[4],f[5]) * Quat.apply(0,0,0,0).fromEuler(Vec3.apply(1.5,0,0))
# 		Main.model.get().pose.quat.slerpTo(q,0.025)
# 	end


# 	if i==2
# 		#$Sine.a(f[6]*10.0)
# 		Main.model.get().scale.lerpTo(Vec3.new[f[1],f[1],f[1]]) 
# 	end
# 	if i==3 
# 	end

# 	if i == 5
# 		Camera.nav.pos.lerpTo(Vec3.new(f[0],f[1],f[2]),0.05)
# 		Camera.nav.quat.slerpTo(Quat.new(f[6],f[3],f[4],f[5]),0.05)
# 	end

# })
# TransTrack.setDebug(false)
# TransTrack.start()

Touch.clear()
Touch.use()
Touch.bind("pan", lambda {|i,f|
	puts i
	puts f[0]
	puts f[1]
})
