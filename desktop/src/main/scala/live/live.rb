require 'java'

Java::com.fishuyo.examples.trees.Main.tree.pos.set(0,-0.5,-1)

Java::com.fishuyo.examples.trees.Main.tree.branch( 8, 60.0, 0.89, 0)

Java::com.fishuyo.audio.Audio.sources.apply(0).f(40.0)
Java::com.fishuyo.audio.Audio.sources.apply(1).f(41.0)

def size
	return 200
end

def init
	print 'hello'
end

def data( field )



end