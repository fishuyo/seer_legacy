


package com.fishuyo.seer
package allosphere

import graphics._
import maths._
import spatial._



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

/* AlloSystem OmniStereo port */

object OmniStereo {
	var fovy = math.Pi
	var aspect = 2.0

	def fillFishEye(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5

			val az = fovy * aspect * sx
			val el = fovy * sy;
			val sel = math.sin(el)
			val cel = math.cos(el)
			val saz = math.sin(az)
			val caz = math.cos(az)

			val v = Vec3(cel*saz,sel,-cel*caz)
			v.normalize

			// data.put(y*w+x, Array(v.x,v.y,v.z,1.f))
			data.put(Array(v.x,v.y,v.z,1.f), y*w+x, 4)

		}
		data.rewind
	}

	def fillCylinder(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5
			val y1 = sy*fovy*2.0
			val y0 = 1.0 - math.abs(y1)

			val az = fovy * math.Pi * aspect * sx
			val saz = math.sin(az)
			val caz = math.cos(az)

			val v = Vec3(y0*saz,y1,-y0*caz)
			v.normalize

			// data.put(y*w+x, Array(v.x,v.y,v.z,1.f))
			data.put(Array(v.x,v.y,v.z,1.f), y*w+x, 4)

		}
		data.rewind
	}

	def fillRect(data:FloatBuffer, w:Int, h:Int) {
		data.rewind

		for( y<-(0 until h); x<-(0 until w)){
			val normx = x.toDouble / w.toDouble
			val normy = y.toDouble / h.toDouble

			val sx = normx - 0.5
			val sy = normy - 0.5
			val f = 1.0/ math.tan(fovy * 0.5)

			val v = Vec3(f*sx*aspect,f*sy,-1)
			v.normalize

			data.put(Array(v.x,v.y,v.z,1.f), y*w+x, 4)

		}
		data.rewind
	}

	// def softEdge(uint8_t * value, double normx, double normy) {
	// 	static const double mult = 20;

	// 	// fade out at edges:
	// 	value[0] = 255. * sin(M_PI_2 * al.min(1., mult*(0.5 - fabs(normx-0.5)))) * sin(M_PI_2 * al.min(1., mult*(0.5 - fabs(normy-0.5))));
	// }

	// prefix this string to every vertex shader used in rendering the scene
	// use it as e.g.:
	// gl_Position = omni_cube(gl_ModelViewMatrix * gl_Vertex);
	// also be sure to call omni.uniforms(shader) in the OmniStereoDrawable callback
	val glsl = """
		// @omni_eye: the eye parallax distance.
		//	This will be zero for mono, and positive/negative for right/left eyes.
		//	Pass this uniform to the shader in the OmniStereoDrawable callback
		uniform float omni_eye;

		// @omni_face: the GL20.GL_TEXTURE_CUBE_MAP face being rendered.
		//	For a typical forward-facing view, this should == 5.
		//	Pass this uniform to the shader in the OmniStereoDrawable callback
		uniform int omni_face;

		// @omni_near: the near clipping plane.
		uniform float omni_near;

		// @omni_far: the far clipping plane.
		uniform float omni_far;

		// omni_render(vertex)
		// @vertex: the eye-space vertex to be rendered.
		//	Typically gl_Position = omni_render(gl_ModelViewMatrix * gl_Vertex);
		vec4 omni_render(in vec4 vertex) {
			// unit direction vector:
			vec3 vn = normalize(vertex.xyz);
			// omni-stereo effect (in eyespace XZ plane)
			// cross-product with up vector also ensures stereo fades out at Y poles
			//v.xyz -= omni_eye * cross(vn, vec3(0, 1, 0));
			// simplified:
			vertex.xz += vec2(omni_eye * vn.z, omni_eye * -vn.x);
			// convert eye-space into cubemap-space:
			// GL_TEXTURE_CUBE_MAP_POSITIVE_X
			if (omni_face == 0) { vertex.xyz = vec3(-vertex.z, -vertex.y, -vertex.x); }
			// GL_TEXTURE_CUBE_MAP_NEGATIVE_X
			else if (omni_face == 1) { vertex.xyz = vec3( vertex.z, -vertex.y,  vertex.x); }
			// GL_TEXTURE_CUBE_MAP_POSITIVE_Y
			else if (omni_face == 2) { vertex.xyz = vec3( vertex.x,  vertex.z, -vertex.y); }
			// GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
			else if (omni_face == 3) { vertex.xyz = vec3( vertex.x, -vertex.z,  vertex.y); }
			// GL_TEXTURE_CUBE_MAP_POSITIVE_Z
			else if (omni_face == 4) { vertex.xyz = vec3( vertex.x, -vertex.y,  -vertex.z); }
			// GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
			else					 { vertex.xyz = vec3( -vertex.x, -vertex.y, vertex.z); }
			// convert into screen-space:
			// simplified perspective projection since fovy = 90 and aspect = 1
			vertex.zw = vec2(
				(vertex.z*(omni_far+omni_near) + vertex.w*omni_far*omni_near*2.)/(omni_near-omni_far),
				-vertex.z
			);
			return vertex;
		}
	"""

	val vGeneric = """
		varying vec2 T;
		void main(void) {
			// pass through the texture coordinate (normalized pixel):
			T = vec2(gl_MultiTexCoord0);
			gl_Position = vec4(T*2.-1., 0, 1);
		}
	"""

	val fCube = """
		uniform sampler2D pixelMap;
		uniform sampler2D alphaMap;
		uniform samplerCube cubeMap;

		varying vec2 T;

		void main (void){
			// ray location (calibration space):
			vec3 v = normalize(texture2D(pixelMap, T).rgb);

			// index into cubemap:
			vec3 rgb = textureCube(cubeMap, v).rgb * texture2D(alphaMap, T).rgb;

			gl_FragColor = vec4(rgb, 1.);
		}
	"""

	val fSphere = """
		uniform sampler2D pixelMap;
		uniform sampler2D alphaMap;
		uniform sampler2D sphereMap;
		// navigation:
		//uniform vec3 pos;
		uniform vec4 quat;
		varying vec2 T;

		float one_over_pi = 0.318309886183791;

		// q must be a normalized quaternion
		vec3 quat_rotate(in vec4 q, in vec3 v) {
			// return quat_mul(quat_mul(q, vec4(v, 0)), quat_conj(q)).xyz;
			// reduced:
			vec4 p = vec4(
				q.w*v.x + q.y*v.z - q.z*v.y,  // x
				q.w*v.y + q.z*v.x - q.x*v.z,  // y
				q.w*v.z + q.x*v.y - q.y*v.x,  // z
			   -q.x*v.x - q.y*v.y - q.z*v.z   // w
			);
			return vec3(
				-p.w*q.x + p.x*q.w - p.y*q.z + p.z*q.y,  // x
				-p.w*q.y + p.y*q.w - p.z*q.x + p.x*q.z,  // y
				-p.w*q.z + p.z*q.w - p.x*q.y + p.y*q.x   // z
			);
		}

		void main (void){
			// ray location (calibration space):
			vec3 v = normalize(texture2D(pixelMap, T).rgb);

			// ray direction (world space);
			vec3 rd = quat_rotate(quat, v);

			// derive new texture coordinates from polar direction:
			float elevation = acos(-rd.y) * one_over_pi;
			float azimuth = atan(-rd.x, -rd.z) * one_over_pi;
			azimuth = (1. - azimuth)*0.5;	// scale from -1,1 to 1,0
			vec2 sphereT = vec2(azimuth, elevation);

			// read maps:
			vec3 rgb = texture2D(sphereMap, sphereT).rgb * texture2D(alphaMap, T).rgb;
			gl_FragColor = vec4(rgb, 1.);
		}
	"""

	val fWarp = """
		uniform sampler2D pixelMap;
		uniform sampler2D alphaMap;
		varying vec2 T;
		void main (void){
			vec3 v = texture2D(pixelMap, T).rgb;
			v = normalize(v);
			v = mod(v * 8., 1.);
			v *= texture2D(alphaMap, T).rgb;
			gl_FragColor = vec4(v, 1.);
		}
	"""

	val fDemo = """
		uniform sampler2D pixelMap;
		uniform sampler2D alphaMap;
		uniform vec4 quat;
		uniform vec3 pos;
		uniform float eyesep;
		varying vec2 T;

		// q must be a normalized quaternion
		vec3 quat_rotate(in vec4 q, in vec3 v) {
			// return quat_mul(quat_mul(q, vec4(v, 0)), quat_conj(q)).xyz;
			// reduced:
			vec4 p = vec4(
				q.w*v.x + q.y*v.z - q.z*v.y,  // x
				q.w*v.y + q.z*v.x - q.x*v.z,  // y
				q.w*v.z + q.x*v.y - q.y*v.x,  // z
			   -q.x*v.x - q.y*v.y - q.z*v.z   // w
			);
			return vec3(
				-p.w*q.x + p.x*q.w - p.y*q.z + p.z*q.y,  // x
				-p.w*q.y + p.y*q.w - p.z*q.x + p.x*q.z,  // y
				-p.w*q.z + p.z*q.w - p.x*q.y + p.y*q.x   // z
			);
		}

		// repetition:
		vec3 opRepeat( vec3 p, vec3 c ) {
			vec3 q = mod(p,c)-0.5*c;
			return q;
		}

		// distance of p from a box of dim b:
		float udBox( vec3 p, vec3 b ) {
		  return length(max(abs(p)-b, 0.0));
		}

		// MAIN SCENE //
		float map(vec3 p) {
			vec3 pr1 = opRepeat(p, vec3(5, 4, 3));
			float s1 = udBox(pr1, vec3(0.4, 0.1, 0.8));
			return s1;
		}

		// shadow ray:
		float shadow( in vec3 ro, in vec3 rd, float mint, float maxt, float mindt, float k ) {
			float res = 1.;
			for( float t=mint; t < maxt; ) {
				float h = map(ro + rd*t);
				// keep looking:
				t += h * 0.5;
				// blurry penumbra:
				res = min( res, k*h/t );
				if( h<mindt ) {
					// in shadow:
					return res;
				}
			}
			return res;
		}

		void main(){
			vec3 light1 = pos + vec3(1, 2, 3);
			vec3 light2 = pos + vec3(2, -3, 1);
			vec3 color1 = vec3(0.3, 0.7, 0.6);
			vec3 color2 = vec3(0.6, 0.2, 0.8);
			vec3 ambient = vec3(0.1, 0.1, 0.1);

			// pixel location (calibration space):
			vec3 v = normalize(texture2D(pixelMap, T).rgb);
			// ray direction (world space);
			vec3 rd = quat_rotate(quat, v);

			// stereo offset:
			// should reduce to zero as the nv becomes close to (0, 1, 0)
			// take the vector of nv in the XZ plane
			// and rotate it 90' around Y:
			vec3 up = vec3(0, 1, 0);
			vec3 rdx = cross(normalize(rd), up);

			//vec3 rdx = projection_on_plane(rd, up);
			vec3 eye = rdx * eyesep * 0.02;

			// ray origin (world space)
			vec3 ro = pos + eye;

			// initial eye-ray to find object intersection:
			float mindt = 0.01;	// how close to a surface we can get
			float mint = mindt;
			float maxt = 50.;
			float t=mint;
			float h = maxt;

			// find object intersection:
			vec3 p = ro + mint*rd;

			int steps = 0;
			int maxsteps = 50;
			for (t; t<maxt;) {
				h = map(p);
				t += h;
				p = ro + t*rd;
				if (h < mindt) { break; }
				//if (++steps > maxsteps) { t = maxt; break; }
			}

			// lighting:
			vec3 color = vec3(0, 0, 0);

			if (t<maxt) {

				// Normals computed by central differences on the distance field at the shading point (gradient approximation).
				// larger eps leads to softer edges
				float eps = 0.01;
				vec3 grad = vec3(
					map(p+vec3(eps,0,0)) - map(p-vec3(eps,0,0)),
					map(p+vec3(0,eps,0)) - map(p-vec3(0,eps,0)),
					map(p+vec3(0, 0, eps)) - map(p-vec3(0,0,eps))
				);
				vec3 normal = normalize(grad);

				// compute ray to light source:
				vec3 ldir1 = normalize(light1 - p);
				vec3 ldir2 = normalize(light2 - p);

				// abs for bidirectional surfaces
				float ln1 = max(0.,dot(ldir1, normal));
				float ln2 = max(0.,dot(ldir2, normal));

				// shadow penumbra coefficient:
				float k = 16.;

				// check for shadow:
				float smint = 0.001;
				float nudge = 0.01;
				float smaxt = maxt;

				color = ambient
						+ color1 * ln1 //* shadow(p+normal*nudge, ldir1, smint, smaxt, mindt, k)
						+ color2 * ln2 //* shadow(p+normal*smint, ldir2, smint, smaxt, mindt, k)
						;

				//color = 	ambient +
				//		color1 * ln1 +
				//		color2 * ln2;

				/*
				// Ambient Occlusion:
				// sample 5 neighbors in direction of normal
				float ao = 0.;
				float dao = 0.001; // delta between AO samples
				float aok = 2.0;
				float weight = 1.;
				for (int i=0; i<5; i++) {
					float dist = float(i)*dao;
					float factor = dist - map(p + normal*dist);
					ao += weight * factor;
					weight *= 0.6;	// decreasing importance
				}
				ao = 1. - aok * ao;
				color *= ao;
				*/


				// fog:
				float tnorm = t/maxt;
				float fog = 1. - tnorm*tnorm;
				//color *= fog;
			}

			color *= texture2D(alphaMap, T).rgb;

			gl_FragColor = vec4(color, 1);
		}
	"""

}

