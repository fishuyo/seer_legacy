require 'java'

Seer = Java::com.fishuyo
Key = Seer.io.Keyboard

drone = Seer.dronesim.Main.drone
drone.drone.s.set(1.0,1.0,1.0)
#drone.drone.p.pos.set(0,0,0)
#drone.velocity.set(0,0,0)

x=0
z=0
y=0
r=0

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

drone.moveTo(-5.0,1.0,0.0,0.0)

Seer.graphics.Shader[1]






