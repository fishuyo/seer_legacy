require 'java'

$Seer = Java::com.fishuyo
$Key = $Seer.io.Keyboard
$Trackpad = $Seer.io.Trackpad
$Vec3 = $Seer.maths.Vec3
$Quat = $Seer.maths.Quat
$Camera = $Seer.graphics.Camera
$Audio = $Seer.audio.Audio
$Sine = $Seer.audio.Sine

$Main = $Seer.examples.synth.Main
sines = $Main.sines
osc = $Main.osc
lfo = $Main.lfo

$Key.clear()
$Key.use()
$Key.bind("n", lambda{
	#gen = ($Sine.new(1.0,100.0)).->($Sine.new(440,1))
	#$Audio.push( gen )
})

dx = 0.0
dy = 0.0
$Trackpad.clear()
$Trackpad.connect()
$Trackpad.bind( lambda{ |i,f| 
	for s in 0...i
		break if s >= sines.length()
		#puts sines[s].frequency
		sines[s].f(f[5+2*s] * 2000.0)
		sines[s].a(f[5+2*s+1] * 1.0)
	end

	if i == 3

	elsif i == 2	
		dx += f[2] * 0.1	
		dy += f[3] * -0.1
		lfo.f(dx*0.0)
		lfo.a(dy*0.0)
		osc.a(0.0)
		#osc.f(f[1]*0.0)
	else
	end
})



def step(dt)
	#puts $Seer.audio.Audio.dt
end
