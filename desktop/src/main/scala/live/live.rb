require 'java'
Seer = Java::com.fishuyo
Vec3 = Seer.maths.Vec3
Camera = Seer.graphics.Camera
Audio = Seer.audio.Audio
OSC = Seer.io.OSC

Tree = Seer.examples.trees.Main.tree

Tree.root.pose.pos.set(0,-0.5,0)
Tree.root.pin(Tree.root.pose.pos)

def tree0
	Tree.branchNum.setChoices([0,1,2].to_java :int)
	Tree.branchNum.setProb([0.0,0.0,1.0].to_java :float)
	Tree.sLength.setMinMax(0.5,0.5,false)

	Tree.bAngle.z.setMinMax(0.0,0.0,false)
	Tree.bAngle.y.setMinMax(0.0,1.2,true)
	Tree.bAngle.x.setMinMax(0.0,0.0,false)

	Tree.sAngle.z.setMinMax(1.0,1.0,false)
	Tree.sAngle.y.setMinMax(0.0,0.0,false)
	Tree.sAngle.x.setMinMax(0.0,0.0,false)
end
def tree1
	Tree.branchNum.setChoices([0,1,2].to_java :int)
	Tree.branchNum.setProb([0.0,0.0,1.0].to_java :float)
	Tree.sLength.setMinMax(0.5,0.5,false)

	Tree.bAngle.z.setMinMax(0.1,0.1,false)
	Tree.bAngle.y.setMinMax(-1.0,1.0,false)
	Tree.bAngle.x.setMinMax(0.0,0.0,false)

	Tree.sAngle.z.setMinMax(-2.0,2.0,false)
	Tree.sAngle.y.setMinMax(-0.2,0.2,false)
	Tree.sAngle.x.setMinMax(-0.2,0.2,false)
end
def tree2
	Tree.branchNum.setChoices([0,1,2].to_java :int)
	Tree.branchNum.setProb([0.5,0.25,0.25].to_java :float)
	Tree.sLength.setMinMax(0.5,0.5,false)

	Tree.bAngle.z.setMinMax(0.1,1.0,false)
	Tree.bAngle.y.setMinMax(0.1,1.0,false)
	Tree.bAngle.x.setMinMax(0.0,0.2,false)

	Tree.sAngle.z.setMinMax(0.7,0.1,false)
	Tree.sAngle.y.setMinMax(-0.2,0.2,false)
	Tree.sAngle.x.setMinMax(-0.2,0.2,false)
end


depth = 6

tree1()

Tree.setAnimate(false)
Tree.setReseed(true)
Tree.setDepth(depth)
Tree.branch(depth)


#Audio.sources.apply(0).f(40.0)
#Audio.sources.apply(1).f(41.0)

Camera.rotateWorld(0.0)

OSC.bind("/1/fader1", Tree.set(Tree.sLength,2.0))

OSC.bind("/2/fader1", Tree.setMin(Tree.sRatio,2.0))
OSC.bind("/2/fader2", Tree.setMax(Tree.sRatio,2.0))
OSC.bind("/2/fader3", Tree.setMin(Tree.sAngle.z,10.0))
OSC.bind("/2/fader4", Tree.setMax(Tree.sAngle.z,10.0))
OSC.bind("/2/fader5", Tree.setMin(Tree.sAngle.y,1.0))
OSC.bind("/2/fader6", Tree.setMax(Tree.sAngle.y,1.0))
OSC.bind("/2/fader7", Tree.setMin(Tree.sAngle.x,1.0))
OSC.bind("/2/fader8", Tree.setMax(Tree.sAngle.x,1.0))

OSC.bind("/3/fader1", Tree.setMin(Tree.bRatio,2.0))
OSC.bind("/3/fader2", Tree.setMax(Tree.bRatio,2.0))
OSC.bind("/3/fader3", Tree.setMin(Tree.bAngle.z,10.0))
OSC.bind("/3/fader4", Tree.setMax(Tree.bAngle.z,10.0))
OSC.bind("/3/fader5", Tree.setMin(Tree.bAngle.y,1.0))
OSC.bind("/3/fader6", Tree.setMax(Tree.bAngle.y,1.0))
OSC.bind("/3/fader7", Tree.setMin(Tree.bAngle.x,1.0))
OSC.bind("/3/fader8", Tree.setMax(Tree.bAngle.x,1.0))

OSC.bind("/2/multifader2/1", Tree.setMin(Tree.sRatio,2.0))
OSC.bind("/2/multifader2/2", Tree.setMax(Tree.sRatio,2.0))
OSC.bind("/2/multifader2/3", Tree.setMin(Tree.sAngle.z,10.0))
OSC.bind("/2/multifader2/4", Tree.setMax(Tree.sAngle.z,10.0))
OSC.bind("/2/multifader2/5", Tree.setMin(Tree.sAngle.y,1.0))
OSC.bind("/2/multifader2/6", Tree.setMax(Tree.sAngle.y,1.0))
OSC.bind("/2/multifader2/7", Tree.setMin(Tree.sAngle.x,1.0))
OSC.bind("/2/multifader2/8", Tree.setMax(Tree.sAngle.x,1.0))

OSC.bind("/2/multifader3/1", Tree.setMin(Tree.bRatio,2.0))
OSC.bind("/2/multifader3/2", Tree.setMax(Tree.bRatio,2.0))
OSC.bind("/2/multifader3/3", Tree.setMin(Tree.bAngle.z,10.0))
OSC.bind("/2/multifader3/4", Tree.setMax(Tree.bAngle.z,10.0))
OSC.bind("/2/multifader3/5", Tree.setMin(Tree.bAngle.y,1.0))
OSC.bind("/2/multifader3/6", Tree.setMax(Tree.bAngle.y,1.0))
OSC.bind("/2/multifader3/7", Tree.setMin(Tree.bAngle.x,1.0))
OSC.bind("/2/multifader3/8", Tree.setMax(Tree.bAngle.x,1.0))

OSC.bind("/1/fader4", Tree.setSeed() )
#seer.graphics.Shader.apply(1)


Seer.trees.Fabric.gv.set(0.0,-10.0,0.0)
Seer.trees.Trees.gv.set(0.0,1.0,0.0)

def hi( v1 )
 print v1
end

OSC.bind("/hm", lambda{|f| print f[0] })


#seer.io.Inputs.removeProcessor( 1 )















def size
	return 200
end
def init
	print 'hello'
end
def data( field )
end