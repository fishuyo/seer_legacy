require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.examples.opencv.feedback"
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
Keyboard.bind("r",lambda{ puts "r"; Main.loop.toggleRecord(); Main.audioLoop.toggleRecord() })
Keyboard.bind("c",lambda{ Main.loop.stop(); Main.loop.clear(); Main.audioLoop.clear() })
Keyboard.bind("x",lambda{ Main.loop.stack(); Main.audioLoop.stack() })
Keyboard.bind("t",lambda{ Main.loop.togglePlay(); Main.audioLoop.togglePlay() })
Keyboard.bind("v",lambda{ Main.loop.reverse(); Main.audioLoop.reverse() })
Keyboard.bind("z",lambda{ Main.loop.rewind(); Main.audioLoop.rewind() })

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

Trackpad.clear()
Trackpad.connect()
Trackpad.bind( lambda{ |i,f| 

  if i == 1
    Main.s.applyForce(Vec3.new((f[0]-0.5)*10,0,(f[1]-0.5)*10,))
  end
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
# OSC.listen(8082)
# OSC.bind("/1/toggle1", lambda{|f| Main.loop.toggleRecord() })
# OSC.bind("/1/toggle2", lambda{|f| Main.loop.stack() })
# OSC.bind("/1/toggle3", lambda{|f| Main.loop.togglePlay() })
# OSC.bind("/1/toggle4", lambda{|f| ScreenCapture.toggleRecord() })
# OSC.bind("/1/push1", lambda{|f| Main.loop.reverse() })
# OSC.bind("/1/push2", lambda{|f| Main.loop.stop(); Main.loop.clear() })
# OSC.bind("/1/push3", lambda{|f| Main.setSubtract(false) })
# OSC.bind("/1/push4", lambda{|f| Main.setSubtract(true); Main.bgsub.updateBackgroundNextFrame(); Main.loop.setAlphaBeta(1,1) })
# OSC.bind("/1/fader1", lambda{|f| Main.loop.setAlphaBeta(f[0], Main.loop.beta) })
# OSC.bind("/1/rotary3", lambda{|f| Main.loop.setAlphaBeta(-f[0], Main.loop.beta) })
# OSC.bind("/1/fader2", lambda{|f| Main.loop.setAlphaBeta(Main.loop.alpha, f[0]) })
# OSC.bind("/1/rotary6", lambda{|f| Main.loop.setAlphaBeta(Main.loop.alpha, -f[0]) })
# OSC.bind("/1/fader3", lambda{|f| Main.loop.setAlpha(f[0]); OSC.send(touchip,9000,"1/fader1", f[0]); OSC.send(touchip,9000,"1/fader2", 1.0-f[0]) })


# b1 = 0.0
# b2 = 1.0
# OSC.bind("/3/xy1", lambda{|f| b1=f[0]; looper.setBounds(l,b1,b2); looper.setGain(l,1-f[1]) })
# OSC.bind("/3/xy2", lambda{|f| b2=f[0]; looper.setBounds(l,b1,b2); looper.setSpeed(l,(1-f[1])*2.0) })

#softstep
b=0
OSC.listen(8010)
OSC.bind("/5/y", lambda{|f| b = f[0] })
OSC.bind("/0/y", lambda{|f| gain = f[0]/127.0; Audio.gain(gain*gain) })

OSC.bind("/3/top", lambda{|f|
  if b == 2
    Main.loop.toggleRecord()
    Main.audioLoop.toggleRecord()
    OSC.send("localhost",8009,"/9/y",0) 
  end
})
OSC.bind("/3/bot", lambda{|f| 
  if b == 2
    Main.loop.stack();
    Main.audioLoop.stack()
  end
})
OSC.bind("/4/bot", lambda{|f|
  if b == 2
    Main.loop.reverse(); Main.audioLoop.reverse()
  end
})
OSC.bind("/4/top", lambda{|f|
  if b == 2
    Main.loop.stop(); Main.loop.clear(); Main.audioLoop.clear()
  end
})
OSC.bind("/8/y", lambda{|f| 
  if b == 2
    Main.loop.setAlpha(f[0]/127.0)
  end
})
OSC.bind("/9/y", lambda{|f| 
  if b == 2
    gain = f[0]/127.0; Main.audioLoop.gain(2*gain*gain)
    OSC.send("localhost",8009,"/9/y",0)
  end
})

# OSC.bind("/1/top", lambda{|f| Main.loop.toggleRecord(); Main.audioLoop.toggleRecord() })
# OSC.bind("/1/bot", lambda{|f| Main.loop.stack(); Main.audioLoop.stack() })
# OSC.bind("/1/x", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/y", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/z", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/doubleTap", lambda{|f| looper.togglePlay(0) })

# OSC.bind("/1/top", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/bot", lambda{|f| looper.stack(0) })
# OSC.bind("/6/y", lambda{|f| Main.loop.setSpeed(0,4*(f[0]/127.0)) })
# OSC.bind("/6/y", lambda{|f| looper.setGain(0,2*f[0]/127.0) })
# OSC.bind("/6/z", lambda{|f| looper.setSpeed(0,f[0]/127.0+1) })
# OSC.bind("/1/doubleTap", lambda{|f| looper.togglePlay(0) })

# OSC.bind("/2/top", lambda{|f| Main.loop.rewind(); Main.audioLoop.rewind() })
# OSC.bind("/2/bot", lambda{|f| Main.loop.reverse(); Main.audioLoop.reverse() })
# OSC.bind("/1/x", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/y", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/2/z", lambda{|f| looper.setSpeed(2,f[0]/127.0+1.0) })
# # OSC.bind("/2/doubleTap", lambda{|f| looper.togglePlay(1) })

# OSC.bind("/3/top", lambda{|f| looper.toggleRecord(2) })
# OSC.bind("/3/bot", lambda{|f| looper.stack(2) })
# OSC.bind("/4/top", lambda{|f| looper.toggleRecord(3) })
# OSC.bind("/4/bot", lambda{|f| looper.stack(3) })
# OSC.bind("/5/top", lambda{|f| looper.toggleRecord(4) })
# OSC.bind("/5/bot", lambda{|f| looper.stack(4) })



# DesktopApp.setDecorated(false)
# DesktopApp.setBounds(0,0,1920,1200)

# for i in 0...Main.loop.images.length
#  Highgui.imwrite("/Users/fishuyo/img/"+i.to_s+".png",Main.loop.images[i])
# end

# Main.resize(0,0,80,80)

def animate dt

  # puts Main.audioLoop.b.rPos

  # puts Main.loop.alpha
  # puts Main.loop.beta
	# puts Main.w
	# puts Main.loop.images[0].type()
end