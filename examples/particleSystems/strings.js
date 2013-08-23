
importPackage(com.fishuyo)
importPackage(com.fishuyo.io)
importPackage(com.fishuyo.io.leap)
importPackage(com.fishuyo.maths)
importPackage(com.fishuyo.graphics)
importPackage(com.fishuyo.examples.particleSystems.strings)


// var fabric = Main.fabric()

Mouse.clear()
Mouse.use()
Mouse.bind("drag", function(i){ 
	var x = (i[0] / SimpleAppRun.app().width()) * 20.0
	var y = (1 - i[1] / SimpleAppRun.app().height()) * 20.0

	Main.strings().apply(0).pins().apply(0).position().set(x,y,0)
	
})

Trackpad.connect()
Trackpad.clear()
Trackpad.bind(function(i,f){
	var s=10.0
	if( i == 1){
		Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(2).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(3).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(4).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
	}else if( i == 2){
		Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[7],s*f[8],0), 0.1)

	}else if( i==3 ){
		Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[7],s*f[8],0), 0.1)
		Main.strings().apply(2).pins().apply(0).position().lerpTo(Vec3(s*f[9],s*f[10],0), 0.1)

	}else if( i==4){
		Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[7],s*f[8],0), 0.1)
		Main.strings().apply(2).pins().apply(0).position().lerpTo(Vec3(s*f[9],s*f[10],0), 0.1)
		Main.strings().apply(3).pins().apply(0).position().lerpTo(Vec3(s*f[11],s*f[12],0), 0.1)


	}else if( i==5){
		Main.strings().apply(0).pins().apply(0).position().lerpTo(Vec3(s*f[5],s*f[6],0), 0.1)
		Main.strings().apply(1).pins().apply(0).position().lerpTo(Vec3(s*f[7],s*f[8],0), 0.1)
		Main.strings().apply(2).pins().apply(0).position().lerpTo(Vec3(s*f[9],s*f[10],0), 0.1)
		Main.strings().apply(3).pins().apply(0).position().lerpTo(Vec3(s*f[11],s*f[12],0), 0.1)
		Main.strings().apply(4).pins().apply(0).position().lerpTo(Vec3(s*f[13],s*f[14],0), 0.1)

	}
})

Leap.connect()
Leap.clear()
Leap.bind(function(frame){

	if( frame.hands().isEmpty() ) return
	var hand = frame.hands().get(0)
	if( hand.fingers().isEmpty() ) return
	var count = hand.fingers().count()

	var s = 0.01

	for( i=0; i < count; i++){
		var finger = hand.fingers().get(i)
		var pos = finger.tipPosition()
		Main.strings().apply(i).pins().apply(0).position().lerpTo(Vec3(s*pos.getX(),s*pos.getY(),s*pos.getZ()), 0.1)
	}

})

function step( dt ){
	// println("hi")
	Shader.lightAmbient().set( RGBA.apply(0,1,1,1))

	var p = Main.strings().apply(0).pins().apply(0).position()
	p = Vec3(p.x(),p.y(),p.z()+50)
	// SimpleAppRun.app().camera().nav().pos().lerpTo(p, 0.05)

}