// Object to encapsulate rendering omni-stereo worlds via cube-maps:
class OmniStereo(res:Int=1024, useMipMaps:Boolean=true) {

	type DrawMethod = (Pose,Double) => Unit  // pose, eye

	var mCubeProgram:Shader = null
	var mSphereProgram:Shader = null
	var mWarpProgram:Shader = null
	var mDemoProgram:Shader = null

	// supports up to 4 warps/viewports
	val mProjections = new Array[Projection](4)
	def projection(i:Int) = mProjections(i)

	val mModelView = 0 //TODO;
	var mClearColor = RGBA(0,0,0,0);

	var mFace = 5
	var mEyeParallax = 0.f
	var mNear = 0.1f
	var mFar = 100.f
	var mResolution = res
	var mNumProjections = 1
	var mFrame = 0
	var mMode = StereoMode.MONO
	var mStereo = 0
	var mAnaglyphMode = AnaglyphMode.RED_CYAN
	var mMipmap = useMipMaps
	var mFullScreen = false

	var mFbo = 0
	var mRbo = 0

	var mTex = Array(0,0)

	configure(WarpMode.FISHEYE)
	configure(BlendMode.SOFTEDGE)

	val mQuad = Quad()
	// mQuad.reset();
	// mQuad.primitive(gl.TRIANGLE_STRIP);
	// mQuad.texCoord	( 0, 0);
	// mQuad.vertex	( 0, 0, 0);
	// mQuad.texCoord	( 1, 0);
	// mQuad.vertex	( 1, 0, 0);
	// mQuad.texCoord	( 0, 1);
	// mQuad.vertex	( 0, 1, 0);
	// mQuad.texCoord	( 1, 1);
	// mQuad.vertex	( 1, 1, 0);



