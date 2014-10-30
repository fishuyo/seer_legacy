
package com.fishuyo.seer
package allosphere

import graphics._
import dynamic._
import spatial._
import spatial._
import io._
import util._

import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.math.Matrix4

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.Gdx.{gl20 => gl }

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL43

import java.nio.FloatBuffer
import java.io.DataInputStream
import java.io.FileInputStream

import org.luaj.vm2._
import org.luaj.vm2.lib.jse._


/* AlloSystem OmniStereo port */

trait OmniDrawable  {
	/// Place drawing code here
	def onDrawOmni(){}
}

/// Stereographic mode
object StereoMode extends Enumeration {
	type StereoModeType = Value
	val MONO,
	SEQUENTIAL,		/**< Alternate left/right eye frames */
	ACTIVE,			/**< Active quad-buffered stereo */
	DUAL,			/**< Dual side-by-side stereo */
	ANAGLYPH,		/**< Red (left eye) / cyan (right eye) stereo */
	LEFT_EYE,		/**< Left eye only */
	RIGHT_EYE = Value		/**< Right eye only */
}
import StereoMode._

/// Anaglyph mode
object AnaglyphMode extends Enumeration {
	type AnaglyphModeType = Value
	val RED_BLUE,	/**< */
	RED_GREEN,		/**< */
	RED_CYAN,		/**< */
	BLUE_RED,		/**< */
	GREEN_RED,		/**< */
	CYAN_RED = Value		/**< */
}
import AnaglyphMode._

object WarpMode extends Enumeration {
	type WarpModeType = Value
	val FISHEYE,
	CYLINDER,
	RECT = Value
};
import WarpMode._

object BlendMode extends Enumeration{
	type BlendModeType = Value
	val NOBLEND,
	SOFTEDGE = Value
};
import BlendMode._



// Object to encapsulate rendering omni-stereo worlds via cube-maps:
class OmniStereo(var mResolution:Int=1024, var mMipmap:Boolean=true) {

	type DrawMethod = (Pose,Double) => Unit  // pose, eye

	var mCubeProgram:Shader = null
	var mSphereProgram:Shader = null
	var mWarpProgram:Shader = null
	var mDemoProgram:Shader = null

	// supports up to 4 warps/viewports
	val mProjections = new Array[Projection](4)
	def projection(i:Int) = mProjections(i)

	// val mModelView = 0 //TODO;
	var mClearColor = RGBA(0,0,0,0);

	var mFace = 5
	var mEyeParallax = 0f
	var mNear = 0.1f
	var mFar = 100f
	var mNumProjections = 1
	var mFrame = 0
	var mMode = StereoMode.MONO
	var mStereo = 0
	var mAnaglyphMode = AnaglyphMode.RED_CYAN
	var mFullScreen = false

	var mFbo = 0
	var mRbo = 0

	var mTex = Array(0,0)

	val mQuad = Plane() //.generateMesh()

	val renderFace = Array(true,true,true,true,true,true)

	implicit def f2i(f:Float) = f.toInt


	// default configuration
	for(i<-(0 until mNumProjections)) {
		mProjections(i) = new Projection
		mProjections(i).onCreate()
	}
	configure(WarpMode.FISHEYE)
	configure(BlendMode.SOFTEDGE)
	
	// @resolution should be a power of 2
	def resolution(res:Int) = {
		mResolution = res;
		// force GPU reallocation:
		mFbo = 0;
		mRbo = 0;
		mTex(0) = 0;
		mTex(1) = 0;
		this
	}
	def resolution() = mResolution

