require 'java'

Seer = Java::com.fishuyo
Key = Seer.io.Keyboard
Pad = Seer.io.Trackpad

drone = Seer.dronesim.Main.drone
$D = drone
#drone.drone.s.set(1.0,1.0,1.0  )
#drone.drone.p.pos.set(0,0,0)
#drone.velocity.set(0,0,0)

#drone.plot.color.set(4.8,5.3,1.3)

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



mx=0.0
my=0.0
mz=0.0
Pad.bind( lambda{|i,f|
	if i == 2
		mx = mx + f[2]*0.05  
		mz = mz + f[3]*-0.05
		drone.moveTo(mx,my,mz,0.0)
		#drone.moveTo(2.0*f[0],1.0,-2.0*f[1],0.0)
	elsif i == 3
		mx = mx + f[2]*0.05  
		my = my + f[3]*0.05
		drone.moveTo(mx,my,mz,0.0)
		#drone.moveTo(2.0*f[0],1.0,-2.0*f[1],0.0)
	end
})

#drone.moveTo(1.0,1.0,1.0,0.0)
drone.setMoveSpeed(5.0)
drone.setMaxEuler(0.6)
#Seer.graphics.Shader[1]


def step(dt)

  z = Java::com.fishuyo.util.Randf.apply(-0.5,0.5,false)
  #Java::com.fishuyo.dronesim.Main.drone.trace.apply($D.pose.pos)
  #puts Java::com.fishuyo.dronesim.Main.drone.trace.vertices[0]
 
  r = Java::com.fishuyo.util.Randf.apply(-0.01,0.01,false)
  d = Java::com.fishuyo.graphics.GLScene.drawable
  if d.size < 10
  	return
  end
  for i in 10 .. d.size
  	o = d.apply(i)
  	s = o.scale.x + r.apply()
  	o.scale.set(s,s,s)
  end
  #Java::com.fishuyo.dronesim.Main.drone.drone.p.pos.set(0,0,0)
  #Java::com.fishuyo.dronesim.Main.drone.velocity.set(0,0,0)
end

Key.bind("n",lambda{
	#a = Seer.graphics.GLPrimitive.cube(Seer.spatial.Pose.apply(), Seer.maths.Vec3.apply(0.1), Seer.graphics.RGB.green )
	a = Seer.graphics.GLPrimitive.sphere(Seer.spatial.Pose.apply(), Seer.maths.Vec3.apply(0.1), 2.0, 30 )
	a.pose.pos.set(mx,my,mz)
	Seer.graphics.GLScene.push(a)
	puts "hi"
})