	///	Abstract base class for any object that can be rendered via OmniStereo:
	trait OmniDrawable  {
		/// Place drawing code here
		def onDrawOmni(omni: OmniStereo){}
	}

	/// Encapsulate the trio of fractional viewport, warp & blend maps:
	class Projection {
		class Parameters {
			var projnum = 0.f			// ID of the projector
			var (width, height) = (0.f,0.f)	// width/height in pixels
			var (projector_position, screen_center, normal_unit, x_vec, y_vec) = (Vec3(),Vec3(),Vec3(),Vec3(),Vec3())
			var (screen_radius, bridge_radius, unused0) = (0.f,0.f,0.f)

			def fromList(l:List[Float]){
				projnum = l(0)
				width = l(1)
				height = l(2)
				projector_position = Vec3(l(3),l(4),l(5))
				screen_center = Vec3(l(6),l(7),l(8))
				normal_unit = Vec3(l(9),l(10),l(11))
				x_vec = Vec3(l(12),l(13),l(14))
				y_vec = Vec3(l(15),l(16),l(17))
				screen_radius = l(18)
				bridge_radius = l(19)
				unused0 = l(20)
			}
		};

		var mViewport = Viewport(0, 0, 1, 1)
		def viewport() = mViewport

		// allocate blend map:

