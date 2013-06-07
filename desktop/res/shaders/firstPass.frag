
uniform sampler2D u_texture0;
uniform sampler2D u_texture1;

uniform float u_near;
uniform float u_far;
uniform float u_useTexture;

varying vec2 v_texCoords;
varying float v_depth;
varying vec3 v_normal;
varying vec3 v_eye;
varying vec3 v_lightPosition;
varying vec4 v_color;

void main()
{
  // these values are set empirically based on the dimensions
  // of the bunny model
  float near = 0.0; //-1.1;
  float far = 4.1;

  //near = -1.1;
  //far =1.1;

  // get shifted versions for better visualization
  //float depthShift = (v_depth + 1.5)/2.1;
  float depthShift = ((v_depth - u_near) / (u_far - u_near));
  vec3 normalShift = (v_normal + vec3(1.0)) * 0.5;

  //vec3 textureColor = texture2D(u_texture0, vec2(1.0*v_texCoords.y,2.0*v_texCoords.x)).rgb;
  vec3 textureColor = texture2D(u_texture0, v_texCoords).rgb;
  
  vec3 textureColor1 = texture2D(u_texture1, vec2(1.0*v_texCoords.y,2.0*v_texCoords.x)).rgb;

  //gl_FragData[0] = v_color;
  gl_FragData[0] = vec4(normalShift, depthShift);
  //gl_FragData[0] = v_color*vec4(normalShift, depthShift);
  //gl_FragData[0] = vec4(normal, 1.0);
  //gl_FragData[0] = vec4(vec3(depthShift), 0.9);

  gl_FragData[0] = vec4( textureColor, depthShift);

  //gl_FragData[1] = vec4(normalize(eye), 1.0);
  //gl_FragData[2] = vec4(normalize(lightPosition), 1.0);

  //gl_FragData[0] = vec4(1.0);

  if( u_useTexture == 1.0){
  //  gl_FragData[0] = vec4( textureColor, depthShift);
  }else{
  //  gl_FragData[0] = vec4(normalShift, depthShift);
  }
  
  if( v_color != vec4(1.0,0.0,0.0,1.0) ){
    //gl_FragData[0] = vec4(v_color.xyz, depthShift);
  }

}
