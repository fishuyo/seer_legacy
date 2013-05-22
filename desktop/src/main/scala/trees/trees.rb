require 'java'

module M
  include_package "scala"
  include_package "com.fishuyo"
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.util"
  include_package "com.fishuyo.examples.trees"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end

$Seer = Java::com.fishuyo
Seer = $Seer
$Vec3 = $Seer.maths.Vec3
$Camera = $Seer.graphics.Camera
$Audio = $Seer.audio.Audio
$OSC = $Seer.io.OSC
$Pad = $Seer.io.Trackpad

$tree = $Seer.examples.trees.Main.tree
tree = $tree

$tree.root.pose.pos.set(0,-0.5,0)
$tree.root.pin($tree.root.pose.pos)



def tree0
	$tree.branchNum.setChoices([0,1,2].to_java :int)
	$tree.branchNum.setProb([0.0,1.0,0.0].to_java :float)
	$tree.sLength.setMinMax(0.5,0.5,false)

	$tree.bAngle.z.setMinMax(0.0,0.0,false)
	$tree.bAngle.y.setMinMax(0.0,1.2,true)
	$tree.bAngle.x.setMinMax(0.0,0.0,false)

	$tree.sAngle.z.setMinMax(1.0,1.0,false)
	$tree.sAngle.y.setMinMax(0.0,0.0,false)
	$tree.sAngle.x.setMinMax(0.0,0.0,false)
end


depth = 10

tree0()

$tree.setAnimate(true)
$tree.setReseed(true)
$tree.setDepth(depth)
$tree.branch(depth)

$Camera.rotateWorld(0.0)

$Seer.trees.Fabric.gv.set(0.0,-10.0,0.0)
$Seer.trees.Trees.gv.set(0.0,1.0,0.0)


### Trackpad state ###
mx=0.0
my=0.0
mz=0.0 
rz=0.0
rx=0.0
$Pad.clear()
$Pad.connect()
$Pad.bind( lambda{|i,f|
	t = tree #Seer.examples.trees.Main.tree
	if i == 1
		ur = Camera.nav.ur()
		uf = Camera.nav.uf()

		# Main.tree.root.applyForce( (v * 10.0 * Main.rms) )
		# t.root.applyForce( Vec3.apply((f[0]-0.5)*1.0*f[4],0,(f[1]-0.5)*f[4]))
		t.root.applyForce( ur*(f[0]-0.5)*2.0*f[4])
		t.root.applyForce( uf*(f[1]-0.5)*-2.0*f[4])

	elsif i == 2
		mx = mx + f[2]*0.05  
		mz = mz + f[3]*-0.05
	elsif i == 3
		mx = mx + f[2]*0.05  
		my = my + f[3]*0.05
	elsif i == 4
		rz = rz + f[3]*0.05
		rx = rx + f[2]*0.05
	end

	if i > 1
		t.bAngle.y.setMinMax( 0.05, mx,false )
		#t.bAngle.y.set(mx)
	    t.sRatio.setMinMax( 0.05, mz, false )
	    #t.sRatio.set( mz )
	    t.bRatio.setMinMax( 0.05, my, false )
	    #t.bRatio.set( my )
	    t.sAngle.x.setMinMax( 0.05, rx, false )
	    t.bAngle.x.setMinMax( 0.05, rx, false )
	    #t.sAngle.x.set( rx )
	    t.sAngle.z.setMinMax( 0.05, rz, false )
	    t.bAngle.z.setMinMax( 0.05, rz, false )
	    #t.sAngle.z.set( rz )
	    #t.branch(depth)
	    t.refresh()

	    t.root.accel.zero
	    t.root.euler.zero
	end
})

