
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

object Script extends SeerScript {

	val path = "../../MAT/andreou/data_2013-08-09/negative/3.txt"

	// parse data
	val (freq,positions,amps) = A.readData(path)

	// calculate bounds for frequency and amplitude values
	val fmin = freq.min
	val fmax = freq.max
	val ampmin = amps.map(_.min).min
	val ampmax = amps.map(_.max).max

	// println( fmin )
	// println( fmax )
	// println( ampmin )
	// println( ampmax )

	val dshift = Vec3(0.001f,0.8f,0.05f)
	val shift = Vec3(0.001f,0.8f,0.05f)

	val meshes = ArrayBuffer[Mesh]()
	val models = ArrayBuffer[Model]()

	// generate mesh for each spectrum
	amps.foreach((amp) => {
		val m = Mesh()
		m.primitive = LineStrip
		
		freq.zip(amp).foreach { 
			case (f,a) =>
				val x = map(f,fmin,fmax,-1.f,1.f)
				val y = map(a,ampmin,ampmax,0.f,1.f)
				m.vertices += Vec3(x,y,0)
		}
		meshes += m
	})

	// group by x coordinate
	val xgroups = positions.zip(meshes).groupBy {
		case (pos,mesh) => pos.x
	}
	val ygroups = positions.zip(meshes).groupBy {
		case (pos,mesh) => pos.y
	}
	val data = positions.zip(meshes)

	var moveCamera=false

	override def draw(){
		//reverse to draw back to front
		models.reverse.foreach(_.draw)
	}

	override def animate(dt:Float){
		models.clear
			//var z = 0.f

		data.foreach {
			case (pos,mesh) =>
				val mdl = Model(mesh).translate(pos.y*shift.y,0,pos.x*shift.x)
				mdl.shader = "s1"
				models += mdl
		}

		// xgroups.values.foreach( (grp) => {
		// 	var z = 0.f

		// 	grp.foreach( { 
		// 		case (pos,mesh) =>
		// 			val mdl = Model(mesh).translate(pos.x*shift.x,0,pos.y*shift.y)
		// 			mdl.shader = "s1"
		// 			models += mdl
		// 			z -= shift.z
		// 	})
		// })

		// ygroups.keys.toList.sorted.foreach( (key) => {
		// 	val grp = ygroups(key)
		// 	var z = 0.f

		// 	grp.foreach( { 
		// 		case (pos,mesh) =>
		// 			val mdl = Model(mesh).translate(z,0,pos.y*shift.y).rotate(0,Pi/2,0)
		// 			// val mdl = Model(mesh).translate(pos.x*shift.x,pos.y*shift.y,z)
		// 			mdl.shader = "s1"
		// 			models += mdl
		// 			z -= shift.z
		// 	})
		// })

		if(moveCamera){
			Camera.nav.pos.lerpTo(newPos,0.1)
		}
		if( (Camera.nav.pos - newPos).mag < 0.01f){
			moveCamera = false
		}
	}

	Touch.clear()
	Touch.use()
	var viewX = 0
	var newPos = Vec3(0,0,2)
	Touch.bind("fling", (button,v) => {
		val thresh = 500
		if(v(0) > thresh)viewX -= 1
		else if(v(0) < -thresh) viewX += 1
		// elsif v(1) > thresh
			// l = l-4
		// elsif v(1) < -thresh
			// l = l+4
		// end
		moveCamera = true
		viewX %= xgroups.size
		if(viewX < 0) viewX = 0
		newPos = Vec3(shift.y,0,0) * xgroups.keys.toList.sorted.apply(viewX) + Vec3(0.f,1,2) //*viewX
	})

	Trackpad.clear
	Trackpad.connect
	Trackpad.bind {
		case (1,f) =>
		case (3,f) => shift.z += f(3)*0.001f
		case _ => ()
	}

}

Camera.nav.pos.set(0,1,2)
Camera.nav.quat.set(1,0,0,0)
Scene.alpha = .5
SceneGraph.root.depth = false
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE) })
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA) })

object S {
  val shaders = Map[String,Shader]()
  val vert = """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;

    varying vec4 v_color;
    varying vec2 v_texCoord;
    varying vec3 v_pos;

    void main() {
      gl_Position = u_projectionViewMatrix * a_position;
      v_texCoord = a_texCoord0;
      v_color = a_color;
      v_pos = a_position.xyz;
    }
  """
  val frag = """
    #ifdef GL_ES
        precision mediump float;
    #endif

    varying vec4 v_color;
    varying vec3 v_pos;
    varying vec2 v_texCoord;
  """
  val frag1 = frag + """

	  vec4 hsv_to_rgb(float h, float s, float v, float a) {
	        float c = v * s;
	        h = mod((h * 6.0), 6.0);
	        float x = c * (1.0 - abs(mod(h, 2.0) - 1.0));
	        vec4 color;

	        if (0.0 <= h && h < 1.0) {
	                color = vec4(c, x, 0.0, a);
	        } else if (1.0 <= h && h < 2.0) {
	                color = vec4(x, c, 0.0, a);
	        } else if (2.0 <= h && h < 3.0) {
	                color = vec4(0.0, c, x, a);
	        } else if (3.0 <= h && h < 4.0) {
	                color = vec4(0.0, x, c, a);
	        } else if (4.0 <= h && h < 5.0) {
	                color = vec4(x, 0.0, c, a);
	        } else if (5.0 <= h && h < 6.0) {
	                color = vec4(c, 0.0, x, a);
	        } else {
	                color = vec4(0.0, 0.0, 0.0, a);
	        }

	        color.rgb += v - c;

	        return color;
		}

    void main(){
    	 	float hue = 0.7 * (1.0-(v_pos.y));
        gl_FragColor = hsv_to_rgb(hue,1.,1.,0.5); //vec4(1,1,1,0.1);
    }
  """
}


// case class D(var pos:Vec2, var amp:Array[Float])

object A{
	def readData(path:String): (Array[Float],ArrayBuffer[Vec2],ArrayBuffer[Array[Float]]) = {
		try{
			val ds = new DataInputStream( new FileInputStream(path))

			// first line is tab seperated frequency values
			val freq = ds.readLine.split('\t').filter(!_.isEmpty).map(_.toFloat)
			
			val positions = ArrayBuffer[Vec2]()
			val amps = ArrayBuffer[Array[Float]]()

			while( ds.available > 0){
				// each additional line is position and amplitude values
				val d = ds.readLine.split('\t').map(_.toFloat)
				positions += Vec2(d(1),d(0)) // first to values xy position
				amps += d.tail.tail // remaining values amplitude
			}
			(freq,positions,amps)

		} catch {
			case e: Exception => println("failed to open file " + path + "\n")
			(null,null,null)
		}

	}

}


Script