		var mBlend:GdxTexture = null
		var mWarp:FloatTexture = null

		def blend() = mBlend
		def warp() = mWarp

		// allocate warp map:
		// mWarp.resize(256, 256)
		// 	.target(Texture.TEXTURE_2D)
		// 	.format(Graphics.RGBA)
		// 	.type(Graphics.FLOAT)
		// 	.filterMin(Texture.LINEAR)
		// 	.allocate();

		var t:Array[Float] = _
		var u:Array[Float] = _
		var v:Array[Float] = _

		val params = new Parameters()

		// derived:
		var (x_unit, y_unit) = (Vec3(),Vec3())
		var (x_pixel, y_pixel, x_offset, y_offset) = (0.f,0.f,0.f,0.f)
		var position = Vec3() //Vec3d

		// TODO: remove this
		var (warpwidth, warpheight) = (0,0)

		// the position/orientation of the raw map data relative to the real world
		var mRegistration = Pose()


		def onCreate(){
			mWarp.t.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.Linear)
			// mWarp.texelFormat(GL_RGB32F_ARB);
			// mWarp.dirty();

			mBlend.setFilter( GdxTexture.TextureFilter.MipMap, GdxTexture.TextureFilter.Linear)
			// mBlend.dirty();
		}

		// load warp/blend from disk:
		def readBlend(path:String){
			mBlend = new GdxTexture(Gdx.files.absolute(path), Pixmap.Format.Intensity, false)

		}
		def readWarp(path:String){

			try{
				val ds = new DataInputStream( new FileInputStream(path))

				val h = readInt(ds)/3
				val w = readInt(ds)

				println(s"warp dim $w x $h\n")

				t = new Array[Float](w*h)
				u = new Array[Float](w*h)
				v = new Array[Float](w*h)

				for( i <- (0 until w*h)) t(i) = readFloat(ds)
				for( i <- (0 until w*h)) u(i) = readFloat(ds)
				for( i <- (0 until w*h)) v(i) = readFloat(ds)

				mWarp = new FloatTexture(w,h)

				updatedWarp()

				ds.close()

				println(s"read $path\n")

			} catch {
				case e: Exception => println("failed to open Projector configuration file " + path + "\n")
			}

		}

