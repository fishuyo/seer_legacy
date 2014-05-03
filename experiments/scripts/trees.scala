
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import trees._
import allosphere._

var move = 0
var mx=0.0
var my=0.0
var mz=0.0 
var rz=0.0
var rx=0.0
var ry=0.0
var mx1=0.0
var my1=0.0
var mz1=1.0

object Script extends SeerScript {


	val n = 5
	val ss = for(i <- -n to n; j<- -n to n; k <- -n to n) yield Sphere().translate(Vec3(i,j,k)*0.5)
	val tree = Tree()
	tree.branch()

	val depth = 7
	tree.setAnimate(true)
	tree.setReseed(true)
	tree.setDepth(depth)
	tree.branch(depth)

	override def draw(){
		// tree.draw
		Omni.draw
	}

	override def animate(dt:Float){
		tree.animate(dt)
		FPS.print
	}

}

Trackpad.clear()
Trackpad.connect()
Trackpad.bind( (i,f) => {
	val t = Script.tree

	i match {
		case 1 =>
			val ur = Vec3(1,0,0) //Camera.nav.ur()
			val uf = Vec3(0,0,1) //Camera.nav.uf()

			t.root.applyForce( ur*(f(0)-0.5) * 2.0*f(4) )
			t.root.applyForce( uf*(f(1)-0.5) * -2.0*f(4) )
		case 2 =>
			mx += f(2)*0.05  
			my += f(3)*0.05
		case 3 =>
			ry = ry + f(2)*0.05  
			mz = mz + f(3)*0.01
			if (mz < 0.08) mz = 0.08
			if (mz > 3.0) mz = 3.0 
		case 4 =>
			rz = rz + f(3)*0.05
			rx = rx + f(2)*0.05
		case _ => ()
	}

  // t.root.pose.pos.set(mx,my,0)

	if(i > 2){
		t.bAngle.y.setMinMax( 0.05, ry,false )
		// ##t.bAngle.y.set(mx)
	    t.sRatio.setMinMax( 0.05, mz, false )
	    // ## t.sRatio.set( mz )
	    t.bRatio.setMinMax( 0.05, mz, false )
	    // ##t.bRatio.set( my )
	    t.sAngle.x.setMinMax( 0.05, rx, false )
	    t.bAngle.x.setMinMax( 0.05, rx, false )
	    // ##t.sAngle.x.set( rx )
	    t.sAngle.z.setMinMax( 0.05, rz, false )
	    t.bAngle.z.setMinMax( 0.05, rz, false )
	    // ##t.sAngle.z.set( rz )
	    // ##t.branch(depth)
	    t.refresh()

	    // # t.root.accel.zero
	    // # t.root.euler.zero
	}
})


object Omni extends Animatable with OmniDrawable {

	val omni = new OmniStereo
	var omniEnabled = true

	val lens = new Lens()
	lens.near = 0.01
	lens.far = 40.0
	lens.eyeSep = 0.03

	var omniShader:Shader = _

  var mode = "omni"

  Camera.nav.pos.set(0,1,0)

	// omni.mStereo = 1
	// omni.mMode = omni.StereoMode.ACTIVE

	override def init(){
    if( omniShader == null){
      omniShader = Shader.load("omni", OmniStereo.glsl + OmniApp.vert, OmniApp.frag )
      omni.onCreate
      omni.configure("../seer-allosphere/calibration","gr02")
    }		
	}

	override def draw(){
		
		if( omniShader == null){ init()}
		val vp = Viewport(Window.width, Window.height)

		// omni.drawWarp(vp)
		// omni.drawDemo(lens,Camera.nav,vp)

		// onDrawOmni()

		// omni.drawSphereMap(t, lens, Camera.nav, vp)

		if (omniEnabled) {
			omni.onFrame(this, lens, Camera.nav, vp);
		} else {
			// omni.onFrameFront(this, lens, Camera.nav, vp);
		}
	}

	override def onDrawOmni(){
		Shader("omni").begin
		omni.uniforms(omniShader);

		// Script.tree.draw
		Script.ss.foreach( _.draw)
		
		Shader("omni").end
	}

}


Script