Kinect.connect()
Kinect.startDepth()
Kinect.clear()
Kinect.bind( lambda{|i,f|
	if f[2] < 10 || f[3] < 10 then return end

	t = Main.tree
	size = f[2]*f[3]/10000.0
	x = (f[0] - 320)/320.0
	y = (f[1] - 240)/-240.0

	# print i
	# print " "
	# print x
	# print " "
	# print y
	# print " "	
	# # print f[2]
	# # print " "
	# # puts f[3]
	# puts size

	if Main.day.x == 1.0
		ur = Camera.nav.ur()
		uf = Camera.nav.uf()
		t.root.applyForce( ur*(-x*size))
		t.root.applyForce( uf*(y*size))
		Main.volume.lerpTo(Vec3.apply(1,1,1),0.01)

		return
	end

	if i == 1

	elsif i == 2
		mx = mx + f[2]*0.05  
		mz = mz + f[3]*-0.05
	elsif i == 3
		mx = mx + f[2]*0.05  
		my = my + f[3]*0.05
	elsif i == 4
		rz = rz + f[3]*0.05
		rx = rx + f[2]*0.05
	end

	if i > 1
		t.bAngle.y.setMinMax( 0.05, mx,false )
		#t.bAngle.y.set(mx)
	    t.sRatio.setMinMax( 0.05, mz, false )
	    #t.sRatio.set( mz )
	    t.bRatio.setMinMax( 0.05, my, false )
	    #t.bRatio.set( my )
	    t.sAngle.x.setMinMax( 0.05, rx, false )
	    t.bAngle.x.setMinMax( 0.05, rx, false )
	    #t.sAngle.x.set( rx )
	    t.sAngle.z.setMinMax( 0.05, rz, false )
	    t.bAngle.z.setMinMax( 0.05, rz, false )
	    #t.sAngle.z.set( rz )
	    #t.branch(depth)
	    t.refresh()

	    t.root.accel.zero
	    t.root.euler.zero
	end
})



def step(dt)

	puts Main.volume.x
	if SimpleAppRun.app.frameCount % 10
		Main.wind.setVolume(Main.volume.x)
	end
	Main.volume.lerpTo(Vec3.apply(0,0,0),0.01)


	Kinect.setThreshold( 0.3 )
	dawnf = 250
	dayf = 500
	duskf = 800
	nightf = 1000

	blue = Vec3.apply(68.0/255.0,122.0/256.0,222.0/256.0)
	white = Vec3.apply(1)

	gpose = Pose.apply(Vec3.apply(0,-1.3,0),Quat.apply(0.42112392,-0.09659095, 0.18010217, -0.8836787))

	frame = 0#SimpleAppRun.app.frameCount % (40*25)
	#puts frame
	if frame <= dawnf
		Main.ground.pose.quat.slerpTo(gpose.quat, 1/250.0)
    	Main.ground.pose.pos.lerpTo(gpose.pos,1/250.0)
    	Main.ground.scale.lerpTo(Vec3.apply(10.0,10.0,10.0),1/250.0)

	elsif frame <= dayf
		Main.color.set(blue)
		Main.ground.pose.set(gpose)
		Main.ground.scale.set(10.0,10.0,10.0)
		Main.day.set(1,0,0)

	elsif frame <= duskf
		Main.color.lerpTo(white, 1/300.0)
    	Main.ground.scale.lerpTo(Vec3.apply(0.0,0.0,0.0),1/300.0)

	elsif frame <= nightf
		Main.day.set(0,0,0)
		Main.uniforms.set(-1,2,1)

	end

	Shader.setBgColor(Main.color,1.0)


	pos = Camera.nav.pos + Camera.nav.uf()*1.5
	pos += Camera.nav.ur()*(1.0)
	pos += Camera.nav.uu()*0.5
	Kinect.cube.pose.pos.lerpTo( pos, 0.1)
	Kinect.cube.pose.quat.slerpTo( Camera.nav.quat, 0.1)

	v = Vec3.apply(Camera.nav.pos)
	v.set(v.z,0,v.x)
	# Main.tree.root.applyForce( (v * 10.0 * Main.rms) )
	# puts Main.rms
	#Shader.setBgColor( Vec3.apply(1.0,1.0,1.0), 1.0)
	#Shader.setBgColor( Vec3.apply(68.0/255.0,122.0/256.0,222.0/256.0),1.0)

	#puts Main.tree.root.children[0].euler

	# Main.cube.scale.set(0.1,0.1,0.1)
	# Main.cube.pose.pos.set(Main.tree.root.pose.pos)

	wx=0.0
	wy=0.0
	wz=0.0
	ur = Camera.nav.ur()
	uf = Camera.nav.uf()
	# t.root.applyForce( ur*(wx-0.5)*2.0*wv)
	# t.root.applyForce( uf*(sy-0.5)*-2.0*wv)

end