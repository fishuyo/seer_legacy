
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import allosphere._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

Shader.bg.set(0.,0.,0.,1)

object Script extends SeerScript {

	val path1 = "../../MAT/andreou/data_2013-08-09/positive/1.txt"
	val path2 = "../../MAT/andreou/data_2013-08-09/negative/3.txt"

	// parse data
	val pos1 = new DataSet(path1)
	val neg3 = new DataSet(path2)
	val diff = pos1 - neg3
	pos1.shader = "omni"
	neg3.shader = "omni"
	diff.shader = "omni"
	pos1.offset.set(0,1.6,0)
	neg3.offset.set(0,0,0)
	diff.offset.set(0,0.8,0)

	pos1.generateMeshes
	neg3.generateMeshes

	var moveCamera=false

	// val coord = Mesh()
	// coord.primitive = Lines
	// coord.vertices += Vec3()
	// coord.vertices += Vec3(1,0,0)
	// coord.vertices += Vec3()
	// coord.vertices += Vec3(0,0.5,0)
	// coord.vertices += Vec3()
	// coord.vertices += Vec3(0,0,0.25)
	// val cM = Model(coord)
	// cM.shader ="s1"

	// println(D.ys)

	override def draw(){
		//reverse to draw back to front		
		// pos1.draw
		// neg3.draw
		// diff.draw
		
		// cM.draw
		Omni.draw
	}

	override def animate(dt:Float){
		pos1.animate(dt)
		neg3.animate(dt)
		diff.animate(dt)

		// cM.pose.pos.set(Camera.nav.pos+Camera.nav.uf()+Vec3(0,-0.5,0))

		if(moveCamera){
			Camera.nav.pos.lerpTo(newPos,speed)
			Camera.nav.quat.slerpTo(newQuat,speed)
		}
		if( (Camera.nav.pos - newPos).mag < 0.05f){
			moveCamera = false
		}
	}

	var viewX = 0
	var viewY = 0
	var speed = 0.1
	var newPos = Vec3(0,1,2)
	var newQuat = Quat.forward

	def updateCamera(){
		if( D.mode == "x"){
			viewX %= D.xs.size
			if(viewX < 0) viewX = D.xs.size
			newPos = Vec3(D.xshift.x,0,0) * D.xs(viewX) + Vec3(0.f,1,2)
			newQuat = Quat.forward
		} else{
			viewY %= D.ys.size
			if(viewY < 0) viewY = D.ys.size
			newPos = Vec3(0,0,D.yshift.y) * D.ys(viewY) + Vec3(2.f,1,0)
			newQuat = Quat.right
		}
		moveCamera = true
	}

	Keyboard.clear
	Keyboard.use
	Keyboard.bind("g", ()=>{ 
		if(D.mode == "x") D.mode = "y"
		else D.mode = "x"
		viewX = 0
		viewY = 0
		speed = 0.05
		updateCamera()
	})

	Touch.clear()
	Touch.use()
	Touch.bind("fling", (button,v) => {
		val thresh = 500
		if(v(0) > thresh){
			viewX -= 1; viewY -= 1
		} else if(v(0) < -thresh){
			viewX += 1; viewY += 1
		}
		speed = 0.1
		updateCamera()
	})

	// Trackpad.clear
	// Trackpad.connect
	// Trackpad.bind {
	// 	case (1,f) =>
	// 	case (3,f) => D.xshift.y += f(3)*0.00025f; if(D.xshift.y < 0.f) D.xshift.y = 0.f
	// 								D.yshift.x -= f(3)*0.00005f; if(D.yshift.x > 0.f) D.yshift.x = 0.f
	// 	case _ => ()
	// }

}

Camera.nav.pos.set(0,0,0)
Camera.nav.quat.set(1,0,0,0)
Scene.alpha = .5
SceneGraph.root.depth = false
Run(()=>{ S.shaders("s1") = Shader.load("s1",S.vert,S.frag1)})
Run(()=>{ S.shaders("r") = Shader.load("r",S.vert,S.r)})
Run(()=>{ S.shaders("g") = Shader.load("g",S.vert,S.g)})
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE) })
Run(()=>{ Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA) })


object D {
	var mode = "x"
	var freq:List[Float] = null
	var pos:List[Vec2] = null
	var xs:List[Float] = null
	var ys:List[Float] = null

	var fmin = Float.MaxValue
	var fmax = Float.MinValue
	var ampmin = Float.MaxValue
	var ampmax = Float.MinValue

	val xshift = Vec3(0.05f,0.001f,0.05f)
	val yshift = Vec3(-0.001f,-0.7f,0.05f)

	def updateBounds(f1:Float,f2:Float,a1:Float,a2:Float){
		fmin = math.min(fmin,f1)
		fmax = math.max(fmax,f2)
		ampmin = math.min(ampmin,a1)
		ampmax = math.max(ampmax,a2)
	}
}

class DataSet(val path:String) extends Animatable {

	// parse data
	val (freq,positions,amps) = Parser.readData(path)

	// store freq and positions since they are the same for each data set
	if(D.freq == null) D.freq = freq.toList
	if(D.pos == null){
		D.pos = positions.toList
		D.xs = D.pos.map(_.x).removeDuplicates
		D.ys = D.pos.map(_.y).removeDuplicates
	}

	// calculate bounds for frequency and amplitude values
	val fmin = freq.min
	val fmax = freq.max
	val ampmin = amps.map(_.min).min
	val ampmax = amps.map(_.max).max

	// update bounds for all loaded data sets
	D.updateBounds(fmin,fmax,ampmin,ampmax)

