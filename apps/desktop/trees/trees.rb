# require 'java'

module M
  include_package "com.fishuyo.seer"
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.io.kinect"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.spatial"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.util"
  include_package "com.fishuyo.seer.examples.trees"
  include_package "com.fishuyo.seer.trees"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end

$Seer = Java::com.fishuyo.seer
# Seer = $Seer
$Vec3 = $Seer.maths.Vec3
$Camera = $Seer.graphics.Camera
$Audio = $Seer.audio.Audio
$OSC = $Seer.io.OSC
$Pad = $Seer.io.Trackpad

$tree = $Seer.examples.trees.Main.tree
tree = $tree

# $tree.root.position.set(0,-0.5,0)
# $tree.root.pin($tree.root.pose.pos)



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


#$Seer.trees.Fabric.gv.set(0.0,-5.0,0.0)
$Seer.trees.Trees.gv.set(0.0,1.0,0.0)


### Trackpad state ###
mx=0.0
my=1.0
mz=1.0 
rz=0.1
rx=0.0
ry=0.0
$Pad.clear()
$Pad.connect()
$Pad.bind( lambda{|i,f|
	t = Main.tree #Seer.examples.trees.Main.tree
	if i == 1
		ur = SceneGraph.root.camera.nav.ur()
		uf = SceneGraph.root.camera.nav.uf()

		# Main.tree.root.applyForce( (v * 10.0 * Main.rms) )
		# t.root.applyForce( Vec3.apply((f[0]-0.5)*1.0*f[4],0,(f[1]-0.5)*f[4]))
		t.root.applyForce( ur*(f[0]-0.5)*2.0*f[4])
		t.root.applyForce( uf*(f[1]-0.5)*-2.0*f[4])

		Shader.apply("composite")
		Shader.shader.get.uniforms.update("u_blend0", f[0])
		Shader.shader.get.uniforms.update("u_blend1", f[1])

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

	    # t.root.accel.zero
	    # t.root.euler.zero
	end
})


move = Vec3.apply(0,0,0)
rot = Vec3.apply(0,0,0)
nmove = Vec3.apply(0,0,0)
nrot = Vec3.apply(0,0,0)

# Kinect.connect()
# Kinect.startDepth()
# Kinect.blob.clear()
Kinect.bind( lambda{|r|
	#if f[2] < 10 || f[3] < 10 then return end

	if r.size == 0
		return
	end

	t = Main.tree
	size = r[0].w*r[0].h/10000.0
	x = (r[0].x - 320)/320.0
	y = (r[0].y - 240)/-240.0
	a = r[0].angle
	if a < 90.0
		a = -a
	else
		a = 180.0 - a
	end

	a = a / 180.0
	i = 0
	# puts a
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
		ur = SceneGraph.root.camera.nav.ur()
		uf = SceneGraph.root.camera.nav.uf()
		t.root.applyForce( ur*(-x*20))
		t.root.applyForce( uf*(y*20))
		Main.volume.lerpTo(Vec3.apply(1,1,1),0.01)

		return
	end

	# puts i
	nm = Main.nmove
	nr = Main.nrot
	if i == 0
		Main.nrot.set(0.0,0.0,0.0)
		Main.nmove.set(a*2,nm.y,nm.z)
		# Main.nmove.set(nm.x, nm.y, (y+0.5)*size+1.0 )
		# Main.nrot.set(x*2.0, nr.y, nr.z)
	elsif i == 1
		Main.nmove.set(x*4.0, (y+0.5)*size+0.8, nm.z )
	elsif i == 2
		Main.nrot.set(nr.x,nr.y, x*4.0)
	elsif i == 4
		rz = f[3]*0.05
		rx = f[2]*0.05
	end

	# move.lerpTo(nmove, 0.05 )
	
	# t.bAngle.y.setMinMax( 0.05, Main.move.x,false )
	# #t.bAngle.y.set(mx)
 #    t.sRatio.setMinMax( 0.05, Main.move.z, false )
 #    #t.sRatio.set( mz )
 #    t.bRatio.setMinMax( 0.05, my, false )
 #    #t.bRatio.set( my )
 #    t.sAngle.x.setMinMax( 0.05, rx, false )
 #    t.bAngle.x.setMinMax( 0.05, rx, false )
 #    #t.sAngle.x.set( rx )
 #    t.sAngle.z.setMinMax( 0.05, rz, false )
 #    t.bAngle.z.setMinMax( 0.05, rz, false )
 #    #t.sAngle.z.set( rz )
 #    #t.branch(depth)
 #    t.refresh()

 #    t.root.accel.zero
 #    t.root.euler.zero
	
})

Trees.setDamp(150.0)

# Kinect.threshold.set( 0.0, 0.4, 0.0 )
# Kinect.device.get().setTiltAngle(0.0)
# Kinect.bgsub.updateBackgroundNextFrame()
# Kinect.bgsub.setThreshold(10.0)

# Kinect.setSizeThreshold( 20 )

