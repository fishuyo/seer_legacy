require 'java'

Seer = Java::com.fishuyo.seer
Key = Seer.io.Keyboard

P = Seer.examples.dla3d.ParticleCollector

Seer.graphics.Camera.rotateWorld(1.0)


Key.clear()
Key.use()
Key.bind("t", lambda{ P.setThresh(P.thresh + 0.005) })
Key.bind("g", lambda{ P.setThresh(P.thresh - 0.005) })
Key.bind("p", lambda{ P.writePoints("/Users/fishuyo/dla.points")})
Key.bind("o", lambda{ P.writeOrientedPoints("/Users/fishuyo/dla.xyz")})

print P.thresh






