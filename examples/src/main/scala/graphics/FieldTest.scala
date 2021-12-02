
// package com.fishuyo.seer
// package examples.graphics

// import graphics._
// import spatial._
// import util._
// import particle._
// import io._

// import concurrent.duration._

// object FieldTest extends SeerApp {

//   val fv = new FV(100,100)
//   val vfv = new VFV(100,100,0.02f,0.02f)

//   val emitter = new Emitter(1000)
//   emitter.field = Some(vfv.field)

//   // Scene += fv
//   // Scene += vfv
//   // Scene += emitter

//   val every = Schedule.every(20 millis){
//     emitter += Particle(Vec3(Random.vec2()*1), Vec3())
//   }

//   Keyboard.listen {
//     case 'f' => emitter.fieldAsForce = !emitter.fieldAsForce
//     case 'p' => every.cancel; emitter.ttl = 0
//     case _ => ()
//   }

//   override def init(){
//     fv.init
//     vfv.init
//     emitter.init
//   }
//   override def draw(){
//     FPS.print
//     fv.draw
//     vfv.draw
//     emitter.draw
//   }

//   override def animate(dt:Float){
//     try{
//       val m = Mouse.xy.now * fv.w
//       val s = 2
//       for(i <- -s to s; j <- -s to s){
//         // fv.field(m.x.toInt+i,m.y.toInt+j) = 1.0f
//         // fv.field(50+i,2+j) = 1.0f
//       }

//       val ray = Camera.ray((Mouse.x.now * Window.width).toInt, ((1f-Mouse.y.now)*Window.height).toInt)
//       ray.intersectQuad(Vec3(),10,10) match {
//         case Some(t) => val p = ray(t); vfv.field(p.xy) = Mouse.vel.now
//         case None => ()
//       }

      
//     } catch { case e:Exception => ()}

//     VecField2D.diffuseSolve(vfv.field, 0.15f, 1)
//     // VecField2D.diffuse(vfv.field)
//     VecField2D.project(vfv.field)
//     VecField2D.advect(vfv.field, vfv.field, 0.15f, 1)
//     VecField2D.project(vfv.field)

//     // Field2D.diffuseSolve(fv.field, 0.15f)
//     // Field2D.diffuse(fv.field)
//     // Field2D.advect(fv.field, vfv.field, 0.15f)

//     fv.animate(dt)
//     vfv.animate(dt)
//     emitter.animate(dt)
//   }
// }

// class FV(val w:Int, val h:Int) extends Animatable {

//   var field = new Field2D(w,h)
//   var image = Image(w,h,1,4)
//   var texture:Texture = _ 
//   val quad = Plane()
//   quad.pose.pos.z = -0.01f

//   override def init(){
//     texture = Texture(image)
//     quad.material.color.set(1,0,0,1)
//     quad.material.loadTexture(texture)
//   }

//   override def draw() = {
//     image.floatBuffer.put(field.data)
//     texture.update
//     quad.draw
//   }

// }


// class VFV(val w:Int, val h:Int, sx:Float=0.1f, sy:Float=0.1f) extends Animatable {

//   var field = new VecField2D(w,h,sx,sy)
//   var mesh = Mesh()
//   mesh.maxVertices = 2*w*h
//   mesh.primitive = Lines
//   var model = Model(mesh)
//   model.material.color.set(0.5,0.5,0.5,0.5)
//   var image = Image(w,h,1,4)
//   var texture:Texture = _ 
//   val quad = Plane()

//   override def init(){
//     texture = Texture(image)
//     quad.material.color.set(1,0,0,1)
//     quad.material.loadTexture(texture)

//     for( y<- 0 until h; x<- 0 until w){
//       // val cen = field.centerOfBin(x,y,z).normalize
//       field(x,y) = (util.Random.vec2().normalize() * 0.5f*field.dx)
//     }
//   }

//   override def draw() = {
//     // image.floatBuffer.put(field.data.map(_.mag))
//     // texture.update
//     // quad.draw

//     mesh.clear
//     for( x <- 0 until w; y <- 0 until h) yield {
//       val c = Vec3(field.getCenter(x,y) - Vec2(field.nx*field.dx/2))
//       mesh.vertices += c
//       val v = field(x,y)
//       mesh.vertices += c + Vec3(v.normalized * (0.5f+v.mag*0.5f) * field.dx)
//     }
//     mesh.update
//     model.draw
//   }

// }

// class Emitter(var maxParticles:Int) extends Animatable {
//   import com.fishuyo.seer.particle._

//   var ttl = 20f
//   var particles = collection.mutable.ListBuffer[Particle]()

//   val mesh = Mesh()
//   mesh.maxVertices = maxParticles
//   mesh.primitive = Points
//   val model = Model(mesh)

//   val sphere = Sphere().scale(0.005)
//   sphere.material.transparent = true
//   sphere.material.color.set(0.4,0.4,0.4,0.4)

//   var field:Option[spatial.VecField2D] = None
//   var fieldAsForce = false
//   var xt = 0f

//   var damping = 0f

//   def +=(p:Particle) = particles += p
//   def ++=(ps:Seq[Particle]) = particles ++= ps
//   def addParticle(p:Particle) = particles += p
//   def clear() = particles.clear

//   override def draw(){
//     // mesh.clear
//     // mesh.vertices ++= particles.map( _.position )
//     // mesh.update
//     // model.draw()
//     particles.foreach { case p =>
//       sphere.pose.pos.set(p.position)
//       sphere.draw
//     }
//   }

//   override def animate(dt:Float){

//     val timeStep = Integrators.dt
//     if(ttl > 0) particles = particles.filter( (p) => p.t < ttl )
//     if( particles.length > maxParticles ) particles = particles.takeRight(maxParticles)

//     val steps = ( (dt+xt) / timeStep ).toInt
//     xt += dt - steps * timeStep

//     for( t <- (0 until steps)){

//       particles.foreach( (p) => {
//         // p.applyGravity()
//         p.applyDamping(damping)
//         if(field.isDefined){
//           val v = Vec3(field.get(p.position.xy))
//           if(fieldAsForce) p.applyForce(v)
//           else p.setVelocity(v*1.0f)
//         }

//         p.step() // timeStep
//         // p.collideGround(-1f, 0.999999f) 
//       })
//     }

//   }
// }