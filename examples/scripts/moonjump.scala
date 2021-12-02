
import seer._

import graphics._
import spatial._
import seer.world.particle._
import io._
import util._
import dynamic._

import de.sciss.osc.Message

import collection.mutable.ArrayBuffer

import scala.concurrent.duration._

class SParticle extends KinematicState with RotationalState {
	var r = 1f
	def step(){
		Integrators.verlet(this)
		Integrators.rotationalVerlet(this)
	}

	def applyGravity( m:SParticle ){
		val x = m.position - position
		val r2 = x.magSq

		// val q = m.orientation * orientation.inverse
		// val r2 = q.magSq
		if( r2 > r){
			val f = 0.01f * mass*m.mass / r2 //- spin.mag * 0.0001
			// val f = mass*m.mass / r2
			applyForce( x.normalized * f )
			// applyTorque( (orientation.slerp(m.orientation, 0.0001)*orientation.inverse * f) )
			// orientation.slerpTo(m.orientation, f)
		}
	}
}

class Entity extends SParticle with Animatable {
	var model:Model = Sphere()
	model.material = Material.specular
	var inited = false
	override def draw() = {
		if(!inited) init()
		model.draw
	}
	override def animate(dt:Float) = {
		step()
		// model.pose.pos.set(position)
		// position = orientation.toZ() * 3f
		// lPosition = position
		position.set( position.normalized * 5f)
		// position.z = 0
		// position.wrap(Vec3(-30,-30,0), Vec3(30,30,0))

		model.pose.pos.set(position)
		model.pose.quat.set(orientation)
  }
  
  var collide = (e:Entity)=>{}
}

object Moon {
	var inited = false
	def init(){
		val material = Material.specular
		// material.loadTexture("/Users/fishuyo/projects/seer/res/img/moon/moon.png")
	}
}
class Moon(size:Float) extends Entity {
	var capturedBy = -1
	r = size
	model.scale(r)
  mass = r*r*r
  
  // collide = (e:Entity)=> {println("moon")}

	override def init() = {
		// model.material.loadTexture("/Users/fishuyo/projects/seer/res/img/moon/moon.png")
		model.material.lightingMix = 0.5f
		model.material.textureMix = 0.5f
		inited = true
	}
}

class Player(var id:Int, var color:Vec3) extends Entity {
	var jumping = false
	var attachedTo = None:Option[Moon]
	var offset = Vec3()
	var angle = 0f
	var groundVelocity = 0f
	var groundAccel = 0f

	model = Sphere();
	r = 0.1f
	mass = 0.001f
	model.scale(r) //.3*r,r,.3*r)
	model.material = Material.specular
	model.material.color = RGBA(color,1f)
	position.set(Random.vec3())
	lPosition.set(position)

	var thrust = Vec3()
	val thrustMesh = new Mesh()
	thrustMesh.primitive = Lines
	thrustMesh.vertices += Vec3()
	thrustMesh.vertices += Vec3()
	val thrustModel = Model(thrustMesh)

	val trail = new Trace3D(500)
	trail.setColors(color, color*0.2)

	collide = (e:Entity) => e match {
    case m:Moon if attachedTo.isEmpty || attachedTo.get != m =>
			val n = (position - m.position).normalize
			val nx = (position cross Vec3(0,1,0)).normalize
			var x = n dot nx //math.sqrt(n.x*n.x+n.z*n.z)
			// if(math.abs(n.x) > 0.000001) x *= -n.x/math.abs(n.x)
			// if(math.abs(n.z) > 0.000001) x *= -n.z/math.abs(n.z)
			angle = math.atan2(n.y,x).toFloat
			groundVelocity = 0f //velocity.dot(-n.cross(velocity.cross(-n)))
			attachedTo = Some(m)
			m.capturedBy = id
			m.model.material.color = model.material.color
			// println("angle: " + angle)
    case _ => ()
  }
  