	val offset = Vec3(0)

	val meshes = ArrayBuffer[Mesh]()
	val models = ArrayBuffer[Model]()

	var shader = "s1"

	def generateMeshes(){
		// generate mesh for each spectrum
		amps.foreach((amp) => {
			val m = Mesh()
			m.primitive = LineStrip
			
			freq.zip(amp).foreach { 
				case (f,a) =>
					val x = map(f,D.fmin,D.fmax,-1.f,1.f)
					val y = map(a,D.ampmin,D.ampmax,0.f,1.f)
					m.vertices += Vec3(x,y,0)
			}
			meshes += m
		})
	}

	def -(b:DataSet) = new DiffSet(this,b)

	override def draw(){
		models.foreach(_.draw)
	}

	override def animate(dt:Float){
		if(meshes.isEmpty) return
		models.clear
			//var z = 0.f

		positions.zip(meshes).foreach {
			case (pos,mesh) =>
				val mdl = Model(mesh)
				if(D.mode == "x") mdl.translate(pos.x*D.xshift.x,0,pos.y*D.xshift.y).translate(offset)
				else mdl.rotate(0,Pi/2,0).translate(pos.x*D.yshift.x,0,pos.y*D.yshift.y).translate(offset)
				mdl.shader = shader
				models += mdl
		}
	}
}

class DiffSet(a:DataSet,b:DataSet) extends Animatable {
	val offset = Vec3(0)
	val meshes = ArrayBuffer[Mesh]()
	val models = ArrayBuffer[Model]()
	var shader = "s1"

	a.amps.zip(b.amps).foreach((amps) => {

		val m = Mesh()
		m.primitive = LineStrip

		val diff = amps._1.zip(amps._2).map( (a) => a._1 - a._2)

		a.freq.zip(diff).foreach { 
			case (f,a) =>
				val x = map(f,D.fmin,D.fmax,-1.f,1.f)
				val y = map(a,D.ampmin,D.ampmax,0.f,1.f)
				m.vertices += Vec3(x,y,0)
		}
		meshes += m
	})

	override def draw(){
		models.foreach(_.draw)
	}

	override def animate(dt:Float){
		FPS.print
		if(meshes.isEmpty) return
		models.clear

		a.positions.zip(meshes).foreach {
			case (pos,mesh) =>
				val mdl = Model(mesh)
				if(D.mode == "x") mdl.translate(pos.x*D.xshift.x,0,pos.y*D.xshift.y).translate(offset)
				else mdl.rotate(0,Pi/2,0).translate(pos.x*D.yshift.x,0,pos.y*D.yshift.y).translate(offset)
				mdl.shader = shader
				models += mdl
		}
	}
}

// data parser
object Parser {
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
				positions += Vec2(d(1),d(0)) // first two values xy position
				amps += d.tail.tail // remaining values amplitude
			}
			(freq,positions,amps)

		} catch {
			case e: Exception => println("failed to open file " + path + "\n")
			(null,null,null)
		}

	}

}

// shader code
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
  val vOmni = """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    attribute vec4 a_color;

    uniform mat4 u_projectionViewMatrix;
    uniform mat4 u_modelViewMatrix;

    varying vec4 v_color;
    varying vec2 v_texCoord;
    varying vec3 v_pos;

    void main() {
      gl_Position = omni_render(u_modelViewMatrix * a_position);
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
        gl_FragColor = hsv_to_rgb(hue,1.,1.,0.8); //vec4(1,1,1,0.1);
    }
  """
  val g = frag + """
    void main(){
    		float c = 0.7+v_pos.y;
        gl_FragColor = vec4(0,1,0,0.25);
    }
  """
  val r = frag + """
    void main(){
    		float c = 0.7+v_pos.y;
        gl_FragColor = vec4(1,0,0,0.25);
    }
  """
}

object Omni extends Animatable with OmniDrawable {

	val omni = new OmniStereo
	var omniEnabled = true

	val lens = new Lens()
	lens.near = 0.01
	lens.far = 40.0
	lens.eyeSep = 0.03

	var omniShader:Shader = _

  var mode = "omni"

	// omni.mStereo = 1
	// omni.mMode = omni.StereoMode.ACTIVE

	override def init(){
    if( omniShader == null){
    	// mCubeProgram = Shader.load("cubeProgram",OmniShader.vGeneric, OmniShader.fCube)
			// mWarpProgram = Shader.load("warpProgram",OmniShader.vGeneric, OmniShader.fWarp)
      omniShader = Shader.load("omni", OmniShader.glsl + S.vOmni, S.frag1 )
      // omni.configure("../seer-modules/seer-allosphere/calibration","gr02")
      omni.configure("../../../calibration-current",java.net.InetAddress.getLocalHost().getHostName())
      omni.onCreate

    }		
	}

	override def draw(){
		
		if( omniShader == null){ init()}
		val vp = Viewport(Window.width, Window.height)

		omni.drawWarp(vp)
		// omni.drawDemo(lens,Camera.nav,vp)

		// onDrawOmni()

		// omni.drawSphereMap(t, lens, Camera.nav, vp)

		// if (omniEnabled) {
			// omni.onFrame(this, lens, Camera.nav, vp);
		// } else {
			// omni.onFrameFront(this, lens, Camera.nav, vp);
		// }
	}

	override def onDrawOmni(){
		Shader("omni").begin
		omni.uniforms(omniShader);

		val c = Cube()
		c.scale(10.f)
		c.draw
		// Script.pos1.draw
		// Script.neg3.draw
		// Script.diff.draw
		
		Shader("omni").end
	}

}

Script
