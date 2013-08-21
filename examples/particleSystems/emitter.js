
importPackage(com.fishuyo.io)
importPackage(com.fishuyo.maths)
importPackage(com.fishuyo.graphics)
importPackage(com.fishuyo.examples.particleSystems.emitter)


var emitter = Main.emitter()

Mouse.clear()
Mouse.use()
Mouse.bind("drag", function(i){ 
    var x = i[0]*10
    var y = i[1]

    Main.emitter().setMaxParticles(x)
})

function step( dt ){

	// emitter.setMaxParticles(1)

}



