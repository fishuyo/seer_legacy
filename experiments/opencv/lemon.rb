require 'java'

module M
  include_package "com.fishuyo.seer.io"
  include_package "com.fishuyo.seer.maths"
  include_package "com.fishuyo.seer.graphics"
  include_package "com.fishuyo.seer.examples.opencv.lemonsynth"
  include_package "com.fishuyo.seer.video"
  include_package "com.fishuyo.seer.audio.gen"
  include_package "com.fishuyo.seer.audio"
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
Keyboard.bind("p", lambda{ puts "p"; ScreenCapture.toggleRecord() })

Main.blobTracker.clear()
Main.blobTracker.bind( lambda{ |blobs|
	return if blobs.size <= 0 
	b = blobs[0]
	a = 0.7
	freq = Main.synth.freq.value * a + (b.x*10.0) * (1-a)
	Main.synth.f(freq)
	# Main.synth.a(0)
	Main.synth.a( Env.decay(b.y * 0.001)) if Main.synth.amp.value == 0.0 

})

Audio.start()

def draw
end

def animate dt
  # Main.bgsub.updateBackgroundNextFrame()
  # Main.bgsub.setThreshold(50.0)
end