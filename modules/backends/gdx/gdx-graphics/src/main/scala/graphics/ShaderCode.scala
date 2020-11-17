package seer
package graphics

/**
  * Shader code split into composable fragments
  */

object ShaderSegments {
  val attributes = Map("all" -> """
    attribute vec3 a_position;
    attribute vec3 a_normal;
    attribute vec4 a_color;
    attribute vec2 a_texCoord0;
  """)

  val matrixUniforms = Map("all" -> """
    uniform mat4 u_projectionViewMatrix;
    uniform mat4 u_modelViewMatrix;
    uniform mat4 u_viewMatrix;
    uniform mat4 u_modelMatrix;
    uniform mat4 u_normalMatrix;
    //uniform vec4 u_cameraPosition;
  """)

  val lightUniforms = Map("simple" -> """
    uniform vec4 u_color;
    uniform vec3 u_lightPosition;
  """)

  val basicVarying = """
      varying vec4 v_color;
      varying vec3 v_normal, v_pos, v_lightDir, v_eyeVec;
      varying vec2 v_texCoord;
      varying float v_fog;
  """


}

object DefaultShaders {

  import ShaderSegments._

  val empty = (
    """
      attribute vec4 a_position;

      uniform mat4 u_projectionViewMatrix;

      void main(){        
        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      void main(){
        gl_FragColor = vec4(1.0,1.0,1.0,1.0);
      }
    """
  )
  
  val basic = (
    // Vertex Shader
    attributes("all") +
    """
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
        if( u_hasColor == 0){
        //if( a_color.xyz == vec3(0,0,0)){
          v_color = u_color;
        } else {
          v_color = a_color*u_color;
        }

        vec4 pos = u_modelViewMatrix * vec4(a_position,1);
        v_pos = vec3(pos) / pos.w;

        float pointSize = 2.0 / length(v_pos); // CORRECT! // was 5.0
        gl_PointSize = max(pointSize, 1.0);

        v_normal = vec3(u_normalMatrix * vec4(a_normal,0));
        
        v_eyeVec = normalize(-pos.xyz);

        v_lightDir = vec3(u_viewMatrix * vec4(u_lightPosition,0));

        v_texCoord = a_texCoord0;

        vec3 flen = u_cameraPosition.xyz - pos.xyz;
        float fog = dot(flen, flen); // * u_cameraPosition.w;
        // v_fog = min(fog, 1.0);
        float fogDensity = 0.035;
        v_fog = 1.0-clamp(exp(-pow(fogDensity*length(flen), 2.0)), 0.0, 1.0);

        gl_Position = u_projectionViewMatrix * vec4(a_position,1); 
      }
    """,
    // Fragment Shader
    """
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
      varying float v_fog;

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

        gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(0,0,0), v_fog);
      }
    """
  )

  val toon = (
    // attributes("all") + matrixUniforms +
    """
      attribute vec4 a_position;
      attribute vec4 a_normal;
      attribute vec4 a_color;
      attribute vec2 a_texCoord0;

      uniform vec4 u_color;
      uniform mat4 u_projectionViewMatrix;
      uniform mat4 u_modelViewMatrix;
      uniform mat4 u_normalMatrix;
      uniform mat4 u_viewMatrix;

      uniform float u_useLocalZ;

      //invariant gl_Position;

      varying float v_depth;
      varying vec3 v_normal;
      varying vec3 v_eye;
      varying vec3 v_lightPosition;
      varying vec4 v_color;

      varying vec2 v_texCoords;

      void main()
      {
        // get the depth and eye position
        vec4 transformedVertex = u_projectionViewMatrix * a_position;
        vec4 transformedVertex2 = u_viewMatrix * a_position;

        if( u_useLocalZ == 1.0 ){
          v_depth = a_position.z;
        } else {
          v_depth = transformedVertex.z;
        }
        //v_depth = transformedVertex.z; //a_position.z;


        v_texCoords = a_texCoord0;

        v_eye = -(u_modelViewMatrix * a_position).xyz;//-transformedVertex.xyz;

        // transform normals to the current view
        v_normal = a_normal.xyz; //normalize(u_normalMatrix * a_normal).xyz;

        // pass the light position through
        v_lightPosition = vec3(10.0,10.0,10.0);

        if( a_color != vec4(0.0,0.0,0.0,1.0)){
          v_color = a_color;
        }else{
          v_color = u_color;  
        }

        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
    """
  )

  val texture = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;

      uniform mat4 u_projectionViewMatrix;

      varying vec2 v_texCoords;

      void main(){

        // pass through the texture coordinate
        v_texCoords = a_texCoord0;
        
        // pass through the quad position
        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      varying vec2 v_texCoords;

      void main(){

        gl_FragColor = texture2D(u_texture0, v_texCoords);

      }
    """
  )

  val composite = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;

      uniform mat4 u_projectionViewMatrix;

      varying vec2 v_texCoords;

      void main(){

        // pass through the texture coordinate
        v_texCoords = a_texCoord0;
        
        // pass through the quad position
        gl_Position = u_projectionViewMatrix * a_position;
      }
    """,
    """
      #ifdef GL_ES
       precision mediump float;
      #endif

      uniform sampler2D u_texture0;
      uniform sampler2D u_texture1;

      uniform float u_blend0;
      uniform float u_blend1;

      uniform int u_mode;

      varying vec2 v_texCoords;

      void main(){

        // pull everything we want from the textures
        vec4 color0 = texture2D(u_texture0, v_texCoords) * u_blend0;
        vec4 color1 = texture2D(u_texture1, v_texCoords) * u_blend1;

        if( u_mode == 0){
          gl_FragColor = color0 + color1;
        }else {
          gl_FragColor = color0 * color1;
        }
      }
    """
  )

  val text = (
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;
      attribute vec4 a_color;

      uniform mat4 u_projTrans;

      varying vec4 v_color;
      varying vec2 v_texCoord;

      void main() {
          gl_Position = u_projTrans * a_position;
          v_texCoord = a_texCoord0;
          v_color = a_color;
      }
    """,
    """
      uniform sampler2D u_texture;

      varying vec4 v_color;
      varying vec2 v_texCoord;

      uniform float smoothing; // = 1.0/16.0;
      uniform vec3 color;

      void main() {
          float distance = texture2D(u_texture, v_texCoord).a;
          float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
          // gl_FragColor = vec4(v_color.rgb*alpha/2.0, alpha);
          gl_FragColor = vec4(v_color.rgb*color*alpha*2.0, alpha);
      }
    """
  )
}
