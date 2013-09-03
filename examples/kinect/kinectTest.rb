require 'java'

module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.io.kinect"
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

})

Keyboard.bind("b", lambda{
	# d = Texture.apply(0).getTextureData()
	# Texture.apply(0).load(d)
})


Kinect.connect()
Kinect.startVideo()
Kinect.startDepth()
Kinect.bgsub.updateBackgroundNextFrame()

Kinect.setAngle(-10)

def step(dt)

	Shader.texture_=(1.0)

	# for i in 0..9000
	# 	# Texture.apply(0).getTextureData().buffer.put(i,1.0)
	# end

	Kinect.threshold.set(0.0,100.4,0.0)
	Texture.apply(0).draw(Kinect.depthPix,0,0)
	Texture.apply(0).draw(Kinect.videoPix,0,480)

end
