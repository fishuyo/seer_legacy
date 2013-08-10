

attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform vec4 u_color;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelViewMatrix;
uniform mat4 u_normalMatrix;

varying vec2 v_texCoords;

void main()
{
  vec4 color = u_color;

  // pass through the texture coordinate
  v_texCoords = a_texCoord0;
  
  // pass through the quad position
  gl_Position = u_projectionViewMatrix * a_position;
}
