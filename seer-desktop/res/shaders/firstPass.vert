

attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform vec4 u_color;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelViewMatrix;
uniform mat4 u_normalMatrix;

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

  if( u_useLocalZ == 1.0 ){
    v_depth = a_position.z;
  } else {
    v_depth = transformedVertex.z;
  }
  v_depth = a_position.z;


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

