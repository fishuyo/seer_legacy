
// default attribute names
attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

// default matrix uniforms
uniform mat4 u_projectionViewMatrix;
// uniform mat4 u_modelViewMatrix;
// uniform mat4 u_viewMatrix;
// uniform mat4 u_modelMatrix;
// uniform mat4 u_normalMatrix;
// uniform vec4 u_cameraPosition;

varying vec2 v_uv;

void main() {
  gl_Position = u_projectionViewMatrix * a_position;
  v_uv = a_texCoord0;
}

