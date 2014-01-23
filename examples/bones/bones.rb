require 'java'

Seer = Java::com.fishuyo.seer
Vec3 = Seer.maths.Vec3
Quat = Seer.maths.Quat
Key = Seer.io.Keyboard
Track = Seer.io.TransTrack
OSC = Seer.io.OSC
Shader = Seer.graphics.Shader
Camera = Seer.graphics.Camera
dla = Seer.examples.dla3d
P = dla.ParticleCollector

M = Seer.bones.Main
bones = M.bones

ground = M.ground
ground.scale.set(100,100,100)
ground.pose.pos.set(0,24.5,0)

drone = M.drone
#drone.drone.scale.set(1.0,1.0,1.0)
#drone.drone.pose.pos.set(0,0,0)
#drone.velocity.set(0,0,0)

x=0
z=0
y=0
r=0

firstPerson = false

### Keyboard ###
Key.clear()
Key.use()
Key.bind("j",lambda{x=-0.7;drone.move(x,z,y,r)})
Key.bind("l",lambda{x=0.7;drone.move(x,z,y,r)})
Key.bind("i",lambda{z=-0.7;drone.move(x,z,y,r)})
Key.bind("k",lambda{z=0.7;drone.move(x,z,y,r)})
Key.bindUp("j",lambda{x=0.0;drone.move(x,z,y,r)})
Key.bindUp("l",lambda{x=0.0;drone.move(x,z,y,r)})
Key.bindUp("i",lambda{z=0.0;drone.move(x,z,y,r)})
Key.bindUp("k",lambda{z=0.0;drone.move(x,z,y,r)})
Key.bind("y",lambda{y=5.0;drone.move(x,z,y,r)})
Key.bindUp("y",lambda{y=0.0;drone.move(x,z,y,r)})
Key.bind("h",lambda{y=-5.0;drone.move(x,z,y,r)})
Key.bindUp("h",lambda{y=0.0;drone.move(x,z,y,r)})
Key.bind("p",lambda{
	if drone.flying == true
		drone.land()
	else
		drone.takeOff()
	end
})
Key.bind("t", lambda{ P.setThresh(P.thresh + 0.005) })
Key.bind("g", lambda{ P.setThresh(P.thresh - 0.005) })
Key.bind("n", lambda{ P.writePoints("/Users/fishuyo/dla.points")})
Key.bind("m", lambda{ P.writeOrientedPoints("/Users/fishuyo/dla.xyz")})


### Tracker ###
Track.clear()
Track.bind("point", lambda{|i,f|
	o = i# - 655361
	if o > 0 and o < 34
		bones[o].scale.set(0.05,0.05,0.05)
		bones[o].pose.pos.set(f[0],f[1]+1.0,f[2])
		M.sines[o].f(f[1]+1.0*500.0)
		#amp = (bones[o].p.pos - ppos[o])
		M.sines[o].a(0.1)
		#M.ppos[o].set(f[0],f[1]+1.0,f[2])
		#bones[o].p.quat.set(f[6],f[3],f[4],f[5])
	end
})
drone.setPhysics(true)
Track.bind("rigid_body", lambda{|i,f|
	if i == 1
		drone.drone.p.pos.set(f[0],f[1]+1.0,f[2])
		drone.drone.p.quat.set(f[6],f[3],f[4],f[5])
		#drone.velocity.set(0,0,0)
	end


	if i==2 && firstPerson == true
		Camera.nav.pos.lerpTo(Vec3.new(f[0],f[1]+1.0,f[2]-1.0),0.05)
		Camera.nav.quat.slerpTo(Quat.new(f[6],f[3],f[4],f[5]),0.05)
		#Camera.nav.quat.set(f[6],f[3],f[4],f[5])
	end

})
Track.setDebug(false)
Track.start()


### OSC ###
OSC.bind("/c_ground", lambda{|f|
	ground.color.set(f[0],f[1],f[2])
})
OSC.bind("/c_bg", lambda{|f|
	Shader.setBgColor(Vec3.new(f[0],f[1],f[2]),1.0)
})
OSC.bind("/c_points", lambda{|f|
	P.color.set(f[0]*2,f[1]*2,f[2]*2)
})

#M.sines[0].a(0.01)





