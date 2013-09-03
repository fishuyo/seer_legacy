


package com.fishuyo
package seer
package allosphere

import graphics._

import com.badlogic.gdx.graphics.{Texture => GdxTexture}


/* AlloSystem OmniStereo port */

object OmniStereo {
	val fovy = math.Pi
	val aspect = 2.0

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

			val v = Vec3f(cel*saz,sel,-cel*caz)
			v.normalize

			data.put(y*w+x, Array(v.x,v.y,v.z,1.f))

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

			val v = Vec3f(y0*saz,y1,-y0*caz)
			v.normalize

			data.put(y*w+x, Array(v.x,v.y,v.z,1.f))

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

			val v = Vec3f(f*sx*aspect,f*sy,-1)
			v.normalize

			data.put(y*w+x, Array(v.x,v.y,v.z,1.f))

		}
		data.rewind	
	}

	def softEdge(uint8_t * value, double normx, double normy) {
		static const double mult = 20;
		
		// fade out at edges:
		value[0] = 255. * sin(M_PI_2 * al::min(1., mult*(0.5 - fabs(normx-0.5)))) * sin(M_PI_2 * al::min(1., mult*(0.5 - fabs(normy-0.5)))); 
	}

	// prefix this string to every vertex shader used in rendering the scene
	// use it as e.g.:
	// gl_Position = omni_cube(gl_ModelViewMatrix * gl_Vertex);
	// also be sure to call omni.uniforms(shader) in the OmniStereoDrawable callback
	val glsl = """	
		// @omni_eye: the eye parallax distance. 	
		//	This will be zero for mono, and positive/negative for right/left eyes.
		//	Pass this uniform to the shader in the OmniStereoDrawable callback 
		uniform float omni_eye;
			
		// @omni_face: the GL_TEXTURE_CUBE_MAP face being rendered. 	
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
class OmniStereo(resolution:Int=1024, useMipMaps:Boolean=true) {
	
	type DrawMethod = (Pose,Double) => ()
	
	// ShaderProgram mCubeProgram, mSphereProgram, mWarpProgram, mDemoProgram;
	
	// supports up to 4 warps/viewports
	val mProjections = new Array[Projection](4)

	val mModelView = 0 //TODO;
	var mClearColor = RGBA(0,0,0,0);
	
	var mFace = 5
	var mEyeParallax = 0.f
	var mNear = 0.1f
	var mFar = 100.f
	var mResolution = resolution
	var mNumProjections = 1
	var mFrame = 0
	var mMode:StereoMode = MONO
	var mStereo = 0
	var mAnaglyphMode:AnaglyphMode = RED_CYAN
	var mMipmap = useMipMaps
	var mFullScreen = false

	var mFbo = 0
	var mRbo = 0

	mTex = Array(0,0)

	configure(FISHEYE)
	configure(SOFTEDGE)
	
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
	class Drawable  {
		/// Place drawing code here
		def onDrawOmni(omni: OmniStereo)	
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
		
		val mViewport = Viewport(0, 0, 1, 1)
		
		// allocate blend map:

		val mBlend:GdxTexture = null
		val mWarp:Texture = null 

		// allocate warp map:
		// mWarp.resize(256, 256)
		// 	.target(Texture::TEXTURE_2D)
		// 	.format(Graphics::RGBA)
		// 	.type(Graphics::FLOAT)
		// 	.filterMin(Texture::LINEAR)
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
			mWarp.t.setFilter( TextureFilter.MipMap, TextureFilter.Linear)
			// mWarp.texelFormat(GL_RGB32F_ARB);
			// mWarp.dirty();
			
			mBlend.setFilter( TextureFilter.MipMap, TextureFilter.Linear)
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

				for( i <- (0 until w*h)) t[i] = readFloat(ds)
				for( i <- (0 until w*h)) u[i] = readFloat(ds)
				for( i <- (0 until w*h)) v[i] = readFloat(ds)

				mWarp = new Texture(w,h)
			
				updatedWarp()

				ds.close()
				
				println(s"read $path\n")
				
			catch {
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
			intBitsToFloat(readInt(ds))
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

			catch {
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
			mWarp.td.consumeCompressedData();
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
	
	enum WarpMode {
		FISHEYE,
		CYLINDER,
		RECT
	};
	
	enum BlendMode {
		NOBLEND,
		SOFTEDGE
	};



	// @resolution should be a power of 2
	def resolution(resolution:Int) = {
		mResolution = r;
		// force GPU reallocation:	
		mFbo = 0;
		mRbo = 0;
		mTex(0) = 0
		mTex(1) = 0;
		this
	}
	def resolution() = mResolution
	
	// configure the projections according to files
	OmniStereo& configure(std::string configpath, std::string configname="default"){

	}
	
	// configure generatively:
	OmniStereo& configure(wm:WarpMode, a:Float=2.f, f:Float=math.Pi){
		mNumProjections = 1
		val p = mProjections(0)
		wm match {
			case FISHEYE =>
				OmniStereo.fovy = f;
				OmniStereo.aspect = a;
				p.warp().array().fill(fillFishEye);
				p.warp().dirty();
				break;
			case CYLINDER =>
				OmniStereo.fovy = f / M_PI;
				OmniStereo.aspect = a;
				p.warp().array().fill(fillCylinder);
				p.warp().dirty();
				break;
			case _ =>
				OmniStereo.fovy = f / 2.;
				OmniStereo.aspect = a;
				p.warp().array().fill(fillRect);
				p.warp().dirty();
				break;
		}
		return *this;
	}
	OmniStereo& configure(BlendMode bm);
	
	// capture a scene to the cubemap textures 
	// this is likely to be an expensive call, as it will render the scene 
	// six (twelve for stereo) times, by calling back into @draw
	// @draw should render without changing the viewport, modelview or projection matrices
	// @lens is used for near/far clipping planes, and eye separation
	// @pose sets the camera position/orientation
	void capture(OmniStereo::Drawable& drawable, const Lens& lens, const Pose& pose);
	// render the captured scene to multiple warp maps and viewports
	// @viewport is the pixel dimensions of the window
	void draw(const Lens& lens, const Pose& pose, const Viewport& vp);
	
	// typically they would be combined like this:
	void onFrame(OmniStereo::Drawable& drawable, const Lens& lens, const Pose& pose, const Viewport& vp) {
		capture(drawable, lens, pose);
		draw(lens, pose, vp);
	}
	
	// render front-view only (bypass FBO)
	void onFrameFront(OmniStereo::Drawable& drawable, const Lens& lens, const Pose& pose, const Viewport& vp);
		
	// send the proper uniforms to the shader:
	void uniforms(ShaderProgram& program) const;
	
	// enable/disable stereographic mode:
	OmniStereo& stereo(bool b) { mStereo = b; return *this; }
	bool stereo() const { return mStereo; }	

	OmniStereo& mode(StereoMode m) { mMode = m; return *this; }
	StereoMode mode() { return mMode; }
	
	// returns true if configured for active stereo:
	bool activeStereo() { return mMode == ACTIVE; }
	
	// returns true if config file suggested fullscreen by default
	bool fullScreen() { return mFullScreen; }
	
	// get/set the background color
	Color& clearColor() { return mClearColor; }
	
	// get individual projector configurations:
	Projection& projection(int i) { return mProjections[i]; }
	int numProjections() const { return mNumProjections; }
	
	// useful accessors:
	GLuint texture() const { return mTex[0]; }
	GLuint textureLeft() const { return mTex[0]; }
	GLuint textureRight() const { return mTex[1]; }
	
	// the current face being rendered:
	int face() const { return mFace; }
	
	// the current eye parallax 
	// (positive for right eye, negative for left, zero for mono):
	float eye() const { return mEyeParallax; }
	
	// create GPU resources:
	void onCreate();
	void onDestroy();
	
	// configure the warp maps
	void loadWarps(std::string calibrationpath, std::string hostname = "default");
	
	// debugging:
	void drawWarp(const Viewport& vp);
	void drawBlend(const Viewport& vp);
	void drawSphereMap(Texture& map, const Lens& lens, const Pose& pose, const Viewport& vp);
	void drawDemo(const Lens& lens, const Pose& pose, const Viewport& vp);
	
	// adjust the registration position:
	void registrationPosition(const Vec3d& pos) {
		for (int i=0; i<numProjections(); i++) {
			projection(i).registrationPosition(pos);
		}
	}
	
protected:
	// supports up to 4 warps/viewports
	Projection mProjections[4];
	
	typedef void (OmniStereo::*DrawMethod)(const Pose& pose, double eye);
	void drawEye(const Pose& pose, double eye);
	void drawQuadEye(const Pose& pose, double eye);
	void drawDemoEye(const Pose& pose, double eye);
	
	template<DrawMethod F>
	void drawStereo(const Lens& lens, const Pose& pose, const Viewport& viewport);
	
	void drawQuad();

	void capture_eye(GLuint& tex, OmniStereo::Drawable& drawable);
	
	GLuint mTex[2];	// the cube map textures
	GLuint mFbo;
	GLuint mRbo;	// TODO: alternative depth cube-map texture option?
	ShaderProgram mCubeProgram, mSphereProgram, mWarpProgram, mDemoProgram;
	Mesh mQuad;
	
	Graphics gl;
	Matrix4d mModelView;
	Color mClearColor;
	
	// these become shader uniforms:
	int mFace;
	float mEyeParallax, mNear, mFar;
	
	unsigned mResolution;
	unsigned mNumProjections;
	int mFrame;
	
	StereoMode mMode;
	AnaglyphMode mAnaglyphMode;
	
	bool mStereo, mMipmap, mFullScreen;	
}; 	

/* inline implementation */

inline 
void OmniStereo::uniforms(ShaderProgram& program) const {
	program.uniform("omni_face", mFace);
	program.uniform("omni_eye", mEyeParallax);
	program.uniform("omni_near", mNear);
	program.uniform("omni_far", mFar);
	gl.error("sending OmniStereo uniforms");
}

template<OmniStereo::DrawMethod F>
inline void OmniStereo::drawStereo(const Lens& lens, const Pose& pose, const Viewport& vp) {
	double eye = lens.eyeSep();
	switch (mMode) {
		case SEQUENTIAL:
			if (mFrame & 1) {
				(this->*F)(pose, eye);
			} else {
				(this->*F)(pose, -eye);
			}
			break;
			
		case ACTIVE:
			glDrawBuffer(GL_BACK_RIGHT);
			gl.error("OmniStereo drawStereo GL_BACK_RIGHT");
			(this->*F)(pose, eye);
			
			glDrawBuffer(GL_BACK_LEFT);
			gl.error("OmniStereo drawStereo GL_BACK_LEFT");
			(this->*F)(pose, -eye);
			
			glDrawBuffer(GL_BACK);
			gl.error("OmniStereo drawStereo GL_BACK");
			break;
		
		case DUAL:
			gl.viewport(vp.l + vp.w*0.5, vp.b, vp.w*0.5, vp.h);
			(this->*F)(pose, eye);
			gl.viewport(vp.l, vp.b, vp.w*0.5, vp.h);
			(this->*F)(pose, -eye);
			break;
			
		case ANAGLYPH:
			switch(mAnaglyphMode){
				case RED_BLUE:
				case RED_GREEN:
				case RED_CYAN:	glColorMask(GL_TRUE, GL_FALSE,GL_FALSE,GL_TRUE); break;
				case BLUE_RED:	glColorMask(GL_FALSE,GL_FALSE,GL_TRUE, GL_TRUE); break;
				case GREEN_RED:	glColorMask(GL_FALSE,GL_TRUE, GL_FALSE,GL_TRUE); break;
				case CYAN_RED:	glColorMask(GL_FALSE,GL_TRUE, GL_TRUE, GL_TRUE); break;
				default:		glColorMask(GL_TRUE, GL_TRUE, GL_TRUE ,GL_TRUE);
			} 
			(this->*F)(pose, eye);
			
			switch(mAnaglyphMode){
				case RED_BLUE:	glColorMask(GL_FALSE,GL_FALSE,GL_TRUE, GL_TRUE); break;
				case RED_GREEN:	glColorMask(GL_FALSE,GL_TRUE, GL_FALSE,GL_TRUE); break;
				case RED_CYAN:	glColorMask(GL_FALSE,GL_TRUE, GL_TRUE, GL_TRUE); break;
				case BLUE_RED:
				case GREEN_RED:
				case CYAN_RED:	glColorMask(GL_TRUE, GL_FALSE,GL_FALSE,GL_TRUE); break;
				default:		glColorMask(GL_TRUE, GL_TRUE ,GL_TRUE, GL_TRUE);
			}
			// clear depth before this pass:
			gl.depthMask(1);
			gl.depthTesting(1);
			gl.clear(gl.DEPTH_BUFFER_BIT);
			(this->*F)(pose, -eye);
			
			break;
		
		case RIGHT_EYE:
			(this->*F)(pose, eye);
			break;
		
		case LEFT_EYE:
			(this->*F)(pose, -eye);
			break;
			
		case MONO:
		default:
			(this->*F)(pose, 0);
			break;
	}
}
	
} // al::








OmniStereo& OmniStereo::configure(WarpMode wm, float a, float f) {
	mNumProjections = 1;
	Projection& p = mProjections[0];
	switch (wm) {
		case FISHEYE:
			fovy = f;
			aspect = a;
			p.warp().array().fill(fillFishEye);
			p.warp().dirty();
			break;
		case CYLINDER:
			fovy = f / M_PI;
			aspect = a;
			p.warp().array().fill(fillCylinder);
			p.warp().dirty();
			break;
		default:
			fovy = f / 2.;
			aspect = a;
			p.warp().array().fill(fillRect);
			p.warp().dirty();
			break;
	}
	return *this;
}

OmniStereo& OmniStereo::configure(BlendMode bm) {
	mNumProjections = 1;
	Projection& p = mProjections[0];
	switch (bm) {
		case SOFTEDGE:
			p.blend().array().fill(softEdge);
			p.blend().dirty();
			break;
		default:
			// default blend of 1:	
			uint8_t white = 255;
			p.blend().array().set2d(&white);
			p.blend().dirty();
			break;
	}
	return *this;
}

OmniStereo& OmniStereo::configure(std::string configpath, std::string configname) {
 
    if (configpath == "") {
        FILE *pipe = popen("echo ~", "r");
        if (pipe) {
          char c;
          while((c = getc(pipe)) != EOF) {
        if (c == '\r' || c == '\n')
              break;
        configpath += c;
          }
          pclose(pipe);
        }
        configpath += "/calibration-current/";
      }


	if (L.dofile(configpath + "/" + configname + ".lua", 0)) return *this;
	
	L.getglobal("projections");
	if (!lua_istable(L, -1)) {
		printf("config file %s has no projections\n", configpath.c_str());
		return *this;
	}
	int projections = L.top();
	
	// set active stereo
	lua_getfield(L, projections, "active");
	if (lua_toboolean(L, -1)) {
		mMode = ACTIVE;
	}
	L.pop(); //active
	
	// set fullscreen by default mode?
	lua_getfield(L, projections, "fullscreen");
	if (lua_toboolean(L, -1)) {
		mFullScreen = true;
	}
	L.pop(); // fullscreen
	
	// set resolution?
	lua_getfield(L, projections, "resolution");
	if (lua_isnumber(L, -1)) {
		resolution(lua_tonumber(L, -1));
	}
	L.pop(); // resolution
	
	mNumProjections = lua_objlen(L, projections);
	printf("found %d viewports\n", mNumProjections);
	
	for (unsigned i=0; i<mNumProjections; i++) {
		L.push(i+1);
		lua_gettable(L, projections);
		int projection = L.top();
		//L.dump("config");
		
		lua_getfield(L, projection, "viewport");
		if (lua_istable(L, -1)) {
			int viewport = L.top();
			lua_getfield(L, viewport, "l");
			mProjections[i].viewport().l = L.to<float>(-1);
			L.pop();
			
			lua_getfield(L, viewport, "b");
			mProjections[i].viewport().b = L.to<float>(-1);
			L.pop();
			
			lua_getfield(L, viewport, "w");
			mProjections[i].viewport().w = L.to<float>(-1);
			L.pop();
			
			lua_getfield(L, viewport, "h");
			mProjections[i].viewport().h = L.to<float>(-1);
			L.pop();
			
		}
		L.pop(); // viewport
		
		lua_getfield(L, projection, "warp");
		if (lua_istable(L, -1)) {
			int warp = L.top();
			
			lua_getfield(L, warp, "width");
			if (lua_isnumber(L, -1)) {
				mProjections[i].warpwidth = lua_tonumber(L, -1);
			}
			L.pop();
			
			lua_getfield(L, warp, "height");
			if (lua_isnumber(L, -1)) {
				mProjections[i].warpheight = lua_tonumber(L, -1);
			}
			L.pop();
			
			lua_getfield(L, warp, "file");
			if (lua_isstring(L, -1)) {
				// load from file
				mProjections[i].readWarp(configpath + "/" + lua_tostring(L, -1));
			}
			L.pop();
		}
		L.pop(); // warp
		
		lua_getfield(L, projection, "blend");
		if (lua_istable(L, -1)) {
			int blend = L.top();
			lua_getfield(L, blend, "file");
			if (lua_isstring(L, -1)) {
				// load from file
				mProjections[i].readBlend(configpath + "/" + lua_tostring(L, -1));
			} else {
				// TODO: generate blend...
			}
			L.pop();
		}
		L.pop(); // blend
		
		lua_getfield(L, projection, "params");
		if (lua_istable(L, -1)) {
			int params = L.top();
			lua_getfield(L, params, "file");
			if (lua_isstring(L, -1)) {
				// load from file
				mProjections[i].readParameters(configpath + "/" + lua_tostring(L, -1)); //, true);
			} 
			L.pop();
		}
		L.pop(); // params
		
		lua_getfield(L, projection, "position");
		if (lua_istable(L, -1)) {
			int position = L.top();
			lua_rawgeti(L, position, 1);
			mProjections[i].position.x = L.to<double>(-1);
			L.pop();
			lua_rawgeti(L, position, 2);
			mProjections[i].position.y = L.to<double>(-1);
			L.pop();
			lua_rawgeti(L, position, 3);
			mProjections[i].position.z = L.to<double>(-1);
			L.pop();			
		}
		L.pop(); // position

		
		L.pop(); // projector
	}
	
	L.pop(); // the projections table
	return *this;
}

void OmniStereo::onCreate() {
	
	// force allocation of warp/blend textures:
	for (unsigned i=0; i<4; i++) {
		mProjections[i].onCreate();
	}

	Shader cubeV, cubeF;
	cubeV.source(vGeneric, Shader::VERTEX).compile();
	cubeF.source(fCube, Shader::FRAGMENT).compile();
	mCubeProgram.attach(cubeV).attach(cubeF);
	mCubeProgram.link(false);	// false means do not validate
	// set uniforms before validating to prevent validation error
	mCubeProgram.begin();
		mCubeProgram.uniform("alphaMap", 2);
		mCubeProgram.uniform("pixelMap", 1);
		mCubeProgram.uniform("cubeMap", 0);
	mCubeProgram.end();
	mCubeProgram.validate();
	cubeV.printLog();
	cubeF.printLog();
	mCubeProgram.printLog();
	Graphics::error("cube program onCreate");

	Shader sphereV, sphereF;
	sphereV.source(vGeneric, Shader::VERTEX).compile();
	sphereF.source(fSphere, Shader::FRAGMENT).compile();
	mSphereProgram.attach(sphereV).attach(sphereF);
	mSphereProgram.link(false);	// false means do not validate
	// set uniforms before validating to prevent validation error
	mSphereProgram.begin();
		mSphereProgram.uniform("alphaMap", 2);
		mSphereProgram.uniform("pixelMap", 1);
		mSphereProgram.uniform("sphereMap", 0);
	mSphereProgram.end();
	mSphereProgram.validate();
	sphereV.printLog();
	sphereF.printLog();
	mSphereProgram.printLog();
	Graphics::error("cube program onCreate");

	Shader warpV, warpF;
	warpV.source(vGeneric, Shader::VERTEX).compile();
	warpF.source(fWarp, Shader::FRAGMENT).compile();
	mWarpProgram.attach(warpV).attach(warpF);
	mWarpProgram.link(false);	// false means do not validate
	// set uniforms before validating to prevent validation error
	mWarpProgram.begin();
		mWarpProgram.uniform("alphaMap", 2);
		mWarpProgram.uniform("pixelMap", 1);
	mWarpProgram.end();
	mWarpProgram.validate();
	warpV.printLog();
	warpF.printLog();
	mWarpProgram.printLog();
	Graphics::error("cube program onCreate");

	Shader demoV, demoF;
	demoV.source(vGeneric, Shader::VERTEX).compile();
	demoF.source(fDemo, Shader::FRAGMENT).compile();
	mDemoProgram.attach(demoV).attach(demoF);
	mDemoProgram.link(false);	// false means do not validate
	// set uniforms before validating to prevent validation error
	mDemoProgram.begin();
		mDemoProgram.uniform("alphaMap", 2);
		mDemoProgram.uniform("pixelMap", 1);
	mDemoProgram.end();
	mDemoProgram.validate();
	demoV.printLog();
	demoF.printLog();
	mDemoProgram.printLog();
	Graphics::error("cube program onCreate");
	
	// create cubemap textures:
	glGenTextures(2, mTex);
	for (int i=0; i<2; i++) {
		// create cubemap texture:
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTex[i]);
		
		// each cube face should clamp at texture edges:
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		
		// filtering
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		// TODO: verify? 
		// Domagoj also has:
		glTexGeni( GL_S, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR );
		glTexGeni( GL_T, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR );
		glTexGeni( GL_R, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR );
		float X[4] = { 1,0,0,0 };
		float Y[4] = { 0,1,0,0 };
		float Z[4] = { 0,0,1,0 };
		glTexGenfv( GL_S, GL_OBJECT_PLANE, X );
		glTexGenfv( GL_T, GL_OBJECT_PLANE, Y );
		glTexGenfv( GL_R, GL_OBJECT_PLANE, Z );

		// RGBA8 Cubemap texture, 24 bit depth texture, mResolution x mResolution
		// NULL means reserve texture memory, but texels are undefined
		for (int f=0; f<6; f++) {
			glTexImage2D(
				GL_TEXTURE_CUBE_MAP_POSITIVE_X+f, 
				0, GL_RGBA8, 
				mResolution, mResolution, 
				0, GL_BGRA, GL_UNSIGNED_BYTE, 
				NULL);
		}
		
		// clean up:
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		Graphics::error("creating cubemap texture");
	}
	
	// one FBO to rule them all...
	glGenFramebuffers(1, &mFbo);
	glBindFramebuffer(GL_FRAMEBUFFER, mFbo);
	//Attach one of the faces of the Cubemap texture to this FBO
	glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, mTex[0], 0);
	
	glGenRenderbuffers(1, &mRbo);
	glBindRenderbuffer(GL_RENDERBUFFER, mRbo);
	glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mResolution, mResolution);
	// Attach depth buffer to FBO
	glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, mRbo);
	
	// ...and in the darkness bind them:
	for (mFace=0; mFace<6; mFace++) {
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+mFace, GL_TEXTURE_CUBE_MAP_POSITIVE_X+mFace, mTex[0], 0);
	}
	
	//Does the GPU support current FBO configuration?
	GLenum status;
	status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
	if (status != GL_FRAMEBUFFER_COMPLETE) {
		printf("GPU does not support required FBO configuration\n");
		exit(0);
	}
	
	// cleanup:
	glBindRenderbuffer(GL_RENDERBUFFER, 0);
	glBindFramebuffer(GL_FRAMEBUFFER, 0);
	
	Graphics::error("OmniStereo onCreate");
}

void OmniStereo::onDestroy() {
	mCubeProgram.destroy();

	glDeleteTextures(2, mTex);
	mTex[0] = mTex[1] = 0;
	
	glDeleteRenderbuffers(1, &mRbo);
	glDeleteFramebuffers(1, &mFbo);
	mRbo = mFbo = 0;
}

void OmniStereo::capture(OmniStereo::Drawable& drawable, const Lens& lens, const Pose& pose) {
	if (mCubeProgram.id() == 0) onCreate();
	gl.error("OmniStereo capture begin");
	
	Vec3d pos = pose.pos();
	Vec3d ux, uy, uz; 
	pose.unitVectors(ux, uy, uz);
	mModelView = Matrix4d::lookAt(ux, uy, uz, pos);
	
	mNear = lens.near();
	mFar = lens.far();
	const double eyeSep = mStereo ? lens.eyeSep() : 0.;
	
	gl.projection(Matrix4d::identity());
	
	// apply camera transform:
	gl.pushMatrix(gl.MODELVIEW);
	gl.loadMatrix(mModelView);
	glPushAttrib(GL_ALL_ATTRIB_BITS);
	glBindFramebuffer(GL_FRAMEBUFFER, mFbo);
	gl.viewport(0, 0, mResolution, mResolution);
	
	for (int i=0; i<(mStereo+1); i++) {
		mEyeParallax = eyeSep * (i-0.5);
		for (mFace=0; mFace<6; mFace++) {
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0 + mFace);
			glFramebufferTexture2D(
				GL_FRAMEBUFFER, 
				GL_COLOR_ATTACHMENT0 + mFace, 
				GL_TEXTURE_CUBE_MAP_POSITIVE_X + mFace, 
				mTex[i], 0);
			
			gl.clearColor(mClearColor);
			gl.depthTesting(1);
			gl.depthMask(1);
			gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
			drawable.onDrawOmni(*this);		
		}
	}
	
	glBindFramebuffer(GL_FRAMEBUFFER, 0);
	glPopAttrib();
	gl.popMatrix(gl.MODELVIEW);
	gl.error("OmniStereo capture end");
	
	// FBOs don't generate mipmaps by default; do it here:
	if (mMipmap) {
		glActiveTexture(GL_TEXTURE0);
		glEnable(GL_TEXTURE_CUBE_MAP);
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTex[0]);
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		gl.error("generating mipmap");
		
		if (mStereo) {
			glBindTexture(GL_TEXTURE_CUBE_MAP, mTex[1]);
			glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
			gl.error("generating mipmap");
		}
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		glDisable(GL_TEXTURE_CUBE_MAP);
	}
	gl.error("OmniStereo FBO mipmap end");
}	

void OmniStereo::onFrameFront(OmniStereo::Drawable& drawable, const Lens& lens, const Pose& pose, const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.error("OmniStereo onFrameFront begin");
	
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		
		gl.projection(Matrix4d::perspective(lens.fovy(), viewport.w / (float)viewport.h, lens.near(), lens.far()));
		
		mFace = 5; // draw negative z
		
		{
			Vec3d pos = pose.pos();
			Vec3d ux, uy, uz; 
			pose.unitVectors(ux, uy, uz);
			mModelView = Matrix4d::lookAt(-ux, -uy, uz, pos);
			
			mNear = lens.near();
			mFar = lens.far();
			//const double eyeSep = mStereo ? lens.eyeSep() : 0.;
			
			// apply camera transform:
			gl.modelView(mModelView);
			gl.clearColor(mClearColor);
			gl.depthTesting(1);
			gl.depthMask(1);
			gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
			
			drawable.onDrawOmni(*this);	
		}
	}
	gl.error("OmniStereo onFrameFront end");
}	

void OmniStereo::drawEye(const Pose& pose, double eye) {
	if (eye > 0.) {
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTex[1]);
	} else {
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTex[0]);
	}
	gl.error("OmniStereo drawEye after texture");
	gl.draw(mQuad);
	gl.error("OmniStereo drawEye after quad");
}

void OmniStereo::draw(const Lens& lens, const Pose& pose, const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.error("OmniStereo draw begin");
	
	gl.viewport(vp);
	gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		gl.clear(Graphics::COLOR_BUFFER_BIT | Graphics::DEPTH_BUFFER_BIT);
		p.blend().bind(2);
		p.warp().bind(1);
	
		gl.error("OmniStereo cube draw begin");
		
		mCubeProgram.begin();
		glActiveTexture(GL_TEXTURE0);
		glEnable(GL_TEXTURE_CUBE_MAP);	
		
		gl.error("OmniStereo cube drawStereo begin");
		
		drawStereo<&OmniStereo::drawEye>(lens, pose, viewport);		
		
		gl.error("OmniStereo cube drawStereo end");
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		glDisable(GL_TEXTURE_CUBE_MAP);
		
		mCubeProgram.end();
		gl.error("OmniStereo cube draw end");
		
		p.blend().unbind(2);
		p.warp().unbind(1);
	}
	gl.error("OmniStereo draw end");
}

void OmniStereo::drawQuadEye(const Pose& pose, double eye) {
	gl.draw(mQuad);
}

void OmniStereo::drawSphereMap(Texture& map, const Lens& lens, const Pose& pose, const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.viewport(vp);
	gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		gl.clear(Graphics::COLOR_BUFFER_BIT | Graphics::DEPTH_BUFFER_BIT);
		p.blend().bind(2);
		p.warp().bind(1);
		
		map.bind(0);
		mSphereProgram.begin();
		mSphereProgram.uniform("quat", pose.quat());
		
		drawQuad();
			
		mSphereProgram.end();
		map.unbind(0);
		
		p.blend().unbind(2);
		p.warp().unbind(1);
	}
}

void OmniStereo::drawDemoEye(const Pose& pose, double eye) {
	mDemoProgram.uniform("eyesep", eye);
	mDemoProgram.uniform("pos", pose.pos()); 
	mDemoProgram.uniform("quat", pose.quat());
	gl.draw(mQuad);
}

void OmniStereo::drawDemo(const Lens& lens, const Pose& pose, const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.viewport(vp);
	gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		gl.clear(Graphics::COLOR_BUFFER_BIT | Graphics::DEPTH_BUFFER_BIT);
		p.blend().bind(2);
		p.warp().bind(1);
		
		mDemoProgram.begin();
		
		drawStereo<&OmniStereo::drawDemoEye>(lens, pose, viewport);	
		
		mDemoProgram.end();
		
		p.blend().unbind(2);
		p.warp().unbind(1);
	}
}

void OmniStereo::drawWarp(const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.viewport(vp);
	gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		gl.clear(Graphics::COLOR_BUFFER_BIT | Graphics::DEPTH_BUFFER_BIT);
		p.blend().bind(2);
		p.warp().bind(1);
		
		mWarpProgram.begin();
		
		drawQuad();
		
		mWarpProgram.end();
		
		p.blend().unbind(2);
		p.warp().unbind(1);
	}
}

void OmniStereo::drawBlend(const Viewport& vp) {
	mFrame++;
	if (mCubeProgram.id() == 0) onCreate();
	
	gl.viewport(vp);
	gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
	for (int i=0; i<numProjections(); i++) {
		Projection& p = projection(i);
		Viewport& v = p.viewport();
		Viewport viewport(
			vp.l + v.l * vp.w,
			vp.b + v.b * vp.h,
			v.w * vp.w,
			v.h * vp.h
		);
		gl.viewport(viewport);
		gl.clear(Graphics::COLOR_BUFFER_BIT | Graphics::DEPTH_BUFFER_BIT);
		
		gl.projection(Matrix4d::ortho(0, 1, 0, 1, -1, 1));
		gl.modelView(Matrix4d::identity());
		
		p.blend().bind(0);
		
		drawQuad();
		
		p.blend().unbind(0);
	}
}

void OmniStereo::drawQuad() {
	switch (mMode) {
		case ACTIVE:
			glDrawBuffer(GL_BACK_RIGHT);
			gl.draw(mQuad);
			
			glDrawBuffer(GL_BACK_LEFT);
			gl.draw(mQuad);
			
			glDrawBuffer(GL_BACK);
			break;
		
		case DUAL:
			// TODO:
			break;
		
		default:
			gl.draw(mQuad);
			break;
	}
}