	// configure the projections according to files
	def configure(configpath:String, configname:String){

		var path = configpath + "/" + configname + ".lua"

		val L = JsePlatform.standardGlobals();
		try{ 
			val chunk = L.loadfile(path);
			chunk.call()
		} catch { 
			case e:Exception => println(s"configuration $path not found.")
			return
		}

		try{
			val ps = L.get("projections")
			if(ps.get("active").toboolean) mMode = ACTIVE
			if(ps.get("fullscreen").toboolean){ 
				mFullScreen = true
				com.fishuyo.seer.DesktopApp.setFullscreen
			}
			
			mResolution = ps.get("resolution").toint

			mNumProjections = ps.length

			for(i <- 0 until mNumProjections){
				mProjections(i) = new Projection

				println(s"configuring projector $i")
				val p = ps.get(i+1)
				val v = p.get("viewport")
				mProjections(i).mViewport.l = v.get("l").tofloat
				mProjections(i).mViewport.b = v.get("b").tofloat
				mProjections(i).mViewport.w = v.get("w").tofloat
				mProjections(i).mViewport.h = v.get("h").tofloat

				val warpFile = configpath + "/" + p.get("warp").get("file").tostring
				mProjections(i).readWarp(warpFile)
				val blendFile = configpath + "/" + p.get("blend").get("file").tostring
				mProjections(i).readBlend(blendFile)
				val paramFile = configpath + "/" + p.get("params").get("file").tostring
				// mProjections(i).readParameters(paramFile)

				// mProjections(i).position.x = p.get("position").get(1).tofloat
				// mProjections(i).position.y = p.get("position").get(2).tofloat
				// mProjections(i).position.z = p.get("position").get(3).tofloat

			}

		} catch {
			case e:Exception => println(s"error reading configuration $path"); e.printStackTrace
			return
		}

	}

	// configure generatively:
	def configure(wm:WarpModeType, a:Float=2f, f:Float=math.Pi) = {
		mNumProjections = 1
		val p = mProjections(0)
		wm match {
			case FISHEYE =>
				WarpBlendGen.fovy = f;
				WarpBlendGen.aspect = a;
				WarpBlendGen.fillFishEye(p.mWarp.data,p.mWarp.w,p.mWarp.h)
				p.mWarp.bind(0)
				p.mWarp.update
				// WarpBlendGen.fillFishEye(p.pWarp)
				// p.warp().draw(p.pWarp,0,0)				
			case CYLINDER =>
				WarpBlendGen.fovy = f / math.Pi;
				WarpBlendGen.aspect = a;
				// p.warp().array().fill(fillCylinder);
			case _ =>
				WarpBlendGen.fovy = f / 2.0;
				WarpBlendGen.aspect = a;
				// p.warp().array().fill(fillRect);
		}
		this
	}

	def configure(bm:BlendModeType) = {
		mNumProjections = 1;
		val p = mProjections(0)
		bm match { // TODO
			case SOFTEDGE =>
				WarpBlendGen.fillSoftEdge(p.pBlend)
				p.blend().draw(p.pBlend,0,0)
			case _ => ()
				p.pBlend.setColor(1,1,1,1)
				p.pBlend.fill()
				p.blend().draw(p.pBlend,0,0)
		}
		this
	}

	// typically they would be combined like this:
	def onFrame(drawable:OmniDrawable, lens:Lens, pose:Pose, vp:Viewport) {
		capture(drawable, lens, pose);
		draw(lens, pose, vp);
	}

	// send the proper uniforms to the shader:
	def uniforms(program:Shader){
		program.uniforms("omni_face") = mFace
		program.uniforms("omni_eye") = mEyeParallax
		program.uniforms("omni_near") = mNear
		program.uniforms("omni_far") = mFar
		program.setUniforms()
		// gl.error("sending OmniStereo uniforms");
	}


