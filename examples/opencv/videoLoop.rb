require 'java'

module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.examples.opencv.loop"
  include_package "com.fishuyo.video"

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
Keyboard.bind("r",lambda{ Main.loop.toggleRecord() })
Keyboard.bind("c",lambda{ Main.loop.stop(); Main.loop.clear() })
Keyboard.bind("x",lambda{ Main.loop.stack() })
Keyboard.bind("t",lambda{ Main.loop.togglePlay() })
Keyboard.bind(" ",lambda{ Main.loop.reverse() })

Keyboard.bind("o",lambda{ Main.loop.writeToFile("out2.mov",1.0,"mpeg4") })

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
  # Main.loop.setSpeed(speed)
	Main.loop.setAlphaBeta(decay, speed)
  # Main.loop.setAlpha(decay)
})

Main.setSubtract(false)
Main.bgsub.updateBackgroundNextFrame()
Main.bgsub.setThreshold(50.0)
Main.loop.setAlphaBeta(1,1)

# for i in 0...Main.loop.images.length
#  Highgui.imwrite("/Users/fishuyo/img/"+i.to_s+".png",Main.loop.images[i])
# end

def step dt
  # puts Main.loop.alpha
  # puts Main.loop.beta
	# puts Main.w
	# puts Main.loop.images[0].type()
end