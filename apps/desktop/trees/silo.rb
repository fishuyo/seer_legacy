
module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.spatial"
  include_package "com.fishuyo.seer.util"
  include_package "com.fishuyo.seer.examples.silo"
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


$tree = Main.tree

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
# $Seer.trees.Trees.gv.set(0.0,1.0,0.0)


### Trackpad state ###
move = 0
mx=0.0
my=0.0
mz=0.0 
rz=0.0
rx=0.0
ry=0.0
mx1=0.0
my1=0.0
mz1=1.0 
Trackpad.clear()
Trackpad.connect()
Trackpad.bind( lambda{|i,f|
	t = Main.tree

	if move == 1
		if i == 2
			mx1 = mx1 + f[2]*0.05  
			my1 = my1 + f[3]*0.05
		elsif i == 3
			mz1 = mz1 + f[3]*0.05
		elsif i == 4

		end
		Main.ground.pose.pos.set(mx1,my1,0)
		Main.ground.scale.set(mz1)
		return
	end

	if i == 1
		ur = Vec3.new(1,0,0) #Camera.nav.ur()
		uf = Vec3.new(0,0,1) #Camera.nav.uf()

		t.root.applyForce( ur*(f[0]-0.5)*2.0*f[4])
		t.root.applyForce( uf*(f[1]-0.5)*-2.0*f[4])
	elsif i == 2
		mx = mx + f[2]*0.05  
		my = my + f[3]*0.05
	elsif i == 3
		ry = ry + f[2]*0.05  
		mz = mz + f[3]*0.01
		mz = 0.08 if mz < 0.08
		mz = 3.0 if mz > 3.0
	elsif i == 4
		rz = rz + f[3]*0.05
		rx = rx + f[2]*0.05
	end

    t.root.pose.pos.set(mx,my,0)

	if i > 2
		t.bAngle.y.setMinMax( 0.05, ry,false )
		##t.bAngle.y.set(mx)
	    t.sRatio.setMinMax( 0.05, mz, false )
	    ## t.sRatio.set( mz )
	    t.bRatio.setMinMax( 0.05, mz, false )
	    ##t.bRatio.set( my )
	    t.sAngle.x.setMinMax( 0.05, rx, false )
	    t.bAngle.x.setMinMax( 0.05, rx, false )
	    ##t.sAngle.x.set( rx )
	    t.sAngle.z.setMinMax( 0.05, rz, false )
	    t.bAngle.z.setMinMax( 0.05, rz, false )
	    ##t.sAngle.z.set( rz )
	    ##t.branch(depth)
	    t.refresh()

	    # t.root.accel.zero
	    # t.root.euler.zero
	end
})


move = Vec3.apply(0,0,0)
rot = Vec3.apply(0,0,0)
nmove = Vec3.apply(0,0,0)
nrot = Vec3.apply(0,0,0)

Trees.setDamp(150.0)

def animate(dt)
	t = Main.tree

	dawnf = 200
	dayf = 3250
	duskf = 3700
	nightf = 6500

	blue = Vec3.apply(68.0/255.0,122.0/256.0,222.0/256.0)
	white = Vec3.apply(1)

	gpose = Pose.apply(Vec3.apply(0,-1.3,0),Quat.apply(0.42112392,-0.09659095, 0.18010217, -0.8836787))

	frame = DesktopApp.app.frameCount % (6500)
	#puts frame
	# if frame <= dawnf
	# 	Main.nmove.set(1.3, 1.22, 1.2)
	# 	Main.nrot.set(0.6, 0, 1.6)

	# elsif frame <= dayf
	# 	Main.color.lerpTo(blue, 0.01)

	# elsif frame <= duskf
	# 	if frame <= duskf-100
	# 		Main.color.lerpTo(Vec3.apply(0.7,0,0.3), 0.01)
	# 	else
	# 		Main.color.lerpTo(white, 0.01)
	# 	end
 #    	# Main.ground.scale.lerpTo(Vec3.apply(-1.0,-1.0,-1.0),1/200.0)

	# elsif frame <= nightf
	# 	Main.color.set(white)
	# 	Main.day.set(0,0,0)
	# 	Main.nightUniforms.set(-1,2,1)

	# end

	# Shader.lightingß_=(Main.rms*4) #0.0)
	Shader.setBgColor(RGBA.apply(1,1,1,1.0))

	Main.ground.color.set(0,0,0,1)
	TreeNode.model.color.set(0,0,0,1)

	# dist = Camera.nav.pos - Main.ground.pose.pos
	# if dist.mag() > 1.1*Main.ground.scale.x 
	# 	Camera.nav.pos.lerpTo(Main.ground.pose.pos, 0.05)
	# elsif dist.mag() < 1.1
	# 	Camera.nav.pos.lerpTo(Main.ground.pose.pos + dist.normalize()*1.1*Main.ground.scale.x, 0.05)
	# end
	# quat = Quat.apply().getRotationTo(Camera.nav.quat.toY(), dist.normalize() )
	# Camera.nav.quat.set(quat*Camera.nav.quat)

	# Main.ground.rotate(0,0,0.001)

	# if Main.day.x == 0.0
	# 	Main.move.lerpTo(Main.nmove, 0.05 )
	# 	Main.rot.lerpTo(Main.nrot, 0.05 )

	# 	t.bAngle.y.setMinMax( 0.0, Main.move.x,false )
	# 	#t.bAngle.y.set(mx)
	#     t.sRatio.setMinMax( 0.05, Main.move.z, false )
	#     #t.sRatio.set( mz )
	#     t.bRatio.setMinMax( 0.05, Main.move.y, false )
	#     #t.bRatio.set( my )
	#     t.sAngle.x.setMinMax( 0.0, Main.rot.x, false )
	#     t.bAngle.x.setMinMax( 0.0, Main.rot.x, false )
	#     #t.sAngle.x.set( rx )
	#     t.sAngle.z.setMinMax( 0.0, Main.rot.z, false )
	#     t.bAngle.z.setMinMax( 0.0, Main.rot.z, false )
	#     #t.sAngle.z.set( rz )
	#     #t.branch(depth)
	#     t.refresh()

	#     # t.root.accel.zero
	#     # t.root.euler.zero
	# end

	# height = t.root.getMaxHeight() + 1
	# height = 10 if height > 10
	# height = 1 if height < 1
	# ncamPos = Camera.nav.uf*-height
	# #Camera.nav.pos.lerpTo( ncamPos, 0.005 )
	# Main.dayUniforms.set(0.0,10.0,0.0)

	# pos = Camera.nav.pos + Camera.nav.uf()*1.5
	# pos += Camera.nav.ur()*(0.8)
	# pos += Camera.nav.uu()*-0.5

end

Keyboard.clear()
Keyboard.use()
Keyboard.bind("m", lambda{ 
	move = 1
})
Keyboard.bind("t", lambda{
	move = 0
})