	def drawStereo(f:DrawMethod)(lens:Lens, pose:Pose, vp:Viewport) {

		val eye = lens.eyeSep;
		mMode match {
			case SEQUENTIAL =>
				if ((mFrame & 1) > 0) {
					f(pose, eye);
				} else {
					f(pose, -eye);
				}

			case ACTIVE =>
				GL11.glDrawBuffer(GL11.GL_BACK_RIGHT);
				f(pose, eye);

				GL11.glDrawBuffer(GL11.GL_BACK_LEFT);
				f(pose, -eye);

				GL11.glDrawBuffer(GL11.GL_BACK);

			case DUAL =>
				gl.glViewport(vp.l + vp.w*0.5, vp.b, vp.w*0.5, vp.h);
				f(pose, eye);
				gl.glViewport(vp.l, vp.b, vp.w*0.5, vp.h);
				f(pose, -eye);

			case ANAGLYPH =>
				mAnaglyphMode match {
					case RED_BLUE |
							 RED_GREEN |
								RED_CYAN =>	gl.glColorMask(true, false, false, true)
					case BLUE_RED =>	gl.glColorMask(false,false,true, true)
					case GREEN_RED =>	gl.glColorMask(false,true, false,true)
					case CYAN_RED =>	gl.glColorMask(false,true, true, true)
					case _ =>		gl.glColorMask(true, true, true ,true)
				}
				f(pose, eye);

				mAnaglyphMode match {
					case RED_BLUE =>	gl.glColorMask(false,false,true, true)
					case RED_GREEN => gl.glColorMask(false,true, false,true)
					case RED_CYAN =>	gl.glColorMask(false,true, true, true)
					case BLUE_RED |
							 GREEN_RED |
							 CYAN_RED =>	gl.glColorMask(true, false,false,true)
					case _ =>		gl.glColorMask(true, true ,true, true)
				}
				// clear depth before this pass:
				gl.glDepthMask(true);
				gl.glEnable(GL20.GL_DEPTH_TEST)
				gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
				f(pose, -eye);

			case RIGHT_EYE =>
				f(pose, eye);

			case LEFT_EYE =>
				f(pose, -eye);

			case _ =>
				f(pose, 0);
		}
	}

	def onCreate() {

		mCubeProgram = Shader.load("cubeProgram",OmniShader.vGeneric, OmniShader.fCube)
		mCubeProgram.uniforms("alphaMap") = 2
		mCubeProgram.uniforms("pixelMap") = 1
		mCubeProgram.uniforms("cubeMap") = 0

		mSphereProgram = Shader.load("sphereProgram",OmniShader.vGeneric, OmniShader.fSphere)
		mSphereProgram.uniforms("alphaMap") = 2
		mSphereProgram.uniforms("pixelMap") = 1
		mSphereProgram.uniforms("sphereMap") = 0

		mWarpProgram = Shader.load("warpProgram",OmniShader.vGeneric, OmniShader.fWarp)
		mWarpProgram.uniforms("alphaMap") = 2
		mWarpProgram.uniforms("pixelMap") = 1

		mDemoProgram = Shader.load("demoProgram",OmniShader.vGeneric, OmniShader.fDemo)
		mDemoProgram.uniforms("alphaMap") = 2
		mDemoProgram.uniforms("pixelMap") = 1

		// create cubemap textures:
		val b = BufferUtils.newIntBuffer(2)
		gl.glGenTextures(2,b)
		mTex(0) = b.get
		mTex(1) = b.get

		for (i<-(0 until 2)) {
			// create cubemap texture:
			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, mTex(i));

			// each cube face should clamp at texture edges:
			gl.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE);

