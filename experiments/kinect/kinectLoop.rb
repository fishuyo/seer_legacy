require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.io.kinect"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.graphics"
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

$Loop = com.fishuyo.seer.examples.kinect.Loop

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r",lambda{ $Loop.loop.toggleRecord() })
Keyboard.bind("c",lambda{ $Loop.loop.stop(); $Loop.loop.clear() })
Keyboard.bind("x",lambda{ $Loop.loop.stack() })
Keyboard.bind("t",lambda{ $Loop.loop.togglePlay() })
Keyboard.bind("	",lambda{ $Loop.loop.reverse() })

Keyboard.bind("o",lambda{ $Loop.loop.writeToFile("out5.mov",1.0,"mpeg4") })

capture = false
Keyboard.bind("p",lambda{
  if capture
    puts "stop capture."
    ScreenCapture.stop()
  else
    puts "start capture."
    ScreenCapture.start()
  end
  capture = !capture
})



Mouse.clear()
Mouse.use()
Mouse.bind("drag", lambda{|i|
	speed = (400 - i[1]) / 100.0
  decay = (i[0] - 400) / 100.0
  # decay = (decay + 4)/8
  # Loop.loop.setSpeed(speed)
	$Loop.loop.setAlphaBeta(decay, speed)
  # Loop.loop.setAlpha(decay)
})

$Loop.bgsub.updateBackgroundNextFrame()

$Loop.setSubtract(true)
$Loop.loop.setAlphaBeta(1,1)
# $Loop.loop.setAlpha(0.1)


Kinect.connect()
Kinect.startVideo()
# Kinect.startDepth()
Kinect.bgsub.updateBackgroundNextFrame()

Kinect.setAngle(0)

def animate(dt)
# puts $Loop.loop.alpha
# puts $Loop.loop.beta
  	Kinect.bgsub.setThreshold(10.0)
	# Texture.apply(0).draw(Kinect.depthPix,0,0)
	# Texture.apply(0).draw(Kinect.videoPix,0,480)

end