	override def draw(){
		super.draw()
		thrustModel.draw
		trail.draw	
	}
	override def animate(dt:Float) = {
		doJump()

		val tx = (position cross Vec3(0,1,0)).normalize
		val t = (tx * thrust.x + Vec3(0,1,0) * thrust.y)
		val f = -t*0.001

		if( attachedTo.isDefined ){
			val m = attachedTo.get
			groundVelocity += groundAccel
			groundAccel = 0f
			groundVelocity *= 0.93f
			angle += groundVelocity * dt / m.r
			val p = (tx*math.cos(angle) + Vec3(0,1,0)*math.sin(angle)) * (r+m.r)
			position.set(m.position+p)
			model.pose.pos.set(position)
			m.applyForce(f)
		}else{ 
			super.animate(dt);

			// val pos = position
			// val tx = -(pos cross Vec3(0,1,0)).normalize
			// val ty = -(pos cross Vec3(1,0,0)).normalize
			// val t = (tx * thrust.x + ty * thrust.y)
			applyForce(f)
		}

		thrustMesh.vertices(1).set(t)
		thrustMesh.update
		thrustModel.pose.pos.set(position)
		thrust.zero

		trail(position)
	}

	def run(f:Float){ groundAccel = f}
	def jump() = if(attachedTo.isDefined) jumping = true
	def doJump(){
		if(!jumping || attachedTo.isEmpty) return
		val m = attachedTo.get
		val n = (position - m.position).normalize
		val t = (-m.position cross n).normalize

		velocity = m.velocity + n * 0.03f + t * groundVelocity*0.015f
		lPosition = position - velocity
		attachedTo = None
		jumping = false
	}
}



class MoonScript extends SeerActor {
	
	Camera.nav.pos.set(0,0,0)
	Camera.nav.quat = Quat()

	Renderer().environment.lightPosition.set(0,0,-1)

	var t = 0f
	var zoom = 0f

	var players = List[Player]()
	var moons = List[Moon]()

	var sun = Sphere()
	sun.material = Material.basic
	sun.material.color = RGB(1,1,0)

	players = new Player(0,Vec3(0,1,1)) :: new Player(1,Vec3(1,0,0)) :: players

	var numMoons = 8
	var playing = true
	var won = false

	makeMoons()

	// override def preUnload(){
		// OSC.disconnect()
	// }

	override def draw(){
		moons.foreach( _.draw )
		players.foreach( _.draw )
		sun.draw
	}
	override def animate(dt:Float){
		t += dt

		applyGravity(players ::: moons)
    checkCollisions(moons)
    checkPlayerCollisions(players, moons)

		moons.foreach( _.animate(dt) )
		players.foreach( _.animate(dt) )

		sun.pose.pos.set(Vec3(math.cos(t*0.01),0f,math.sin(t*0.01)) * 10f)
		Renderer().environment.lightPosition.set(sun.pose.pos)

		// check win condition
		if(playing){
			if(moons.count(_.capturedBy == -1) == 0){
				val winner = moons.groupBy( (m) => m.capturedBy ).map{case (id,ms) => (ms.length,id)}.max._2
				val p = players(winner)
				println(s"player ${p.id} wins")
				playing = false
				won = true
				Schedule.every(0.1 seconds){
					val s = Random.float()*.3f + .1f
					val m = new Moon(s)
					m.position.set(Random.vec3().normalized * 3f)
					m.lPosition.set( m.position - Random.vec3()*0.05f )		
					m.orientation.set(Random.quat())
					m.lOrientation.set(m.orientation.slerp(Random.quat(),0.01f))
					m.model.material.color = RGBA(p.color,1f)
					if(moons.length < 300 && won) moons = m :: moons
				}
				Schedule.after(35 seconds){ reset() }
			}
			// players.foreach { case p =>
			// 	val score = moons.count( _.capturedBy == p.id )
			// 	if(score == moons.length){
			// 		println(s"player ${p.id} wins")
			// 		playing = false
			// 		won = true
			// 		Schedule.every(0.1 seconds){
			// 			val s = Random.float()*.3f + .1f
			// 			val m = new Moon(s)
			// 			m.position.set(Random.vec3().normalized * 3f)
			// 			m.lPosition.set( m.position - Random.vec3()*0.05f )		
			// 			m.orientation.set(Random.quat())
			// 			m.lOrientation.set(m.orientation.slerp(Random.quat(),0.01))
			// 			m.model.material.color = RGBA(p.color,1f)
			// 			if(moons.length < 300 && won) moons = m :: moons
			// 		}
			// 		Schedule.after(35 seconds){ reset() }
			// 	}
			// }
		}
		if(won) players.foreach( _.attachedTo = None)
		// moons(0).position.set(0f,0,-5f)

		val z = 5f + 10f*players(0).velocity.mag() + zoom 
		// Camera.nav.pos.lerpTo( players(0).position + Vec3(0,0,z), 0.25)
		// val nav = Nav()
		// nav.lookAt( players(0).position )
		// Camera.nav.quat.set(nav.quat)
		// println(Camera.nav.uf())
		// println(players(0).position)
	}