			// filtering
			gl.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
			gl.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR);

			// TODO: verify?
			// Domagoj also has:
			GL11.glTexGeni( GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR );
			GL11.glTexGeni( GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR );
			GL11.glTexGeni( GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR );
			
			// float X[4] = { 1,0,0,0 };
			// float Y[4] = { 0,1,0,0 };
			// float Z[4] = { 0,0,1,0 };
			val buf = BufferUtils.newFloatBuffer(4)
			var array = Array(1f,0f,0f,0f)
			buf.put(array)
			buf.rewind
			GL11.glTexGen( GL11.GL_S, GL11.GL_OBJECT_PLANE, buf );
			array = Array(0f,1f,0f,0f)
			buf.put(array)
			buf.rewind
			GL11.glTexGen( GL11.GL_T, GL11.GL_OBJECT_PLANE, buf );
			array = Array(0f,0f,1f,0f)
			buf.put(array)
			buf.rewind
			GL11.glTexGen( GL11.GL_R, GL11.GL_OBJECT_PLANE, buf );

			// RGBA8 Cubemap texture, 24 bit depth texture, mResolution x mResolution
			// NULL means reserve texture memory, but texels are undefined
			for (f<-(0 until 6)){
				gl.glTexImage2D(
					GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X+f,
					0, GL11.GL_RGBA8,
					mResolution, mResolution,
					0, GL12.GL_BGRA, GL20.GL_UNSIGNED_BYTE,
					null);
			}

			// clean up:
			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
			//Graphics.error("creating cubemap texture");
		}

		// one FBO to rule them all...
		b.rewind
		gl.glGenFramebuffers(1, b);
		mFbo = b.get()

		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, mFbo);
		//Attach one of the faces of the Cubemap texture to this FBO
		gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, mTex(0), 0);

		b.rewind;
		gl.glGenRenderbuffers(1, b);
		mRbo = b.get()

		gl.glBindRenderbuffer(GL43.GL_RENDERBUFFER, mRbo);
		gl.glRenderbufferStorage(GL43.GL_RENDERBUFFER,GL30.GL_DEPTH_COMPONENT24, mResolution, mResolution);
		// Attach depth buffer to FBO
		gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL43.GL_RENDERBUFFER, mRbo);

		// ...and in the darkness bind them:
		for (mFace <- (0 until 6)){
			gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0+mFace, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X+mFace, mTex(0), 0);
		}

		//Does the GPU support current FBO configuration?
		var status = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
		if (status != GL20.GL_FRAMEBUFFER_COMPLETE) {
			printf("GPU does not support required FBO configuration!\n");
			// exit(0);
		}

		// cleanup:
		gl.glBindRenderbuffer(GL43.GL_RENDERBUFFER, 0);
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

		// Graphics.error("OmniStereo onCreate");
	}

	def onDestroy() {
		mCubeProgram.program.get.dispose()

		val b = BufferUtils.newIntBuffer(2)
		b.put(mTex)
		gl.glDeleteTextures(2, b);
		mTex(0) = 0
		mTex(1) = 0

		b.rewind; b.put(mRbo)
		gl.glDeleteRenderbuffers(1, b);
		b.rewind; b.put(mFbo)
		gl.glDeleteFramebuffers(1, b);
		mRbo = 0;
		mFbo = 0;
	}

	def capture(drawable:OmniDrawable, lens:Lens, pose:Pose) {
		if (mCubeProgram == null) onCreate();
		// gl.error("OmniStereo capture begin");

		val pos = pose.pos;
		// Vec3d ux, uy, uz;
		// pose.unitVectors(ux, uy, uz);
		// mModelView = Matrix4.lookAt(ux, uy, uz, pos);

		mNear = lens.near
		mFar = lens.far
		val eyeSep = (if(mStereo==1) lens.eyeSep else 0.0)

		// gl.projection(Matrix4.identity());
		// val cam = new OrthographicCamera(1,1)
		// Shader.setCamera(cam) 

		// apply camera transform:
		// gl.pushMatrix(gl.MODELVIEW);
		// gl.loadMatrix(mModelView);
		// glPushAttrib(GL_ALL_ATTRIB_BITS);
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, mFbo);
		gl.glViewport(0,0,mResolution, mResolution);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(0,0,mResolution, mResolution);


		for (i <-(0 until (mStereo+1))) {
			mEyeParallax = eyeSep * (i-0.5);
			for (face <- (0 until 6)) {
				if(renderFace(face)){
					mFace = face

					GL11.glDrawBuffer(GL20.GL_COLOR_ATTACHMENT0 + mFace);
					gl.glFramebufferTexture2D(
						GL20.GL_FRAMEBUFFER,
						GL20.GL_COLOR_ATTACHMENT0 + mFace,
						GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + mFace,
						mTex(i), 0);

					gl.glClearColor(mClearColor.r,mClearColor.g,mClearColor.b,mClearColor.a);
					gl.glEnable(GL20.GL_DEPTH_TEST)
					gl.glDepthMask(true);
					gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
					drawable.onDrawOmni();
				}
			}
		}

		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);
		// glPopAttrib()
		// glPopMatrix(gl.MODELVIEW)
		// gl.error("OmniStereo capture end");

		// FBOs don't generate mipmaps by default; do it here:
		if (mMipmap) {
			gl.glActiveTexture(GL20.GL_TEXTURE0);
			gl.glEnable(GL20.GL_TEXTURE_CUBE_MAP);

			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, mTex(0));
			gl.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
			// gl.error("generating mipmap");

			if (mStereo==1) {
				gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, mTex(1));
				gl.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
				// gl.error("generating mipmap");
			}

			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
			gl.glDisable(GL20.GL_TEXTURE_CUBE_MAP);
		}
		// gl.error("OmniStereo FBO mipmap end");
	}

	def onFrameFront(drawable:OmniDrawable, lens:Lens, pose:Pose, vp:Viewport) {
		mFrame += 1
		if (mCubeProgram == null) onCreate();

		// gl.error("OmniStereo onFrameFront begin");

		for (i <-(0 until mNumProjections)) {
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			)
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);

			// gl.projection(Matrix4.perspective(lens.fovy(), viewport.w / viewport.h, lens.near, lens.far))

			mFace = 5; // draw negative z

			{
				val pos = pose.pos
				// Vec3d ux, uy, uz;
				// pose.unitVectors(ux, uy, uz);
				// mModelView = Matrix4.lookAt(-ux, -uy, uz, pos);
				// MatrixStack.rotate(0,0,180f.toRadians)

				mNear = lens.near
				mFar = lens.far
				//const double eyeSep = mStereo ? lens.eyeSep() : 0.0;

				// apply camera transform:
				// gl.modelView(mModelView);
				gl.glClearColor(mClearColor.r,mClearColor.g,mClearColor.b,mClearColor.a);
				gl.glEnable(GL20.GL_DEPTH_TEST)
				gl.glDepthMask(true);
				gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				drawable.onDrawOmni();
			}
		}
		// gl.error("OmniStereo onFrameFront end");
	}

	def drawEye(pose:Pose, eye:Double) {
		if (eye > 0.0) {
			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, mTex(1));
		} else {
			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, mTex(0));
		}
		// gl.error("OmniStereo drawEye after texture");
		mQuad.draw()
		// gl.error("OmniStereo drawEye after quad");
	}

	def draw(lens:Lens, pose:Pose, vp:Viewport) {
		mFrame+=1;
		if (mCubeProgram == null) onCreate();

		// gl.error("OmniStereo draw begin");

		gl.glViewport(vp.l, vp.b, vp.w, vp.h);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(vp.l, vp.b, vp.w, vp.h);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for (i <- (0 until mNumProjections)) {
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			);
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);	
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			p.blend().bind(2);
			p.warp().bind(1);

			// gl.error("OmniStereo cube draw begin");

			mCubeProgram.begin();
			mCubeProgram.setUniforms();

			gl.glActiveTexture(GL20.GL_TEXTURE0);
			gl.glEnable(GL20.GL_TEXTURE_CUBE_MAP);

			// gl.error("OmniStereo cube drawStereo begin");

			drawStereo(drawEye)(lens, pose, viewport);

			// gl.error("OmniStereo cube drawStereo end");

			gl.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
			gl.glDisable(GL20.GL_TEXTURE_CUBE_MAP);

			mCubeProgram.end();
			// gl.error("OmniStereo cube draw end");

			// p.blend().unbind(2);
			// p.warp().unbind(1);
		}
		// gl.error("OmniStereo draw end");
	}

	def drawQuadEye(pose:Pose, eye:Double) {
		mQuad.draw()
	}

	def drawSphereMap(map:GdxTexture, lens:Lens, pose:Pose, vp:Viewport) {
		mFrame+=1;
		if (mCubeProgram == null) onCreate();

		gl.glViewport(vp.l, vp.b, vp.w, vp.h);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(vp.l, vp.b, vp.w, vp.h);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for (i <- (0 until mNumProjections)) {
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			);
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			p.blend().bind(2);
			p.warp().bind(1);

			map.bind(0);
			mSphereProgram.begin();
			mSphereProgram.uniforms("quat") = pose.quat
			mSphereProgram.setUniforms()

			drawQuad();

			mSphereProgram.end();
			// map.unbind(0);

			// p.blend().unbind(2);
			// p.warp().unbind(1);
		}
	}

	def drawDemoEye(pose:Pose, eye:Double) {
		mDemoProgram.uniforms("eyesep") = eye
		mDemoProgram.uniforms("pos") = pose.pos
		mDemoProgram.uniforms("quat") = pose.quat
		mDemoProgram.setUniforms()
		mQuad.draw()
	}

	def drawDemo(lens:Lens, pose:Pose, vp:Viewport) {
		mFrame+=1;
		if (mCubeProgram == null) onCreate();

		gl.glViewport(vp.l, vp.b, vp.w, vp.h);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(vp.l, vp.b, vp.w, vp.h);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for (i <- (0 until mNumProjections)) {
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			);
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			p.blend().bind(2);
			p.warp().bind(1);

			mDemoProgram.begin();
			mDemoProgram.setUniforms

			drawStereo(drawDemoEye)(lens, pose, viewport);

			mDemoProgram.end();

			// p.blend().unbind(2);
			// p.warp().unbind(1);
		}
	}

	def drawWarp(vp:Viewport) {
		mFrame+=1;
		if (mCubeProgram == null) onCreate();

		gl.glViewport(vp.l, vp.b, vp.w, vp.h);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(vp.l, vp.b, vp.w, vp.h);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for (i <- (0 until mNumProjections)){
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			);
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			p.blend().bind(2);
			p.warp().bind(1);
			// Script.ft.bind(0); //UNDO

			mWarpProgram.begin();
			mWarpProgram.setUniforms

			drawQuad();

			mWarpProgram.end();

			// p.blend().unbind(2);
			// p.warp().unbind(1);
		}
	}

	def drawBlend(vp:Viewport) {
		mFrame+=1;
		if (mCubeProgram == null) onCreate();

		gl.glViewport(vp.l, vp.b, vp.w, vp.h);
		gl.glEnable(GL20.GL_SCISSOR_TEST);
		gl.glScissor(vp.l, vp.b, vp.w, vp.h);

		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for (i <- (0 until mNumProjections)){
			val p = projection(i);
			val v = p.viewport();
			val viewport = Viewport(
				vp.l + v.l * vp.w,
				vp.b + v.b * vp.h,
				v.w * vp.w,
				v.h * vp.h
			);
			gl.glViewport(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glEnable(GL20.GL_SCISSOR_TEST);
			gl.glScissor(viewport.l, viewport.b, viewport.w, viewport.h);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			// gl.projection(Matrix4.ortho(0, 1, 0, 1, -1, 1));
			// gl.modelView(Matrix4.identity());

			p.blend().bind(0);

			drawQuad();

			// p.blend().unbind(0);
		}
	}

	def drawQuad() {
		mMode match {
			case ACTIVE =>
				GL11.glDrawBuffer(GL11.GL_BACK_RIGHT);
				mQuad.draw()

				GL11.glDrawBuffer(GL11.GL_BACK_LEFT);
				mQuad.draw()

				GL11.glDrawBuffer(GL11.GL_BACK);

			// case DUAL => ()
				// TODO:

			case _ => mQuad.draw()
		}
	}
}


