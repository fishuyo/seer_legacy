require 'java'

Seer = Java::com.fishuyo
Key = Seer.io.Keyboard
Track = Seer.io.TransTrack

bones = Seer.bones.Main.bones


Key.clear()
Key.use()


#Seer.graphics.Shader[1]

# Track.clear()
# Track.bind("point", lambda{|i,f|
# 	o = i# - 655361
# 	if o > 0 and o < 34
# 		bones[o].s.set(0.01,0.01,0.01)
# 		bones[o].p.pos.set(f[0],f[1],f[2])
# 		#bones[o].p.quat.set(f[6],f[3],f[4],f[5])
# 	end
# })
Track.setDebug(false)
#Track.start()


