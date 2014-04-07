require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.audio"
  include_package "com.fishuyo.seer.thereisaplace"
  include_package "com.fishuyo.seer.video"
  include_package "com.fishuyo.seer"


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
  x = i[0] / (1.0*Window.width)
  y = i[1] / (1.0*Window.height)
  speed = (400 - i[1]) / 100.0
  decay = (i[0] - 400) / 100.0

  # Main.loop.setSpeed(1.0) #speed)
  # Main.loop.setAlphaBeta(decay, speed)
  # Main.loop.setAlpha(decay)
})
l=0.0
t=0.0
doResize=false
Mouse.bind("down", lambda{|i|
  l = i[0] / (1.0*Window.width)
  t = i[1] / (1.0*Window.height)
})
Mouse.bind("up", lambda{|i|
  x = i[0] / (1.0*Window.width)
  y = i[1] / (1.0*Window.height)
  Main.resizeC(l,t,x,y) if doResize
  doResize = false
})

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r",lambda{ puts "r"; Main.loop.toggleRecord(); Main.looper.toggleRecord(4) })
Keyboard.bind("c",lambda{ Main.loop.stop(); Main.loop.clear(); Main.looper.clear(4) })
Keyboard.bind("x",lambda{ Main.loop.stack(); Main.looper.stack(4) })
Keyboard.bind("t",lambda{ Main.loop.togglePlay(); Main.looper.togglePlay(4) })
Keyboard.bind("v",lambda{ Main.loop.reverse(); Main.looper.reverse(4) })
Keyboard.bind("z",lambda{ Main.loop.rewind(); Main.looper.rewind(4) })
Keyboard.bind("1",lambda{ Main.resizeFull(); doResize=true })

# Keyboard.bind("l",lambda{ puts "save"; Main.looper.loops.apply(0).save("Desktop/boop.wav") })

OSC.clear()
OSC.disconnect()

#softstep
b=2
OSC.listen(8008)
OSC.bind("/5/y", lambda{|f| b = f[0] })
OSC.bind("/0/y", lambda{|f| gain = f[0]/127.0; Audio.gain(gain*gain); puts gain })

OSC.bind("/1/top", lambda{|f|
  if b == 2
    Main.loop.toggleRecord()
  end
  Main.looper.toggleRecord(2*b)
})
OSC.bind("/1/bot", lambda{|f| 
  if b == 2
    Main.loop.stack();
  end
  Main.looper.stack(2*b)
})
OSC.bind("/2/bot", lambda{|f|
  if b == 2
    Main.loop.reverse()
  end
  Main.looper.reverse(2*b)
})
OSC.bind("/2/top", lambda{|f|
  if b == 2
    Main.loop.stop(); Main.loop.clear();
  end
  Main.looper.togglePlay(2*b)
})
OSC.bind("/6/y", lambda{|f| 
  if b == 2
    Main.loop.setAlpha(f[0]/127.0)
  else
  	Main.looper.setSpeed(2*b,4*(f[0]/127.0))
  end
})
OSC.bind("/7/y", lambda{|f| 
  if b == 2
 #  	Shader.apply("composite")
	# Shader.shader.get.uniforms.update("u_blend0", 1.0*f[0]/127.0)
	# Shader.shader.get.uniforms.update("u_blend1", f[1])
  end
  gain = f[0]/127.0; Main.looper.setGain(2*b,2*gain*gain)
})

OSC.bind("/3/top", lambda{|f|
  if b == 2
  else
  	Main.looper.toggleRecord(2*b+1)
  end
})
OSC.bind("/3/bot", lambda{|f| 
  if b == 2
  else
	Main.looper.stack(2*b+1)
  end
})
OSC.bind("/3/y", lambda{|f| 
  if b == 2
  	Shader.apply("composite")
    Shader.shader.get.uniforms.update("u_blend0", 4.0*f[0]/127.0)
  end
})
OSC.bind("/4/bot", lambda{|f|
  if b == 2
	Shader.shader.get.uniforms.update("mode", 0)
  else
  	Main.looper.reverse(2*b+1)
  end
})
OSC.bind("/4/top", lambda{|f|
  if b == 2
	Shader.shader.get.uniforms.update("mode", 1)
  else
  	Main.looper.togglePlay(2*b+1)
  end
})
OSC.bind("/8/y", lambda{|f| 
  if b == 2
  	Main.player.playing(true)
  	Shader.apply("composite")
    Shader.shader.get.uniforms.update("u_blend1", f[0]/127.0)
  else
  	Main.looper.setSpeed(2*b+1,4*(f[0]/127.0))
  end
})
OSC.bind("/9/y", lambda{|f| 
  if b == 2
  	Main.player.playing(true)
  	Shader.apply("composite")
  	fade = f[0]/127.0
    Shader.shader.get.uniforms.update("u_blend0", 1.0-fade)
    Shader.shader.get.uniforms.update("u_blend1", fade)
  else
  	gain = f[0]/127.0; Main.looper.setGain(2*b+1,2*gain*gain)
  end
})

# Main.looper.loops.apply(0).save("Desktop/boop.wav")

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

# Main.resize(300,0,500,320)
def init
	# Main.resize(360,300,430,320)
  # Main.resize(300,300,560,320)
  Shader.apply("composite")
  fade = 0.5
  Shader.shader.get.uniforms.update("u_blend0", 1.0-fade)
  Shader.shader.get.uniforms.update("u_blend1", fade)

end

def animate dt
    Shader.apply("composite")
  fade = 0.0
  Shader.shader.get.uniforms.update("u_blend0", 1.0-fade)
  Shader.shader.get.uniforms.update("u_blend1", fade)

end