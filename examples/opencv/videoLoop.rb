require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.examples.opencv.loop"
  include_package "com.fishuyo.seer.video"

end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end

ScreenCapture.scale_=(0.5)

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r",lambda{ puts "r"; Main.loop.toggleRecord() })
Keyboard.bind("c",lambda{ Main.loop.stop(); Main.loop.clear() })
Keyboard.bind("x",lambda{ Main.loop.stack() })
Keyboard.bind("t",lambda{ Main.loop.togglePlay() })
Keyboard.bind(" ",lambda{ Main.loop.reverse() })

Keyboard.bind("o",lambda{ Main.loop.writeToFile("",1.0,"mpeg4") })
Keyboard.bind("i",lambda{ Main.loop.writeToPointCloud("") })
Keyboard.bind("p",lambda{ puts "p"; ScreenCapture.toggleRecord() })

sub = false
Keyboard.bind("y",lambda{
  Main.setSubtract(sub)
  Main.bgsub.updateBackgroundNextFrame()
  Main.loop.setAlphaBeta(1,1)
  sub = !sub
})

Main.bgsub.setThreshold(10.0)

touchip="192.168.3.103"

Mouse.clear()
Mouse.use()
Mouse.bind("drag", lambda{|i|
	speed = (400 - i[1]) / 100.0
  decay = (i[0] - 400) / 100.0

  # if decay > 0.0 and decay < 1.0
  #   OSC.send(touchip,9000,"1/fader1", decay)
  # elsif decay < 0.0 and decay > -1.0
  #   OSC.send(touchip,9000,"1/rotary3", -decay)
  # elsif decay < -1.0
  #   OSC.send(touchip,9000,"1/rotary2", -decay)
  # elsif decay > 1.0
  #   OSC.send(touchip,9000,"1/rotary1", decay)
  # end

  # if speed > 0.0 and speed < 1.0
  #   OSC.send(touchip,9000,"1/fader2", speed)
  # elsif speed < 0.0 and speed > -1.0
  #   OSC.send(touchip,9000,"1/rotary6", -speed)
  # elsif speed < -1.0
  #   OSC.send(touchip,9000,"1/rotary5", -speed)
  # elsif speed > 1.0
  #   OSC.send(touchip,9000,"1/rotary4", speed)
  # end

  # decay = (decay + 4)/8
  Main.loop.setSpeed(1.0) #speed)
	# Main.loop.setAlphaBeta(decay, speed)
  # puts decay
  Main.loop.setAlpha(decay)
})


OSC.clear()
OSC.disconnect()
OSC.listen(8082)
OSC.bind("/1/toggle1", lambda{|f| Main.loop.toggleRecord() })
OSC.bind("/1/toggle2", lambda{|f| Main.loop.stack() })
OSC.bind("/1/toggle3", lambda{|f| Main.loop.togglePlay() })
OSC.bind("/1/toggle4", lambda{|f| ScreenCapture.toggleRecord() })
OSC.bind("/1/push1", lambda{|f| Main.loop.reverse() })
OSC.bind("/1/push2", lambda{|f| Main.loop.stop(); Main.loop.clear() })
OSC.bind("/1/push3", lambda{|f| Main.setSubtract(false) })
OSC.bind("/1/push4", lambda{|f| Main.setSubtract(true); Main.bgsub.updateBackgroundNextFrame(); Main.loop.setAlphaBeta(1,1) })
OSC.bind("/1/fader1", lambda{|f| Main.loop.setAlphaBeta(f[0], Main.loop.beta) })
OSC.bind("/1/rotary3", lambda{|f| Main.loop.setAlphaBeta(-f[0], Main.loop.beta) })
OSC.bind("/1/fader2", lambda{|f| Main.loop.setAlphaBeta(Main.loop.alpha, f[0]) })
OSC.bind("/1/rotary6", lambda{|f| Main.loop.setAlphaBeta(Main.loop.alpha, -f[0]) })
OSC.bind("/1/fader3", lambda{|f| Main.loop.setAlpha(f[0]); OSC.send(touchip,9000,"1/fader1", f[0]); OSC.send(touchip,9000,"1/fader2", 1.0-f[0]) })


b1 = 0.0
b2 = 1.0
OSC.bind("/3/xy1", lambda{|f| b1=f[0]; looper.setBounds(l,b1,b2); looper.setGain(l,1-f[1]) })
OSC.bind("/3/xy2", lambda{|f| b2=f[0]; looper.setBounds(l,b1,b2); looper.setSpeed(l,(1-f[1])*2.0) })

# SimpleAppRun.setDecorated(false)
# SimpleAppRun.setBounds(0,0,1920,1200)

# for i in 0...Main.loop.images.length
#  Highgui.imwrite("/Users/fishuyo/img/"+i.to_s+".png",Main.loop.images[i])
# end

# Main.resize(0,0,80,80)

def animate dt

  # puts Main.loop.alpha
  # puts Main.loop.beta
	# puts Main.w
	# puts Main.loop.images[0].type()
end