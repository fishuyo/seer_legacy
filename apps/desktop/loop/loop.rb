
looper = Java::com.fishuyo.seer.examples.loop.Main.looper
$Main = Java::com.fishuyo.seer.examples.loop.Main
$looper = looper
l = 0


Keyboard.clear()
Keyboard.use()
Keyboard.bind("1",lambda{ l=0; $Main.l_=(0) })
Keyboard.bind("2",lambda{ l=1; $Main.l_=(1) })
Keyboard.bind("3",lambda{ l=2 })
Keyboard.bind("4",lambda{ l=3 })
Keyboard.bind("5",lambda{ l=4 })
Keyboard.bind("6",lambda{ l=5 })
Keyboard.bind("7",lambda{ l=6 })
Keyboard.bind("8",lambda{ l=7 })
Keyboard.bind("r",lambda{ looper.toggleRecord(l) })
Keyboard.bind("c",lambda{ looper.stop(l); looper.clear(l) })
Keyboard.bind("x",lambda{ looper.stack(l) })
Keyboard.bind("t",lambda{ looper.togglePlay(l) })
Keyboard.bind("	",lambda{ looper.reverse(l) })
Keyboard.bind("p",lambda{ looper.switchTo(l) })
Keyboard.bind("m",lambda{ looper.setMaster(l) })
Keyboard.bind("b",lambda{ looper.duplicate(l,1) })
Keyboard.bind("l",lambda{ PortAudio.toggleRecording() })

Keyboard.bind("j",lambda{ looper.save("") })
Keyboard.bind("k",lambda{ looper.load("project-Nov-3,-2013-1-21-06-AM") })

Keyboard.bind("f", lambda{
	OSC.send("127.0.0.1", 8001, "/test", "f")
	looper.setGain(l,1)
	looper.setSpeed(l,1)
	looper.setBounds(l,0,1)
	looper.toggleRecord(l)
})
Keyboard.bind("g", lambda{
	looper.setGain(l,1)
	looper.setSpeed(l,1)
	looper.setBounds(l,0,1)
	looper.setDecay(l,0.99)
	looper.stack(l)
})

Touch.clear()
Touch.use()

$newPos = Vec3.new(0.3,-0.3,2.0)
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

# Mouse.clear()
# Mouse.use()
# Mouse.bind( "down", lambda{ |i|
# 	x = i[0]
# 	y = i[1]
# 	ray = Camera.ray(x,y)
# 	t = ray.intersectQuad($model.pose.pos, 0.05, 0.05 )
# 	if t.isDefined()
# 		$model.nodes[0].color.set(RGBA.apply(1,0,0,0))
# 		looper.toggleRecord(l)
# 	else
# 		$model.nodes[0].color.set(RGBA.apply(0,0,1,0))
# 	end
# })


Trackpad.clear()
Trackpad.connect()

down = false
s = []
t = []
width = 0.001
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

		# looper.plots[l].pose.quat.set(q)

		# looper.setGain(l,2*f[1])
		# looper.setPan(l,f[0])
		#looper.setDecay(l,f[0])
		# looper.setSpeed(l,f[1]*2.0)



	elsif i == 2
		looper.setGain(l,f[6])
		looper.setSpeed(l,f[8]*2.0)
		#print f[5]
		#puts f[6]
		looper.setBounds(l,f[5],f[7])
		
		down =false
	elsif i == 1
		looper.setPan(l,f[0])

		width += 0.01*f[3]
		width = 0.0 if width < 0.0
		width = 1.0 - f[0] if width > 1.0 - f[0]
		e = f[0] + width
		e = 1.0 if e > 1.0
		#looper.setBounds(l,f[0],e)

		# puts f[0]
		down = false
	end
})