		def readInt(ds:DataInputStream) = {
			var b = Array[Byte](0,0,0,0)
			for( i <- (0 until 4)) b(i) = ds.readByte
			val i = ((b(3)&0xff)<< 24)+((b(2)&0xff)<< 16)+((b(1)&0xff)<< 8)+(b(0)&0xff)
			i
		}
		def readFloat(ds:DataInputStream) = {
			java.lang.Float.intBitsToFloat(readInt(ds))
		}

		def readParameters(path:String, verbose:Boolean=true){

			import java.io._
			import java.lang.Float.intBitsToFloat

			try{
				val ds = new DataInputStream( new FileInputStream(path))
				var list = List[Float]()

				while(ds.available >= 4) list = readFloat(ds) :: list
				params.fromList(list)
				ds.close()

				initParameters(verbose)
				println("read " + path + "\n" )

			} catch {
				case e: Exception => println("failed to open Projector configuration file " + path + "\n")
			}
		}

		def initParameters( verbose:Boolean=true){
			val v = params.screen_center - params.projector_position
			val screen_perpendicular_dist = params.normal_unit.dot(v)
			val compensated_center = (v) / screen_perpendicular_dist + params.projector_position

			// calculate uv parameters
			val x_dist = params.x_vec.mag();
			x_unit = params.x_vec / x_dist;
			x_pixel = x_dist / params.width;
			x_offset = x_unit.dot(compensated_center - params.projector_position);

			val y_dist = params.y_vec.mag();
			y_unit = params.y_vec / y_dist;
			y_pixel = y_dist / params.height;
			y_offset = y_unit.dot(compensated_center - params.projector_position);
		}

		// adjust the registration position:
		def registrationPosition(pos: Vec3 /*double*/){}

		// Texture& blend() { return mBlend; }
		// Texture& warp() { return mWarp; }
		// Viewport& viewport() { return mViewport; }


		def updatedWarp(){
			val w = mWarp.w
			val h = mWarp.h
			for( y <- (0 until h); x <- (0 until w)){
				val y1 = h-y-1
				val idx = y1*w+x

		    mWarp.data.position(4*idx)
		    mWarp.data.put( Array(t(idx),u(idx),v(idx),1.f),0, 4)

			 //  if (y == 32 && x == 32) {
				// 	println("example: %f %f %f -> %f %f %f\n",
				// 		t[idx], u[idx], v[idx],
				// 		cell[0], cell[1], cell[2]);
				// }
			}

		  mWarp.data.rewind
			mWarp.td.consumeCompressedData(0);
		}

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

		// configpath += "/calibration-current/";

		// if (L.dofile(configpath + "/" + configname + ".lua", 0)) return *this;

		// L.getglobal("projections");
		// if (!lua_istable(L, -1)) {
			// printf("config file %s has no projections\n", configpath.c_str());
			// return *this;
		// }
		// int projections = L.top();

		// set active stereo
		// lua_getfield(L, projections, "active");
		// if (lua_toboolean(L, -1)) {
			// mMode = ACTIVE;
		// }
		// L.pop(); //active

		// set fullscreen by default mode?
		// lua_getfield(L, projections, "fullscreen");
		// if (lua_toboolean(L, -1)) {
		// 	mFullScreen = true;
		// }
		// L.pop(); // fullscreen

		// set resolution?
		// lua_getfield(L, projections, "resolution");
		// if (lua_isnumber(L, -1)) {
		// 	resolution(lua_tonumber(L, -1));
		// }
		// L.pop(); // resolution

		// mNumProjections = lua_objlen(L, projections);
		// printf("found %d viewports\n", mNumProjections);

		// for (unsigned i=0; i<mNumProjections; i++) {
			// L.push(i+1);
			// lua_gettable(L, projections);
			// int projection = L.top();
			//L.dump("config");

			// lua_getfield(L, projection, "viewport");
			// if (lua_istable(L, -1)) {
				// int viewport = L.top();
				// lua_getfield(L, viewport, "l");
				// mProjections[i].viewport().l = L.to<float>(-1);
				// L.pop();

				// lua_getfield(L, viewport, "b");
				// mProjections[i].viewport().b = L.to<float>(-1);
				// L.pop();

				// lua_getfield(L, viewport, "w");
				// mProjections[i].viewport().w = L.to<float>(-1);
				// L.pop();

				// lua_getfield(L, viewport, "h");
				// mProjections[i].viewport().h = L.to<float>(-1);
				// L.pop();

			// }
			// L.pop(); // viewport

			// lua_getfield(L, projection, "warp");
			// if (lua_istable(L, -1)) {
				// int warp = L.top();

				// lua_getfield(L, warp, "width");
				// if (lua_isnumber(L, -1)) {
					// mProjections[i].warpwidth = lua_tonumber(L, -1);
				// }
				// L.pop();

				// lua_getfield(L, warp, "height");
				// if (lua_isnumber(L, -1)) {
					// mProjections[i].warpheight = lua_tonumber(L, -1);
				// }
				// L.pop();

