
attribute vec4 a_position;
attribute vec4 a_normal;

uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelViewMatrix;
uniform mat4 u_normalMatrix;

const vec4 lightPos = vec4(4.0,4.0,0.0,0.0); 
const float ks = 0.3; 
const float kd = 1.0 - ks; 
const float shiny = 16.0; 

varying float diffuse;
varying float spec; 

void main() 
{ 
    vec3 ecPosition = vec3(u_modelViewMatrix * a_position); 
    vec3 tnorm      = vec3(normalize(u_normalMatrix * a_normal));
    vec3 norm       = faceforward( tnorm, ecPosition, tnorm); 
    vec3 lightVec   = normalize(vec3(u_modelViewMatrix * lightPos) - ecPosition); 
    vec3 reflectVec = reflect(-lightVec, tnorm); 
    vec3 viewVec    = normalize(-ecPosition); 
    spec      = max(dot(reflectVec, viewVec), 0.0); 
    spec            = pow(spec, shiny);

    diffuse = kd * max(dot(lightVec, tnorm), 0.0); 
     
    gl_Position = u_projectionViewMatrix * a_position;
} 