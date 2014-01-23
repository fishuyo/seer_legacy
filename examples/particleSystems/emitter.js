
importPackage(com.fishuyo.seer)
importPackage(com.fishuyo.seer.io)
importPackage(com.fishuyo.seer.maths)
importPackage(com.fishuyo.seer.graphics)
importPackage(com.fishuyo.seer.examples.particleSystems.emitter)


var emitter = Main.emitter()

Mouse.clear()
Mouse.use()
Mouse.bind("drag", function(i){ 
    var x = i[0] * 1.0 / Window.width() - 0.5 
    var y = i[1] * -1.0 / Window.height() + 0.5

    var dir = Main.emitter().direction()
    dir.set(x,y,0.0)
    // dir.normalize()

    Main.emitter().velocity_$eq(1.0)
    Main.emitter().spread_$eq(0.02)

    Main.emitter().setMaxParticles(1000)
})

function animate( dt ){

	// emitter.setMaxParticles(1)

}