				// lua_getfield(L, warp, "file");
				// if (lua_isstring(L, -1)) {
					// load from file
					// mProjections[i].readWarp(configpath + "/" + lua_tostring(L, -1));
				// }
				// L.pop();
			// }
			// L.pop(); // warp

			// lua_getfield(L, projection, "blend");
			// if (lua_istable(L, -1)) {
				// int blend = L.top();
				// lua_getfield(L, blend, "file");
				// if (lua_isstring(L, -1)) {
					// load from file
					// mProjections[i].readBlend(configpath + "/" + lua_tostring(L, -1));
				// } else {
					// TODO: generate blend...
				// }
				// L.pop();
			// }
			// L.pop(); // blend

			// lua_getfield(L, projection, "params");
			// if (lua_istable(L, -1)) {
				// int params = L.top();
				// lua_getfield(L, params, "file");
				// if (lua_isstring(L, -1)) {
					// load from file
					// mProjections[i].readParameters(configpath + "/" + lua_tostring(L, -1)); //, true);
				// }
				// L.pop();
			// }
			// L.pop(); // params

			// lua_getfield(L, projection, "position");
			// if (lua_istable(L, -1)) {
				// // int position = L.top();
				// lua_rawgeti(L, position, 1);
				// mProjections[i].position.x = L.to<double>(-1);
				// L.pop();
				// lua_rawgeti(L, position, 2);
				// mProjections[i].position.y = L.to<double>(-1);
				// L.pop();
				// lua_rawgeti(L, position, 3);
				// mProjections[i].position.z = L.to<double>(-1);
				// L.pop();
			// }
			// L.pop(); // position


			// L.pop(); // projector
		// }