	def checkCollisions(ents:Seq[Entity]):Unit = ents match {
		case Nil => return
    case e1 :: tail => 
      tail.foreach { case e2 =>        
				val d = e2.position - e1.position
				val n = d.normalized
				val diff = (e1.r+e2.r) - d.mag()
				if( diff > 0){
					var (m1,m2) = (e1.mass,e2.mass)
					var (u1,u2) = (e1.velocity, e2.velocity)

					e1.position += -d.normalize * diff
					e2.position += d.normalize * diff

					var (a1,a2) = (n.dot(e1.velocity), n.dot(e2.velocity))
					val p = (2.0 * (a1 - a2)) / (m1 + m2)
					e1.velocity += n * -p * m2;
					e2.velocity += n * p * m1;
					// e1.velocity = (u1*((m1-m2)/(m1+m2))) + (u2*((2*m2)/(m1+m2)));
          // e2.velocity = (u2*((m2-m1)/(m1+m2))) + (u1*((2*m1)/(m1+m2)));
					e1.lPosition = e1.position - e1.velocity
					e2.lPosition = e2.position - e2.velocity

					if(playing){
						e1.collide(e2)
						e2.collide(e1)
					}
				}
			}
			checkCollisions(tail)
  }
  def checkPlayerCollisions(ps:Seq[Player], ms:Seq[Moon]) = {
    ps.foreach { case e1 =>
      ms.foreach { case e2 =>
        val d = e2.position - e1.position
        val diff = (e1.r+e2.r) - d.mag()
				if( diff > 0){
					if(playing){
						e1.collide(e2)
					}
				}
      }
    }
  }

	def applyGravity(ents:Seq[Entity]):Unit = ents match {
		case Nil => return
		case e1 :: tail => 
			tail.foreach { case e2 =>
				val d = e2.position - e1.position
				var r2 = d.magSq()
				if(d.mag() > e1.r + e2.r){
					val f = 8f * e1.mass * e2.mass / r2
					e1.applyForce( d.normalize * f)
					e2.applyForce( -d.normalize * f)
				}
			}
			applyGravity(tail)
	}

	def makeMoons(){
		for (i <- 0 until numMoons){
			val s = Random.float()*.5f + .2f
			val m = new Moon(s)
			m.position.set(Random.vec3().normalized * 3f)
			m.lPosition.set( m.position - Random.vec3()*0.1f )		
			m.orientation.set(Random.quat())
			m.lOrientation.set(m.orientation.slerp(Random.quat(),0.01f))
			// m.inertia = s

			moons = m :: moons
		}
	}

	def reset(){
		won = false
		Schedule.clear()
		moons = List()
		Schedule.after(5 seconds){
			playing = true
			makeMoons()
		}
	}

	// OSC.listen(8082)
	// OSC.bindp {
	// 	// case Message("/slider", f:Float, i:Int) => moons(i).position.y = f //println(p + " " + f)
	// 	case Message("/3/xy", x:Float, y:Float) => 
	// 		val tx = x*2-1
	// 		val ty = (y*2-1) * -1
	// 		if(players(0).attachedTo.isDefined) players(0).run(-tx/2)
	// 		else players(0).thrust.set(tx,ty,0)
	// 	case Message("/3/toggle4", v:Float) => players(0).jump()
	// 	case msg => println(msg)
	// }

	Trackpad.clear
	Trackpad.connect
	Trackpad.bind( (t) => {
		t.count match {
			case 1 =>
				players(0).run(-t.pos.x + 0.5f)
			case 2 =>
				val x = t.pos.x*2-1
				val y = t.pos.y*2-1
				players(0).thrust.set(x,y,0)
			case 3 =>
				zoom += t.vel.y * 0.1f
				if( zoom < 0) zoom = 0
				else if( zoom > 50) zoom = 50
			case 4 =>
				players(0).jump()
				// players(0).applyForce(Vec3(x,y,0)*0.1)
				// players(0).applyTorque(Vec3(-y,x,0)*0.00001)
			case _ => ()
		}
	})

}

classOf[MoonScript]