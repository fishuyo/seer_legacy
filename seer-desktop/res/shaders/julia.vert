
const vec3 LightPosition = vec3(0.0,0.0,4.0); 
const float ks = 0.3; 
const float kd = 1.0 - ks; 
const float shiny = 16.0; 

varying float LightIntensity; 
varying vec3  Position;
varying vec4 texCoord;

void main() 
{ 
    vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex); 
    vec3 tnorm      = normalize(gl_NormalMatrix * gl_Normal); 
    vec3 lightVec   = normalize(LightPosition - ecPosition); 
    vec3 reflectVec = reflect(-lightVec, tnorm); 
    vec3 viewVec    = normalize(-ecPosition); 
    float spec      = max(dot(reflectVec, viewVec), 0.0); 
    spec            = pow(spec, shiny);

    LightIntensity  = kd * max(dot(lightVec, tnorm), 0.0) + ks * spec; 
    Position        = vec3(gl_MultiTexCoord0 - 0.5) * 5.0; 
    gl_Position     = ftransform();
    texCoord = gl_MultiTexCoord0; 
} 