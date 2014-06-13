
package com.fishuyo.seer
package allosphere

object OmniShader {

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
		attribute vec4 a_position;
		attribute vec2 a_texCoord0;
		attribute vec4 a_color;

		varying vec2 T;
		void main(void) {
			// pass through the texture coordinate (normalized pixel):
			// T = vec2(gl_MultiTexCoord0);
			T = vec2(a_texCoord0);
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
