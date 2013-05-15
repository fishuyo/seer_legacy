require 'java'

module M
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
		t.root.applyForce( Vec3.apply((f[0]-0.5)*100.0*f[4],0,(f[1]-0.5)*f[4]))
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
	end
})


def step(dt)
	#puts Main.tree.root.children[0].euler

	Main.cube.scale.set(0.1,0.1,0.1)
	Main.cube.pose.pos.set(Main.tree.root.pose.pos)

end