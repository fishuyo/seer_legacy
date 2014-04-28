
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import spatial._
import io._
import util._
import audio._
import gen.Sine
import gen.Env

import collection.mutable.ListBuffer


class Agent(val body:Model) {
	body.material = Material.basic

	val nav = Nav()
	val pos0 = Vec3(body.pose.pos)
	nav.set( body.pose)

	val map = automapper(0,1)
	var t = 0.f

	val s = new Sine(new Single(0), new Single(0))
	var phase = 0.f
	var freq = 0.1f

	def draw(){ body.draw }
	
	def step(dt:Float){
		t += dt

		val p = nav.pos - Script.c.pose.pos
		// val p = nav.pos - Camera.nav.pos
		val d = p.magSq
		if( d < 10.f + Camera.nav.vel.mag ){
			val amnt = 5.f - d + Camera.nav.vel.mag
			nav.vel = -p.normalized * amnt + Random.vec3()*amnt
			t = 0.f
			freq = amnt*0.1
			s.f(amnt*400.f+40.f)
			s.a(0.1)
			// if(s.amp.value == 0.f) s.a(Env.decay(2.f))
		} else{
			nav.vel *= 0.95
		}
		val v = map(nav.vel.mag)
		body.material.color = HSV(0.5f+v,1,1-v)
		val r = Random.float()
		if( t > 5.f + r){
			nav.pos.lerpTo(pos0,0.01f)
			val d = (pos0-nav.pos).mag
			if( d > 0.5) body.material.color = HSV(0.11,0.5+r*0.5,1)
			else body.material.color = HSV(0.5f+d,1,1)
		}


		nav.step(dt)

		body.pose = nav
		body.pose.quat = Camera.nav.quat

	}

}

object Script extends SeerScript {

	val c = Cube().scale(0.5)
	c.material = Material.specular
	// c.material.color.set(1,0,1)

	Shader.bg = RGB.black

	val h = HSV(0,1,1)
	val n = 5
	val s = 1.f
	val as = for(i<-(-n until n); j<-(-n until n); k<-(-n until n)) yield new Agent(Circle().translate(s*i,s*j,s*k).scale(0.1))

	val buf = ListBuffer[Vec2]()

	var theta = 0.f
	val osc = new Sine(new Single(140), new Single(1))

	// Audio.start
	Mouse.use
	// rx.Obs(Mouse.scroll){ c.scale(1.f+0.001f*Mouse.scroll()) }
	rx.Obs(Mouse.scroll){ 
		c.translate((Camera.nav.pos-c.pose.pos)*Mouse.scroll()*0.001f)
		h *= HSV(0.001f*Mouse.scroll(),1,1)
		c.material.color = h
	}

	override def draw(){
		Scene.alpha = 1
		theta += 0.001f
    MatrixStack.rotate(0.f,0.f,0.f)//theta,0.f)
		c.draw()
		as.foreach(_.draw)
	}

	override def animate(dt:Float){
		implicit def f2i(f:Float) = f.toInt

		if( Mouse.status() == "drag"){
			buf += Mouse.xy()
			val r = Camera.ray(Mouse.x()*Window.width, (1-Mouse.y()) * Window.height)
			val t = r.intersectQuad(c.pose.pos, 100,100,Camera.nav.quat)
			if(t.isDefined){
				val p = r(t.get)
				c.pose.pos.set(p)
			}
		}
		c.rotate(0,0.01,0)

		as.foreach(_.step(dt))
	}

  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){

	  as.foreach( (a) => {
  		for(i <- ( 0 until numSamples)){
  			a.phase += a.freq
  			a.phase %= 2*Pi
  			val s = math.sin(a.phase)/as.length
  			// val s = a.s()/as.length
				out(0)(i) += s
				out(1)(i) += s
			}
  	})

  }

}
Script
