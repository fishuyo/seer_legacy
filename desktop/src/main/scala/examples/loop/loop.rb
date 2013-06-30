# require 'java'
Seer = Java::com.fishuyo
# Key = Seer.io.Keyboard
# Trackpad = Seer.io.Trackpad

# $Seer = Java::com.fishuyo
# $Vec3 = $Seer.maths.Vec3
# $Quat = $Seer.maths.Quat
# $Camera = $Seer.graphics.Camera


looper = Seer.examples.loop.Main.looper
l = 0

Keyboard.clear()
Keyboard.use()
Keyboard.bind("1",lambda{ l=0 })
Keyboard.bind("2",lambda{ l=1 })
Keyboard.bind("3",lambda{ l=2 })
Keyboard.bind("4",lambda{ l=3 })
Keyboard.bind("5",lambda{ l=4 })
Keyboard.bind("6",lambda{ l=5 })
Keyboard.bind("7",lambda{ l=6 })
Keyboard.bind("8",lambda{ l=7 })
Keyboard.bind("r",lambda{ looper.toggleRecord(l) })
Keyboard.bind("c",lambda{ looper.clear(l) })
Keyboard.bind("x",lambda{ looper.stack(l) })
Keyboard.bind("t",lambda{ looper.togglePlay(l) })
Keyboard.bind("v",lambda{ looper.reverse(l) })
Keyboard.bind("q",lambda{ looper.switchTo(l) })

Touch.clear()
Touch.use()

$newPos = Vec3.new(0,0,0)
Touch.bind("fling", lambda{|button,v|
	thresh = 500
	if v[0] > thresh
		l = l-1
	elsif v[0] < -thresh
		l = l+1
	elsif v[1] > thresh
		l = l-4
	elsif v[1] < -thresh
		l = l+4
	end
	l = l%looper.plots.size
	pos = looper.plots[l].pose.pos + Vec3.new(0,0,0.5)
	$newPos.set(pos)
})


Trackpad.clear()
Trackpad.connect()
down = false
s = []
t = []
Trackpad.bind( lambda{ |i,f|
		# 	a = []
		# for ff in f do
		# 	a.push(ff)
		# end
				#puts a.slice(5,4).join(" ")

	if i == 3
		if down == false
			s[0] = Vec3.new(f[5],f[6],0)
			s[1] = Vec3.new(f[7],f[8],0)
			s[2] = Vec3.new(f[9],f[10],0)
		end
		down = true

		t[0] = Vec3.new(f[5],f[6],0)
		t[1] = Vec3.new(f[7],f[8],0)
		t[2] = Vec3.new(f[9],f[10],0)
		snorm = (s[0]-s[1]).cross(s[0]-s[2])
		tnorm = (t[0]-t[1]).cross(t[0]-t[2])

		quat1 = Quat.new(0,0,0,1).getRotationTo(snorm,tnorm)
		quat2 = Quat.new(0,0,0,1).getRotationTo(quat1.rotate(s[0]-s[1]),(t[1]-t[0]))
		q = quat2 * quat1

		looper.plots[l].pose.quat.set(q)

		#looper.setGain(l,2*f[1])
		#looper.setPan(l,f[0])
		#looper.setDecay(l,f[0])
		#looper.setSpeed(l,f[1]*2.0)



	elsif i == 2
		looper.setGain(l,f[6])
		looper.setSpeed(l,f[8]*2.0)
		#print f[5]
		#puts f[6]
		looper.setBounds(l,f[5],f[7])
		
		down =false
	elsif i == 1
		looper.setPan(l,f[0])
		# puts f[0]
		down = false
	end
})



def step dt


	Camera.nav.pos.lerpTo($newPos, 0.1)
	t=0
end

