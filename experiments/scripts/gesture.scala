
import com.fishuyo.seer._
import dynamic._
import graphics._
import maths._
import util._
import io._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL10

import collection.mutable.ListBuffer
import collection.mutable.ArrayBuffer
import collection.mutable.HashMap


class Gesture extends Animatable {
	val buffer = new RingBuffer[(Vec3,Float)](100)

	val mesh = new Mesh()
	// mesh.primitive = LineStrip
	mesh.primitive = TriangleStrip
	mesh.maxVertices = 1000
	val model = Model(mesh)
	model.material = Material.basic

	var jump = Vec3(0)

	def setJump = {
		if( buffer.length > 0) jump = (buffer.last._1 - buffer.head._1)
		// else jump = Vec3(0)
	}

	def +=(p:Vec3){
		def v2width(vel:Float) = {
			var v = vel
			if( v == -1.f) v = 0.f
			val scale = 18.f
			val size = 10
	    val minP = 0.02f
	    val oldP = if(buffer.length > 0) buffer.last._2 else 0.f
	    ((minP + size*math.max(0.f, 1.f - v/scale)) + (4.f*oldP))*1.f/5.f
			// if( v == -1.f) 5.f 
			// else 100.f/(v*v) + .1f
		}
		var v = -1.f
		if(buffer.length > 0) v = (buffer.last._1 - p).mag
		val w = v2width(v)
		if(v == -1.f || v > 0.1f) buffer += (p,w)
	}

	def ribbonize(line:Seq[Vec3], width:Seq[Float]) = {
		def findNorm(vs:Seq[Vec3]) = vs match {
			case v0::v1::v2::Nil => 
				val (df,db) = (v2-v1, v1-v0)
				val d1 = df+db
				val binorm = (db cross df).normalize
				val norm = (d1 cross binorm).normalize
				norm
			case _ => Vec3(0)
		}

		val vert = ArrayBuffer[Vec3]()
		if( line.length >= 3){
			val norm = findNorm(line.take(3))
			vert += line.head + norm * width.head
			vert += line.head - norm * width.head
		}

		var pn = Vec3(0)
		line.sliding(3).toList.zip(width.tail).foreach{
			case (vs@(v0::v1::v2::Nil),w) => 
				val norm = findNorm(vs)
				val nd = pn dot norm
				if( nd < 0.f) norm *= -1
				pn = norm
				vert += v1 + norm * w
				vert += v1 - norm * w
			case _ => ()
		}
		if( line.length >= 3){
			val norm = findNorm(line.takeRight(3))
			vert += line.last + norm * width.last
			vert += line.last - norm * width.last
		}
		vert
	}

	override def draw() = {
		mesh.primitive = TriangleStrip
		mesh.clear
		val vert = ribbonize(buffer.map(_._1), buffer.map(_._2))
		mesh.vertices ++= vert 
		// mesh.vertices ++= buffer.map( (v)=> Vec3(v._1.x,v._1.y,0))
		mesh.update
		model.material.color.set(1,1,1,1)
		model.draw
		MatrixStack.push
		MatrixStack.translate(Window.width,0,0)
		model.draw
		MatrixStack.translate(-2*Window.width,0,0)
		model.draw
		MatrixStack.translate(Window.width,Window.height,0)
		model.draw
		MatrixStack.translate(0,-2*Window.height,0)
		model.draw
		MatrixStack.pop

		// mesh.primitive = Points
		// model.material.color.set(1,0,0,1)
		// model.draw
				// for(i <- 0 until buf.length){
		// 	val c = circles(i)
		// 	val (p,v) = buf(i)
		// 	c.pose.pos.set(p)
		// 	c.scale.set(1/(v+1)) //f/(0+1))
		// 	c.draw
		// }
	}

	override def animate(dt:Float){
		if(buffer.length > 1){		
			val h = buffer.next
			var p = h._1 + jump
			var shift = Vec3()
			if( buffer.head._1.x < -Window.width/2) shift.x += Window.width
			else if( buffer.head._1.x > Window.width/2) shift.x -= Window.width
			if( buffer.head._1.y < -Window.height/2) shift.y += Window.height
			else if( buffer.head._1.y > Window.height/2) shift.y -= Window.height
			buffer += (p, h._2)
			buffer.foreach( _._1 += shift)
		}
	}

}

object Script extends SeerScript {

	implicit def f2i(f:Float) = f.toInt

