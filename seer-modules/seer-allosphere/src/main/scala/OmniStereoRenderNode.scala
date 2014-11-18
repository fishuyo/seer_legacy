
package com.fishuyo.seer
package allosphere

import graphics._

class OmniStereoRenderer extends graphics.Renderer with OmniDrawable {

  org.lwjgl.opengl.SetDisplayStereo()

  val omni = new OmniStereo
  shader = Shader.load(OmniShader.glsl + SS.vert, SS.frag )

  var omniEnabled = true

  val lens = new Lens()
  lens.near = 0.01
  lens.far = 40.0
  lens.eyeSep = 0.03

  var mode = "omni"

  omni.configure("../../../../calibration-current", Hostname())
  omni.onCreate

  omni.mStereo = 0
  omni.mMode = StereoMode.MONO

  override def render(){
    Renderer() = this

    // val vp = Viewport(Window.width, Window.height)

    mode match {
      case "warp" => omni.drawWarp(viewport)
      case "demo" => omni.drawDemo(lens, camera.nav, viewport)
      case _ =>
        if (omniEnabled) {
          omni.onFrame(this, lens, camera.nav, viewport)
        } else {
          omni.onFrameFront(this, lens, camera.nav, viewport)
        }
    }
  }

  override def onDrawOmni(){
    shader.begin()

    MatrixStack.clear()
    setMatrixUniforms()
    setEnvironmentUniforms()
    environment.setGLState()
    setMaterialUniforms(material)
    
    shader.setUniforms() // set buffered uniforms in shader program
    omni.uniforms(shader);

    scene.draw()
    
    shader.end()
  }

}


class OmniCapture extends graphics.Renderer with OmniDrawable {

  val omni = new OmniStereo
  shader = Shader.load(OmniShader.glsl + SS.vert, SS.frag )

  val lens = new Lens()
  lens.near = 0.01
  lens.far = 40.0
  lens.eyeSep = 0.03

  omni.configure("../../../../calibration-current", Hostname())
  omni.onCreate

  omni.mStereo = 0
  omni.mMode = StereoMode.MONO

  override def render(){
    Renderer() = this
    omni.capture(this, lens, camera.nav)
  }

  override def onDrawOmni(){
    shader.begin()

    MatrixStack.clear()
    setMatrixUniforms()
    setEnvironmentUniforms()
    environment.setGLState()
    setMaterialUniforms(material)
    
    shader.setUniforms() // set buffered uniforms in shader program
    omni.uniforms(shader);

    scene.draw()
    
    shader.end()
  }

}

// XXX don't know if having a framebuffer target will work with stereo..
class OmniRender(val omni:OmniStereo) extends graphics.Renderer {

  org.lwjgl.opengl.SetDisplayStereo()

  val lens = new Lens()
  lens.near = 0.01
  lens.far = 40.0
  lens.eyeSep = 0.03

  override def render(){
    // Renderer() = this
    omni.draw(lens, camera.nav, viewport)
  }
}

object SS {
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
        gl_Position = omni_render(u_modelViewMatrix * vec4(a_position,1));
        // gl_Position = u_projectionViewMatrix * vec4(a_position,1); 
      }
    """

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

        // gl_FragColor = vec4(1,0,1,1); //(1.0 - u_fade);

      }
    """
}