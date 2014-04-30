// package com.fishuyo.seer
// package bones

// import maths._
// import graphics._
// import spatial._
// import io._
// import dynamic._
// import audio._
// import drone._

// import examples.dla3d._

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.InputAdapter
// import com.badlogic.gdx.graphics.glutils._
// //import com.badlogic.gdx.graphics.GL20

// object Main extends App{

//   DesktopApp.loadLibs()

//   Scene.push( ParticleCollector )
//   var p = new Particle(Vec3(.01,0,.01),Vec3(1,0,1).normalize)
//   p.setAlignment("line")
//   ParticleCollector.insertPoint(p)
//   p = new Particle(Vec3(-.01,0,.01),Vec3(-1,0,1).normalize)
//   p.setAlignment("line")
//   ParticleCollector.insertPoint(p)
//   p = new Particle(Vec3(0,0,-.01),Vec3(0,0,-1).normalize)
//   p.setAlignment("line")
//   ParticleCollector.insertPoint(p)

//   var bones = new Array[GLPrimitive](34)
//   var ppos = new Array[Vec3](34)
//   var sines = new Array[Gen](34)
//   for( i <- (0 until 34)){
// 		bones(i) = GLPrimitive.cube(Pose(), Vec3(.01f))
// 		sines(i) = new Sine(80.f,0.f)
// 	}
//   bones.foreach( Scene.push( _ ))
//   sines.foreach( Audio.push( _ ))




//   var drone = new ParticleDrone
//   Scene.push( drone )

//   val ground = ObjParser("src/main/scala/drone/ground.obj")
//   ground.scale.set(100.f,1.f,100.f)
//   Scene.push(ground)
//   // val ground2 = ObjParser("src/main/scala/drone/groundrough.obj")
//   // ground2.p.pos.set(0.f,0.f,-200.f)
//   // ground2.s.set(100.f,1.f,100.f)
//   // Scene.push(ground2)
//   // val ground3 = ObjParser("src/main/scala/drone/landscapealiendecimated.obj","triangles",false)
//   // //ground.s.set(100.f,1.f,100.f)
//   // Scene.push(ground3)


//   val live = new Ruby("src/main/scala/bones/bones.rb")

// 	// TransTrack.bind("point", (i:Int, f:Array[Float])=>{

// 	// 	if (i > 0 && i < 34){
// 	// 		bones(i).p.pos.set(f(0),f(1),f(2))
// 	// 		//bones(i).p.quat.set(f[6],f[3],f[4],f[5])
// 	// 	}
// 	// })

//   TransTrack.start()
//   OSC.listen()

//   //Trackpad.connect()
  
//   DesktopApp()  

// }

// class ParticleDrone extends FakeDrone {
// 	override def step(dt:Float){
// 		super.step(dt)

// 		import util._
// 		val x = Randf( drone.pose.pos.x -.25f, drone.pose.pos.x + .25f,true)
// 		val z = Randf( drone.pose.pos.z -.25f, drone.pose.pos.z + .25f,true)
// 		val vs = Randf(.05f,.2f)
// 		val vt = RandVec3(Vec3(-.05f,0.f,-.05f),Vec3(.05f,0.f,.05f))

// 		val pc = ParticleCollector
//     if( pc.collection.size < pc.maxParticles - pc.numParticles ){
//     	for( i<-(0 until 100)){
// 				val p = Vec3(x(),drone.pose.pos.y-.2f,z())
// 				val v = drone.pose.uu() * -thrust * vs() + vt()
// 	      val newp = new Particle(p,v)
// 	      //newp.setAlignment("line")
// 	      if( pc.particles.size >= pc.numParticles) pc.particles.remove(0)
// 	      pc.particles += newp
//     	}
//     }

// 	}
// }



