
importPackage(com.fishuyo.io)
importPackage(com.fishuyo.maths)
importPackage(com.fishuyo.maths.particle)
importPackage(com.fishuyo.graphics)

importPackage(com.fishuyo.examples.particleSystems.verletFabric)


var fabric = Main.fabric()

Keyboard.clear()
Keyboard.use()
Keyboard.bind("g",function(){ println("hi"); Gravity.set(0,-Gravity.y(),0)})


Trackpad.connect()
Trackpad.clear()
Trackpad.bind(function(i,f){
	var s=10.0
	if( i == 1){
	}else if( i == 2){
		// Main.fabric().pins().head().position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		// Main.fabric().pins().last().position().lerpTo(Vec3(s*f[7],s*f[8],0), 0.1)
	}
})

function step( dt ){
	// println("hi");
	// var pins = Main.fabric().pins()
	// for( i=1; i < pins.length()-1; i++ ){
		// pins.apply(i).setActive(false)
	// }

}