		// L.pop(); // the projections table
		// return *this;
	}

	// configure generatively:
	def configure(wm:WarpModeType, a:Float=2.f, f:Float=math.Pi) = {
		mNumProjections = 1
		val p = mProjections(0)
		wm match {
			case FISHEYE =>
				OmniStereo.fovy = f;
				OmniStereo.aspect = a;
				// p.warp().array().fill(fillFishEye);
				// p.warp().dirty();
				// break;
			case CYLINDER =>
				OmniStereo.fovy = f / math.Pi;
				OmniStereo.aspect = a;
				// p.warp().array().fill(fillCylinder);
				// p.warp().dirty();
				// break;
			case _ =>
				OmniStereo.fovy = f / 2.;
				OmniStereo.aspect = a;
				// p.warp().array().fill(fillRect);
				// p.warp().dirty();
				// break;
		}
		this
	}

	def configure(bm:BlendModeType) = {
		mNumProjections = 1;
		val p = mProjections(0)
		// bm match { // TODO
		// 	case SOFTEDGE =>
		// 		p.blend().array().fill(softEdge);
		// 		p.blend().dirty();
		// 		break;

		// 	case _ =>
		// 		// default blend of 1:
		// 		uint8_t white = 255;
		// 		p.blend().array().set2d(&white);
		// 		p.blend().dirty();
		// 		break;
		// }
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
				// gl.error("OmniStereo drawStereo GL_BACK_RIGHT");
				f(pose, eye);

				GL11.glDrawBuffer(GL11.GL_BACK_LEFT);
				// gl.error("OmniStereo drawStereo GL_BACK_LEFT");
				f(pose, -eye);

				GL11.glDrawBuffer(GL11.GL_BACK);
				// gl.error("OmniStereo drawStereo GL_BACK");
				// break;

			// case DUAL =>
				// gl.viewport(vp.l + vp.w*0.5, vp.b, vp.w*0.5, vp.h);
				// f(pose, eye);
				// gl.viewport(vp.l, vp.b, vp.w*0.5, vp.h);
				// f(pose, -eye);
				// break;

			// case ANAGLYPH =>
				// switch(mAnaglyphMode){
				// 	case RED_BLUE:
				// 	case RED_GREEN:
				// 	case RED_CYAN:	glColorMask(GL_TRUE, GL_FALSE,GL_FALSE,GL_TRUE); break;
				// 	case BLUE_RED:	glColorMask(GL_FALSE,GL_FALSE,GL_TRUE, GL_TRUE); break;
				// 	case GREEN_RED:	glColorMask(GL_FALSE,GL_TRUE, GL_FALSE,GL_TRUE); break;
				// 	case CYAN_RED:	glColorMask(GL_FALSE,GL_TRUE, GL_TRUE, GL_TRUE); break;
				// 	default:		glColorMask(GL_TRUE, GL_TRUE, GL_TRUE ,GL_TRUE);
				// }
				// f(pose, eye);

				// switch(mAnaglyphMode){
				// 	case RED_BLUE:	glColorMask(GL_FALSE,GL_FALSE,GL_TRUE, GL_TRUE); break;
				// 	case RED_GREEN:	glColorMask(GL_FALSE,GL_TRUE, GL_FALSE,GL_TRUE); break;
				// 	case RED_CYAN:	glColorMask(GL_FALSE,GL_TRUE, GL_TRUE, GL_TRUE); break;
				// 	case BLUE_RED:
				// 	case GREEN_RED:
				// 	case CYAN_RED:	glColorMask(GL_TRUE, GL_FALSE,GL_FALSE,GL_TRUE); break;
				// 	default:		glColorMask(GL_TRUE, GL_TRUE ,GL_TRUE, GL_TRUE);
				// }
				// // clear depth before this pass:
				// gl.glDepthMask(true);
				// gl.glEnable(GL20.GL_DEPTH_TEST)
				// gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
				// f(pose, -eye);

				// break;

			case RIGHT_EYE =>
				f(pose, eye);

			case LEFT_EYE =>
				f(pose, -eye);

			case _ =>
				f(pose, 0);
		}
	}

	def onCreate() {

		// force allocation of warp/blend textures:
		for(i<-(0 until 4)) {
			mProjections(i) = new Projection
			mProjections(i).onCreate()
		}

		// Shader cubeV, cubeF;
		// cubeV.source(vGeneric, Shader.VERTEX).compile();
		// cubeF.source(fCube, Shader.FRAGMENT).compile();
		// mCubeProgram.attach(cubeV).attach(cubeF);
		// mCubeProgram.link(false);	// false means do not validate
		// // set uniforms before validating to prevent validation error
		// mCubeProgram.begin();
		// 	mCubeProgram.uniform("alphaMap", 2);
		// 	mCubeProgram.uniform("pixelMap", 1);
		// 	mCubeProgram.uniform("cubeMap", 0);
		// mCubeProgram.end();
		// mCubeProgram.validate();
		// cubeV.printLog();
		// cubeF.printLog();
		// mCubeProgram.printLog();
		// Graphics.error("cube program onCreate");

		// Shader sphereV, sphereF;
		// sphereV.source(vGeneric, Shader.VERTEX).compile();
		// sphereF.source(fSphere, Shader.FRAGMENT).compile();
		// mSphereProgram.attach(sphereV).attach(sphereF);
		// mSphereProgram.link(false);	// false means do not validate
		// // set uniforms before validating to prevent validation error
		// mSphereProgram.begin();
		// 	mSphereProgram.uniform("alphaMap", 2);
		// 	mSphereProgram.uniform("pixelMap", 1);
		// 	mSphereProgram.uniform("sphereMap", 0);
		// mSphereProgram.end();
		// mSphereProgram.validate();
		// sphereV.printLog();
		// sphereF.printLog();
		// mSphereProgram.printLog();
		// Graphics.error("cube program onCreate");

		// Shader warpV, warpF;
		// warpV.source(vGeneric, Shader.VERTEX).compile();
		// warpF.source(fWarp, Shader.FRAGMENT).compile();
		// mWarpProgram.attach(warpV).attach(warpF);
		// mWarpProgram.link(false);	// false means do not validate
		// // set uniforms before validating to prevent validation error
		// mWarpProgram.begin();
		// 	mWarpProgram.uniform("alphaMap", 2);
		// 	mWarpProgram.uniform("pixelMap", 1);
		// mWarpProgram.end();
		// mWarpProgram.validate();
		// warpV.printLog();
		// warpF.printLog();
		// mWarpProgram.printLog();
		// Graphics.error("cube program onCreate");

		// Shader demoV, demoF;
		// demoV.source(vGeneric, Shader.VERTEX).compile();
		// demoF.source(fDemo, Shader.FRAGMENT).compile();
		// mDemoProgram.attach(demoV).attach(demoF);
		// mDemoProgram.link(false);	// false means do not validate
		// // set uniforms before validating to prevent validation error
		// mDemoProgram.begin();
		// 	mDemoProgram.uniform("alphaMap", 2);
		// 	mDemoProgram.uniform("pixelMap", 1);
		// mDemoProgram.end();
		// mDemoProgram.validate();
		// demoV.printLog();
		// demoF.printLog();
		// mDemoProgram.printLog();
		// Graphics.error("cube program onCreate");

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
			var array = Array(1.f,0.f,0.f,0.f)
			buf.put(array)
			buf.rewind
			GL11.glTexGen( GL11.GL_S, GL11.GL_OBJECT_PLANE, buf );
			array = Array(0.f,1.f,0.f,0.f)
			buf.put(array)
			buf.rewind
			GL11.glTexGen( GL11.GL_T, GL11.GL_OBJECT_PLANE, buf );
			array = Array(0.f,0.f,1.f,0.f)
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
			printf("GPU does not support required FBO configuration\n");
			exit(0);
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
			for (mFace <- (0 until 6)) {

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
				drawable.onDrawOmni(this);
			}
		}

		// glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);
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

				mNear = lens.near
				mFar = lens.far
				//const double eyeSep = mStereo ? lens.eyeSep() : 0.;

				// apply camera transform:
				// gl.modelView(mModelView);
				gl.glClearColor(mClearColor.r,mClearColor.g,mClearColor.b,mClearColor.a);
				gl.glEnable(GL20.GL_DEPTH_TEST)
				gl.glDepthMask(true);
				gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				drawable.onDrawOmni(this);
			}
		}
		// gl.error("OmniStereo onFrameFront end");
	}

	def drawEye(pose:Pose, eye:Double) {
		if (eye > 0.) {
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
			p.warp().t.bind(1);

			// gl.error("OmniStereo cube draw begin");

			mCubeProgram.begin();
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
			p.warp().t.bind(1);

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
			p.warp().t.bind(1);

			mDemoProgram.begin();

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
			p.warp().t.bind(1);

			mWarpProgram.begin();

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
			// case ACTIVE =>
			// 	glDrawBuffer(GL_BACK_RIGHT);
			// 	mQuad.draw()

			// 	glDrawBuffer(GL_BACK_LEFT);
			// 	mQuad.draw()

			// 	glDrawBuffer(GL_BACK);

			// case DUAL => ()
				// TODO:

			case _ => mQuad.draw()
		}
	}
}