OSC.clear()
OSC.disconnect()
# OSC.listen(8082)
# OSC.bind("/button", lambda{|f| looper.toggleRecord(l) if f[0] == 1 })
# OSC.bind("/x", lambda{|f| looper.setSpeed(l,f[0]*2.0); OSC.send("localhost", 8001, "/test", f[0])    })
# OSC.bind("/1/toggle1", lambda{|f| looper.toggleRecord(l) })
# OSC.bind("/1/toggle2", lambda{|f| looper.stack(l) })
# OSC.bind("/1/toggle3", lambda{|f| Audio.toggleRecording() })
# OSC.bind("/1/push1", lambda{|f| looper.setMaster(l) })
# OSC.bind("/1/rotary1", lambda{|f| l=0; looper.setPan(l,f[0]) })
# OSC.bind("/1/rotary2", lambda{|f| l=1; looper.setPan(l,f[0]) })
# OSC.bind("/1/rotary3", lambda{|f| l=2; looper.setPan(l,f[0]) })
# OSC.bind("/1/rotary4", lambda{|f| l=3; looper.setPan(l,f[0]) })
# OSC.bind("/1/rotary5", lambda{|f| l=4; looper.setPan(l,f[0]) })
# OSC.bind("/1/rotary6", lambda{|f| l=5; looper.setPan(l,f[0]) })

# b1 = 0.0
# b2 = 1.0
# OSC.bind("/3/xy1", lambda{|f| b1=f[0]; looper.setBounds(l,b1,b2); looper.setGain(l,1-f[1]) })
# OSC.bind("/3/xy2", lambda{|f| b2=f[0]; looper.setBounds(l,b1,b2); looper.setSpeed(l,(1-f[1])*2.0) })

# SoftStep
OSC.listen(8008)
OSC.bind("/1/top", lambda{|f| looper.toggleRecord(0) })
OSC.bind("/1/bot", lambda{|f| looper.stack(0) })
# OSC.bind("/1/x", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/y", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/z", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/doubleTap", lambda{|f| looper.togglePlay(0) })

# OSC.bind("/1/top", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/bot", lambda{|f| looper.stack(0) })
OSC.bind("/6/y", lambda{|f| looper.setSpeed(0,4*(f[0]/127.0)) })
# OSC.bind("/6/y", lambda{|f| looper.setGain(0,2*f[0]/127.0) })
# OSC.bind("/6/z", lambda{|f| looper.setSpeed(0,f[0]/127.0+1) })
# OSC.bind("/1/doubleTap", lambda{|f| looper.togglePlay(0) })

OSC.bind("/2/top", lambda{|f| looper.toggleRecord(1) })
OSC.bind("/2/bot", lambda{|f| looper.stack(1) })
# OSC.bind("/1/x", lambda{|f| looper.toggleRecord(0) })
# OSC.bind("/1/y", lambda{|f| looper.toggleRecord(0) })
OSC.bind("/2/z", lambda{|f| looper.setSpeed(2,f[0]/127.0+1.0) })
# OSC.bind("/2/doubleTap", lambda{|f| looper.togglePlay(1) })

OSC.bind("/3/top", lambda{|f| looper.toggleRecord(2) })
OSC.bind("/3/bot", lambda{|f| looper.stack(2) })
OSC.bind("/4/top", lambda{|f| looper.toggleRecord(3) })
OSC.bind("/4/bot", lambda{|f| looper.stack(3) })
OSC.bind("/5/top", lambda{|f| looper.toggleRecord(4) })
OSC.bind("/5/bot", lambda{|f| looper.stack(4) })


def animate dt

	$looper.setMode("sync")
	Audio.playThru(false)
	Audio.recordThru(true)
	# Camera.nav.pos.lerpTo($newPos, 0.15)

	pos = Camera.nav.pos + Vec3.apply(-0.45,0,-0.55) #$model.pose.pos
	# pos.set(pos.x, pos.y, 0)
	$Main.buttons.pose.pos.lerpTo(pos, 0.2)
	# puts $Main.buttons.pose.pos
	t=0
end



def draw
	# $model.draw()
end