def animate(dt)

	# f = com.fishuyo.seer.util.Random.float.apply()
	# # Main.blurDist(f*0.001 + 0.001)
	# Main.blurDist(0)

	t = Main.tree
	# t.root.pose.pos.set(0,-0.5,0)

	if DesktopApp.app.frameCount % 10
		Main.wind.setVolume(Main.volume.x)
	end
	Main.volume.lerpTo(Vec3.apply(0.0,0,0),0.01)

	dawnf = 200
	dayf = 3250
	duskf = 3700
	nightf = 6500

	blue = Vec3.apply(68.0/255.0,122.0/256.0,222.0/256.0)
	white = Vec3.apply(0.1)

	gpose = Pose.apply(Vec3.apply(0,-1.3,0),Quat.apply(0.42112392,-0.09659095, 0.18010217, -0.8836787))

	frame = DesktopApp.app.frameCount % (6500)
	#puts frame
	if frame <= dawnf
		Main.nmove.set(1.3, 1.22, 1.2)
		Main.nrot.set(0.6, 0, 1.6)
		# Main.ground.pose.quat.slerpTo(gpose.quat, 1/250.0)
  #   	Main.ground.pose.pos.lerpTo(gpose.pos,1/250.0)
  #   	Main.ground.scale.lerpTo(Vec3.apply(10.0,10.0,10.0),1/50.0)

	elsif frame <= dayf
		Main.color.lerpTo(blue, 0.01)
		#Main.ground.pose.set(gpose)
		#Main.ground.scale.set(10.0,10.0,10.0)
		Main.day.set(1,0,1)

	elsif frame <= duskf
		if frame <= duskf-100
			Main.color.lerpTo(Vec3.apply(0.7,0,0.3), 0.01)
		else
			Main.color.lerpTo(white, 0.01)
		end
    	# Main.ground.scale.lerpTo(Vec3.apply(-1.0,-1.0,-1.0),1/200.0)

	elsif frame <= nightf
		Main.color.set(white)
		Main.day.set(0,0,0)
		Main.nightUniforms.set(-1,2,1)

	end

	# Main.day.set(0,0,0)

	Shader.setBgColor(RGBA.apply(Main.color,1.0))

	if Main.day.x == false #0.0
		Main.move.lerpTo(Main.nmove, 0.05 )
		Main.rot.lerpTo(Main.nrot, 0.05 )

		t.bAngle.y.setMinMax( 0.0, Main.move.x,false )
		#t.bAngle.y.set(mx)
	    t.sRatio.setMinMax( 0.05, Main.move.z, false )
	    #t.sRatio.set( mz )
	    t.bRatio.setMinMax( 0.05, Main.move.y, false )
	    #t.bRatio.set( my )
	    t.sAngle.x.setMinMax( 0.0, Main.rot.x, false )
	    t.bAngle.x.setMinMax( 0.0, Main.rot.x, false )
	    #t.sAngle.x.set( rx )
	    t.sAngle.z.setMinMax( 0.0, Main.rot.z, false )
	    t.bAngle.z.setMinMax( 0.0, Main.rot.z, false )
	    #t.sAngle.z.set( rz )
	    #t.branch(depth)
	    t.refresh()

	    # t.root.accel.zero
	    # t.root.euler.zero
	end

	height = t.root.getMaxHeight() + 1
	height = 10 if height > 10
	height = 1 if height < 1
	ncamPos = SceneGraph.root.camera.nav.uf()*-height
	SceneGraph.root.camera.nav.pos.lerpTo( ncamPos, 0.005 )
	Main.dayUniforms.set(0.0,10.0,0.0)

	pos = SceneGraph.root.camera.nav.pos + SceneGraph.root.camera.nav.uf()*1.5
	pos += SceneGraph.root.camera.nav.ur()*(0.8)
	pos += SceneGraph.root.camera.nav.uu()*-0.5
	Kinect.cube.pose.pos.lerpTo( pos, 0.1)
	Kinect.cube.pose.quat.slerpTo( SceneGraph.root.camera.nav.quat, 0.1)
	scl = 0.5
	Kinect.cube.scale.set( scl*1.0, scl*480.0/640.0, 0.05 )
	#puts Kinect.cube.pose.pos
	#puts Kinect.cube.pose.quat

	Kinect.cube.pose.pos.set(0.49894238, -0.6203693, 1.2422245)
	Kinect.cube.pose.quat.set(-0.7918381, 0.60365784, -0.08232442, -0.042571533)


	puts ScreenNode.inputs.apply(0).camera.nav.pos


end

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r", lambda{
	Main.rotateWorld(0.004)	
})
Keyboard.bind("t", lambda{
	Main.rotateWorld(0.000)	
})
Keyboard.bind("n", lambda{
	p = Pose.apply(Camera.nav)
	Main.niceViews += p
	Main.niceTrees += [mx,my,mz,rx,ry,rz].to_java 

})
Keyboard.bind("m", lambda{
	for i in 0..Main.niceTrees.length
		puts Main.niceTrees(i)
	end
})
