require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.io.kinect"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.spatial"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.util"
  include_package "com.fishuyo.seer.examples.kinect"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end


Mouse.clear()
Mouse.use()
Mouse.bind("drag", lambda{|i|
  x = i[0]
  y = i[1]
  ray = Camera.ray(x,y)
  t = ray.intersectQuad(Vec3.apply(0,0,1.0), 1.0, 2*480.0 / 640.0 )
  if t.isDefined()
    p = ray.apply(t.get) + Vec3.apply(1.0, 2*480.0/640.0, 0)
    xx = (p.x * 0.5 * 640.0).to_i
    yy = (p.y * (1.0/(2*480.0/640.0)) * 480.0).to_i

    print xx
    print " "
    print yy
    print " "
    if yy >= 480
      yy = yy - 480
      puts Kinect.bgsub.mask.get(480 - yy,xx)[0]

    else
      # puts Kinect.depthData[(640*(480-yy)+xx).to_i]
      puts Kinect.flo[(640*(480-yy)+xx).to_i]

    end
    # puts yy
  else
    
  end
})

Keyboard.clear()
Keyboard.use()
Keyboard.bind("f", lambda{
  Kinect.captureDepthImage()
  Kinect.captureDepthPoints(2000.0)
})

Keyboard.bind("b", lambda{
	# d = Texture.apply(0).getTextureData()
	# Texture.apply(0).load(d)
})


Kinect.connect()
Kinect.startVideo()
Kinect.startDepth()
Kinect.bgsub.updateBackgroundNextFrame()

Kinect.setAngle(10)

def animate(dt)


  Shader.lightingMix_=(0.0)
	Shader.textureMix_=(1.0)

	# for i in 0..9000
	# 	# Texture.apply(0).getTextureData().buffer.put(i,1.0)
	# end

  Kinect.bgsub.setThreshold(10.0)
	# Kinect.threshold.set(0.0,100.4,0.0)
	Texture.apply(0).draw(Kinect.depthPix,0,0)
  # Texture.apply(0).draw(Kinect.videoPix,0,480)
	Texture.apply(0).draw(Kinect.detectPix,0,480)

end