	var t = 0.f
	val buf = new RingBuffer[(Vec3,Float)](100)

	val joints = HashMap[String,Gesture]()
	val gestures = ListBuffer[Gesture]()
	var gest:Option[Gesture] = None

	Shader.bg = RGB.black
	SceneGraph.root.camera = new OrthographicCamera(800,800)
	SceneGraph.root.camera.nav.pos.set(0,0,1)

	val circles = for(i <- 0 to 100) yield {
		val c = Circle().translate(i/2.f,0,0).scale(0.5f)
		c.material = Material.basic
		c.material.color = HSV(0,0,0)
		c
	}

	Mouse.clear
	Mouse.use
	Mouse.bind("down", (i) => gest = Some(new Gesture ))
	Mouse.bind("up", (i) =>{ 
		if(gest.isDefined && gest.get.buffer.length > 0 ) gestures += gest.get;
		gest.get.setJump
		gest = None
	})
	Mouse.bind("drag", (i) =>{ 
		val r = SceneGraph.root.camera.ray(Mouse.x()*Window.width, (1.f-Mouse.y()) * Window.height)
		val t = r.intersectQuad(Vec3(0),Window.width,Window.height,Camera.nav.quat)
		if(t.isDefined){
			val p = r(t.get)
			var v = -1.f
			gest.get += p
		}
	})

	Keyboard.clear
	Keyboard.use
	Keyboard.bind(" ", () => gestures.clear)
	var record = false
	Keyboard.bind("r", () => {record = !record; println(record)})

	// OSC.clear()
	// OSC.disconnect()
	// OSC.listen(7110)

	// OSC.bind("/new_user", (f)=>{ println("new user"); Main.skeletons.apply(f[0]).calibrating(true) })
	// OSC.bind("/user/1", lambda{|f| Main.skeletons.apply(0).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
	// OSC.bind("/user/2", lambda{|f| Main.skeletons.apply(1).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
	// OSC.bind("/user/3", lambda{|f| Main.skeletons.apply(2).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
	// OSC.bind("/user/4", lambda{|f| Main.skeletons.apply(3).loadingModel.pose.pos.set(2*f[0]-1,1-f[1],f[2]) })
	// OSC.bind("/new_skel", lambda{|f| puts "calibrated"; s = Main.skeletons.apply(f[0]); s.calibrating(false); s.tracking(true) })
	// OSC.bind("/lost_user", lambda{|f| puts "lost user"; s = Main.skeletons.apply(f[0]); s.calibrating(false); s.tracking(false) })
	
	OSC.bind("/joint", (f) => {
		val id = f(1).asInstanceOf[Int]
		val name = f(0).asInstanceOf[String]

		val z = f(4).asInstanceOf[Float]
		val pos = Vec3(400.f*(f(2).asInstanceOf[Float]-.5f), 800.f*(1.f-f(3).asInstanceOf[Float]), 0.f) 

		name match {
			case "l_hand" | "r_hand" =>
				if( record ){
					if(!joints.isDefinedAt(name)) joints(name) = new Gesture
					joints(name) += pos
				}else{
					if(joints.isDefinedAt(name)){
						val g = joints(name)
						if(g.buffer.length > 0 ) gestures += g
						g.setJump
						joints -= name
					}
				}
			case _ => ()
		}
	})

	override def draw(){
		Gdx.gl.glLineWidth(2)
		
		// println(gestures(0).mesh.vertices)


		if(gest.isDefined) gest.get.draw 
		gestures.foreach( _.draw )
		joints.values.foreach( _.draw )
	}

	override def animate(dt:Float){
		t += dt
		gestures.foreach( _.animate(dt))
	}

}

	// Run(()=>{
	// val blur = new RenderNode
 //  blur.shader = "composite"
 //  blur.clear = false
 //  val quag = new Drawable {
 //    val m = Mesh(Primitive2D.quad)
 //    override def draw(){
 //      // Shader("composite").setUniformf("u_blend0", 1.0f)
 //      // Shader("composite").setUniformf("u_blend1", 1.0f)
 //      // Shader("composite").setUniformMatrix("u_projectionViewMatrix", new Matrix4())
 //      m.draw()
 //    }
 //  }
 //  blur.scene.push( quag )
 //  SceneGraph.root.outputTo(blur)
 //  blur.outputTo(blur)
 //  blur.outputTo(ScreenNode)
	// })
Script