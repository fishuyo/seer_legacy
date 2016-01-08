
// package com.fishuyo.seer
// package examples.graphics

// import graphics._
// import particle._
// import spatial._
// import util._
// import io._

// import collection.mutable.ArrayBuffer
// import collection.mutable.ListBuffer
// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics.GL20


// class Agent(name:String="A") {
//   var text = ""
//   var damping = 20.0f
//   var nav = Nav()
//   nav.pos = Random.vec3()
//   nav.quat.set(Quat.up)
//   nav.vel.set(0,0,-80.0)
//   var particles = ListBuffer[Particle]()
//   var springs = ListBuffer[LinearSpringConstraint]()
//   add(name)

//   def add(s:String){s.foreach(add(_))}
//   def add(a:Char){
//     text += a
//     if(particles.length > 0){
//       val q = particles.last
//       val p = Particle(q.position+Vec3(0,-1,0)*15.0, Vec3())
//       particles += p
//       springs += LinearSpringConstraint(p,q,15.0,0.1)
//     } else particles += Particle(Random.vec3()*100,Vec3())
//   }

//   def animate(dt:Float){
//     // nav.lookAt(Vec3(),0.001f)
//     nav.turn.set(0,util.Random.float(-.2,0.2)(),0)
//     nav.step(dt)
//     nav.pos.z = 0.0f
//     nav.pos.lerpTo(Vec3(), 0.001f)
//     if(nav.pos.x < -300f) nav.pos.x = -300f
//     if(nav.pos.x > 300f) nav.pos.x = 300f
//     if(nav.pos.y < -300f) nav.pos.y = -300f
//     if(nav.pos.y > 300f) nav.pos.y = 300f

//     for( s <- (0 until 3) ){ 
//       springs.foreach( _.solve() )
//       // pins.foreach( _.solve() )
//     }
//     particles.head.position.lerpTo(nav.pos,0.01f)


//     particles.foreach( (p) => {
//       // p.applyGravity()
//       p.applyDamping(damping)
//       p.step() // timeStep
//       p.collideGround(-300f, 0.9f) 
//       if( p.position.y > 300f){
//         p.position.y = 300f
//         p.velocity.y *= -1
//         p.lPosition = p.position - p.velocity * 0.9f
//       }
//       if( p.position.x < -300f){
//         p.position.x = -300f
//         p.velocity.x *= -1
//         p.lPosition = p.position - p.velocity * 0.9f
//       }
//       if( p.position.x > 300f){
//         p.position.x = 300f
//         p.velocity.x *= -1
//         p.lPosition = p.position - p.velocity * 0.9f
//       }
//     })
//   }

//   def draw(){
//     particles.zipWithIndex.foreach{ case (p,i) =>
//       Text.render(text(i).toString, p.position*1.0f)
//     }
//   }
// }


// object TextExample extends SeerApp {

//   var t = 0f
//   var lpos = Vec2()
//   var vel = Vec2()

//   val particles = new ParticleEmitter(200){
//     var chars = ListBuffer[Char]()
//     override def draw(){
//       particles.zip(chars).foreach{ case (p,c) =>
//         Text.render(c.toString, p.position)
//       }
//     }
//   }

//   val n = 40
//   val field = new VecField3D(n,Vec3(0),200f)
//   var updateField = false
//   particles.field = Some(field)
//   particles.fieldAsForce = false
//   randomizeField()

//   Gravity.set(0,0,0)

//   var initd = false

//   val agents = ArrayBuffer[Agent]()

//   // Keyboard.bindCamera(Text.camera)
//   // Text.camera.nav.scale = 100f

//   Keyboard.bind("g", ()=>{ 
//     if(Gravity.y == 0f) Gravity.set(0,-5,0)
//     else Gravity.zero()
//   })

//   val words = ArrayBuffer[String]()
//   words += "mysterious"
//   words += "werewolf"
//   words += "tiny"
//   words += "touch"
//   words += "won't"
//   words += "be"
//   words += "allow"
//   words += "searching.."
//   words += "sounds"
//   words += "cold"
//   words += "fire"
//   words += "turtle"
//   words += "i feel"
//   words += "not quite"
//   words += "in between"
//   words += "soiled"
//   val randomWord = Random.oneOf(words:_*)

//   Keyboard.bind("i", ()=>{ agents += new Agent(randomWord()) })
//   Keyboard.bind("o", ()=>{ agents += new Agent("O") })
//   Keyboard.bind("u", ()=>{ agents += new Agent("go hang a salami") })
//   Keyboard.bind("y", ()=>{ agents += new Agent("goh angasal a mi") })
//   Keyboard.bind("p", ()=>{ agents.last.add('s')})
//   Keyboard.bind("r", ()=>{ reset() })
//   // Keyboard.bindTyped( (k) => { agents(0).add(k) })

//   def reset(){
//     agents.clear
//     // agents += new Agent("mysterious")
//     // agents += new Agent("elegant")
//     // agents += new Agent("werewolf")
//     // agents += new Agent("magnanomous")
//   }

//   def randomizeField(){
//     for( z<-(0 until n); y<-(0 until n); x<-(0 until n)){
//       val cen = field.centerOfBin(x,y,z).normalize
//       // field.set(x,y,z,Vec3(0))
//       field.set(x,y,z, Random.vec3()*1)
//     }
//   }

//   override def draw(){
//     Text.scale = 0.01f
//     Text.begin()
//     agents.foreach( _.draw() )
//     Text.end()

//     Text.scale = 0.003f
//     Text.begin()
//     particles.draw()
//     Text.end()
//   }


//   override def animate(dt:Float){

//     if(!initd){
//       Text.loadFont("../../res/fonts/arial")
//       Text.sb.enableBlending()
//       Text.sb.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE)

//       initd = true
//       reset()
//     }


//     t += dt 

//     // Text.camera.step(dt)

//     implicit def f2i(f:Float) = f.toInt

//     if( Mouse.status() == "drag"){
//       vel = (Mouse.xy() - lpos)/dt
//       // println(vel)
//       // s.applyForce( Vec3(vel.x,vel.y,0)*10.0f)
//       val r = Text.camera.ray(Mouse.x()*Window.width, (1f-Mouse.y()) * Window.height)
//       agents.flatMap(_.particles).foreach( (p) => {
//         val t = r.intersectSphere(p.position, 10f)
//         if(t.isDefined){
//           // val p = r(t.get)
//           p.applyForce(Vec3(vel.x,vel.y,0)*150f)
//           // cursor.pose.pos.set(r(t.get))
//         }
//       })
//     }
//     lpos = Mouse.xy()

//     agents.foreach{ case a =>
//       particles.particles.zip(particles.chars).foreach { case (p,c) =>
//         if((p.position - a.particles.head.position).mag() < 2){
//           a.add(c)
//           p.t = particles.ttl
//         }
//       }
//       agents.foreach{ case b =>
//         val dist = (a.particles.last.position - b.particles.last.position).mag()
//         if( dist > 0 && dist < 4){
//           a.springs += LinearSpringConstraint(a.particles.last,b.particles.last,15.0,0.1)
//         }
//       }
//       a.animate(dt)
//     }

//     val size = 10
//     for(i <- ( -size until size); j <- (-size*2 until size*2)){
//       if(Random.float() < 0.001){
//         particles.chars += scala.util.Random.nextPrintableChar
//         particles += Particle(Vec3(i,j,0)*10, Random.vec3()*4)
//       }
//     }
//     particles.animate(dt)
//     if( particles.chars.length > particles.maxParticles ) particles.chars = particles.chars.takeRight(particles.maxParticles)



//   }
// }



