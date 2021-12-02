// #version 100

attribute vec3 a_position;
attribute vec4 a_color;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelViewMatrix;
uniform vec4 u_color;
varying vec4 v_color;

void main(){

  vec4 fire = a_color; //vec3(0.89,0.19,0.0);
  // fire.r -= abs(a_position.x*0.35);
  // fire.g += abs(a_position.x*0.15);
  // fire.b += abs(a_position.x*0.51);
  v_color = fire; //vec4(fire*0.7,1.0) * u_color;

  vec4 pos = u_modelViewMatrix * vec4(a_position,1);
  vec3 v_pos = vec3(pos) / pos.w;

  float pointSize = 30.0 / length(v_pos); // CORRECT! // was 5.0
  // float pointSize = 7.0*(3.0-a_position.y) / length(v_pos); // CORRECT! // was 5.0
  gl_PointSize = max(pointSize, 1.0);

  gl_Position = u_projectionViewMatrix * vec4(a_position,1.0); 
}