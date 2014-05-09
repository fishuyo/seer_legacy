

package com.fishuyo.seer
package allosphere


object OmniApp {
    // Vertex Shader

	  val vert = """
      attribute vec3 a_position;
      attribute vec3 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform int u_hasColor;
      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_viewMatrix;
      uniform mat4 u_modelMatrix;
      uniform mat4 u_normalMatrix;
      uniform vec4 u_cameraPosition;

      uniform vec3 u_lightPosition;

      varying vec4 v_color;
      varying vec3 v_normal, v_pos, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
      varying float v_fog;

      void main(){
        // if( u_hasColor == 0){
        if( a_color.xyz == vec3(0,0,0)){
          v_color = u_color;
        } else {
          v_color = a_color;
        }

        vec4 pos = u_modelViewMatrix * vec4(a_position,1);
        v_pos = vec3(pos) / pos.w;

        v_normal = vec3(u_normalMatrix * vec4(a_normal,0));
        
        v_eyeVec = normalize(-pos.xyz);

        v_lightDir = vec3(u_viewMatrix * vec4(u_lightPosition,0));

        v_texCoord = a_texCoord0;
        gl_Position = omni_render(pos); 
      }
    """
    // Fragment Shader
  val frag = """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;

      uniform float u_alpha;
      uniform float u_fade;
      uniform float u_textureMix;
      uniform float u_lightingMix;
      uniform vec4 u_lightAmbient;
      uniform vec4 u_lightDiffuse;
      uniform vec4 u_lightSpecular;
      uniform float u_shininess;

      varying vec2 v_texCoord;
      varying vec3 v_normal;
      varying vec3 v_eyeVec;
      varying vec3 v_lightDir;
      varying vec4 v_color;
      varying vec3 v_pos;

      void main() {
        
        vec4 colorMixed;
        if( u_textureMix > 0.0){
          vec4 textureColor = texture2D(u_texture0, v_texCoord);
          colorMixed = mix(v_color, textureColor, u_textureMix);
        }else{
          colorMixed = v_color;
        }

        vec4 final_color = colorMixed * u_lightAmbient;

        vec3 N = normalize(v_normal);
        vec3 L = normalize(v_lightDir);

        float lambertTerm = dot(N,L);
        final_color += u_lightDiffuse * colorMixed * max(lambertTerm,0.0);

        float specularTerm = 0.0;

        //phong
        vec3 R = reflect(-L, N);
        vec3 E = normalize(v_eyeVec); //normalize(-v_pos);
        //float specAngle = max(dot(R,E), 0.0);
        //specularTerm = pow(specAngle, 8.0);

        //blinn
        float halfDotView = max(0.0, dot(N, normalize(L + E)));
        specularTerm = pow(halfDotView, 20.0);
        specularTerm = specularTerm * smoothstep(0.0,0.2,lambertTerm);

        final_color += u_lightSpecular * specularTerm;
        gl_FragColor = mix(colorMixed, final_color, u_lightingMix);
        gl_FragColor *= (1.0 - u_fade);
        gl_FragColor.a *= u_alpha;
        // gl_FragColor = vec4(1,0,0,1);

      }
    """
}

// // class OmniNode extends RenderNode with OmniDrawable {

// //   var viewport = new Viewport(0,0,800,800)
// //   var scene = new Scene
// //   var camera:NavCamera = new OrthographicCamera(2,2)

// //   var buffer:Option[FrameBuffer] = None
// //   var shader = "basic"

// //   def createBuffer(){
// //     if(buffer.isEmpty) buffer = Some(FrameBuffer(viewport.w, viewport.h))
// //   }

// //   def bindBuffer(i:Int) = buffer.get.getColorBufferTexture().bind(i)

// //   def resize(vp:Viewport){
// //     viewport = vp
// //     if(camera.viewportHeight == 1.f){
// //       camera.viewportWidth = vp.aspect
// //     }else{
// //       // camera.viewportWidth = vp.w
// //       // camera.viewportHeight = vp.h
// //     }

// //     if(buffer.isDefined){
// //       buffer.get.dispose
// //       buffer = Some(FrameBuffer(vp.w,vp.h))
// //     }
// //   }

// //   def addInput(node:RenderNode){
// //     inputs += node
// //   }
// //   def outputTo(node:RenderNode){
// //     createBuffer()
// //     outputs += node
// //     node.addInput(this)
// //   }

// //   def animate(dt:Float){
// //     scene.animate(dt)
// //     camera.step(dt)
// //   }

// //   def render(){
// //     if( buffer.isDefined ){
// //       buffer.get.begin()

// //       if( clear ) Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
// //       else Gdx.gl.glClear( GL20.GL_DEPTH_BUFFER_BIT)

// //       nodes.foreach(_.render()) //hacky
      
// //     }

// //     try{
// //       Shader(shader).begin() 

// //       inputs.zipWithIndex.foreach( (i) => {
// //         // i._1.buffer.get.getColorBufferTexture().bind(i._2) 
// //         i._1.bindBuffer(i._2) 
// //         Shader.shader.get.uniforms("u_texture"+i._2) = i._2
// //       })

// //       MatrixStack.clear()
// //       Shader.setCamera(camera)
// //       Shader.setMatrices()
// //       if(active){
// //         Shader.alpha = scene.alpha
// //         Shader.fade = scene.fade

// //         if( scene.alpha < 1.f ){ //TODO depth ordering, conflicts with depth flag
// //           Shader.blend = true
// //           Gdx.gl.glEnable(GL20.GL_BLEND);
// //           Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
// //         }else {
// //           Shader.blend = false
// //           Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
// //           Gdx.gl.glDisable( GL20.GL_BLEND )
// //         }

// //         if(depth) Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
// //         else Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )

// //         scene.draw()
// //       }
      
// //       Shader().end()
// //     } catch{ case e:Exception => println(e)
// //       // println ("\n" + e.printStackTrace + "\n")
// //     }

// //     if( buffer.isDefined ) buffer.get.end()
// //   }
// // }


// class OmniApp extends Animatable with OmniDrawable {

// 	val omni = new OmniStereo
// 	var omniEnabled = true

// 	val lens = new Lens()
// 	lens.near = 0.01
// 	lens.far = 40.0
// 	lens.eyeSep = 0.03

// 	var omniShader:Shader = _

//   var mode = "omni"

// 	// omni.mStereo = 1
// 	// omni.mMode = omni.StereoMode.ACTIVE

// 	override def init(){
//     if( omniShader == null){
//       omniShader = Shader.load("omni", OmniStereo.glsl + OmniApp.vert, OmniApp.frag )
//       omni.onCreate
//     }		
// 	}

// 	override def draw(){
		
// 		if( omniShader == null){ init()}
// 		val vp = Viewport(Window.width, Window.height)

// 		// omni.drawWarp(vp)
// 		// omni.drawDemo(lens,Camera.nav,vp)

// 		// onDrawOmni()

// 		// omni.drawSphereMap(t, lens, Camera.nav, vp)

// 		if (omniEnabled) {
// 			omni.onFrame(this, lens, Camera.nav, vp);
// 		} else {
// 			omni.onFrameFront(this, lens, Camera.nav, vp);
// 		}
// 	}

// 	override def onDrawOmni(){
// 		Shader("omni").begin
// 		omni.uniforms(omniShader);


		
// 		Shader("omni").end
// 	}

// }
