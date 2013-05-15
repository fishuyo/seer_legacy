
varying float v_depth;
varying vec3 v_normal;
varying vec3 v_eye;
varying vec3 v_lightPosition;
varying vec4 v_color;

void main()
{
  // these values are set empirically based on the dimensions
  // of the bunny model
  float near = -1.1;
  float far = 1.1;

  // get shifted versions for better visualization
  //float depthShift = (v_depth + 1.5)/2.1;
  float depthShift = ((v_depth - near) / (far - near));
  vec3 normalShift = (v_normal + vec3(1.0)) * 0.5;

  //gl_FragData[0] = color;
  gl_FragData[0] = vec4(normalShift, depthShift);
  //gl_FragData[0] = v_color*vec4(normalShift, depthShift);
  //gl_FragData[0] = vec4(normal, 1.0);
  //gl_FragData[0] = vec4(vec3(depthShift), 0.9);
  //gl_FragData[1] = vec4(normalize(eye), 1.0);
  //gl_FragData[2] = vec4(normalize(lightPosition), 1.0);
}