/// Stores optics settings important for rendering
class Lens(
		fovy0:Double=30.0,
		var near:Double=0.1,
		var far:Double=100.0,
		var focalLength:Double=6.0,
		var eyeSep:Double=0.02
	){

	var mTanFOV = 0.0				// Cached factor for computing frustum dimensions
	var fovy = fovy0

	setFovy(fovy0)

	// setters
	def setFovy(v:Double) = {
		val cDeg2Rad = math.Pi / 180.;
		fovy = v;
		mTanFOV = math.tan(fovy * cDeg2Rad*0.5);
		this
	}							///< Set vertical field of view, in degrees
	def setFovx(v:Double, aspect:Double) = {
		setFovy(getFovyForFovX(v, aspect));
		this
	}				///< Set horizontal field of view, in degrees

	def eyeSepAuto() = { focalLength/30.0; } ///< Get automatic inter-ocular distance

	// void frustum(Frustumd& f, const Pose& p, double aspect) const;
	
	/// Returns half the height of the frustum at a given depth
	/// To get the half-width multiply the half-height by the viewport aspect
	/// ratio.
	// double heightAtDepth(double depth) const { return depth*mTanFOV; }
	
	/// Returns half the height of the frustum at the near plane
	// double heightAtNear() const { return heightAtDepth(near()); }
	
	// calculate desired fovy, given the Y height of the border at a specified Z depth:
	// static double getFovyForHeight(double height, double depth) {
		// return 2.*M_RAD2DEG*atan(height/depth);
	// }

	/// Calculate required fovy to produce a specific fovx
	/// @param[fovx] field-of-view in X axis to recreate
	/// @param[aspect] aspect ratio of viewport
	/// @return field-of-view in Y axis, usable by Lens.fovy() 
	def getFovyForFovX(fovx:Double, aspect:Double) = {
		val farW = math.tan(0.5*fovx.toRadians);
		2.*math.atan(farW/aspect).toDegrees
	}


	// // @param[in] isStereo		Whether scene is in stereo (widens near/far planes to fit both eyes)
	// void Lens::frustum(Frustumd& f, const Pose& p, double aspect) const {//, bool isStereo) const {

	// 	Vec3d ur, uu, uf;
	// 	p.directionVectors(ur, uu, uf);
	// 	const Vec3d& pos = p.pos();

	// 	double nh = heightAtDepth(near());
	// 	double fh = heightAtDepth(far());

	// 	double nw = nh * aspect;
	// 	double fw = fh * aspect;
		
	// //	// This effectively creates a union between the near/far planes of the 
	// //	// left and right eyes. The offsets are computed by using the law
	// //	// of similar triangles.
	// //	if(isStereo){
	// //		nw += fabs(0.5*eyeSep()*(focalLength()-near())/focalLength());
	// //		fw += fabs(0.5*eyeSep()*(focalLength()- far())/focalLength());
	// //	}

	// 	Vec3d nc = pos + uf * near();	// center point of near plane
	// 	Vec3d fc = pos + uf * far();	// center point of far plane

	// 	f.ntl = nc + uu * nh - ur * nw;
	// 	f.ntr = nc + uu * nh + ur * nw;
	// 	f.nbl = nc - uu * nh - ur * nw;
	// 	f.nbr = nc - uu * nh + ur * nw;

	// 	f.ftl = fc + uu * fh - ur * fw;
	// 	f.ftr = fc + uu * fh + ur * fw;
	// 	f.fbl = fc - uu * fh - ur * fw;
	// 	f.fbr = fc - uu * fh + ur * fw;

	// 	f.computePlanes();
	// }

}



