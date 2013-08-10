uniform vec2 C;

varying vec4 texCoord;
varying vec3  Position; 
varying float LightIntensity; 

const float MaxIter = 100.0; 
const float Zoom = .75; 
const float Xcenter = 0.0; 
const float Ycenter = 0.0; 
const vec3  color1 = vec3(0.0,0.0,0.0);
const vec3  color2 = vec3(0.0,0.2,0.9); 
const vec3  color3 = vec3(0.8,0.8,0.8); 
const vec3  color4 = vec3(1.0,0.0,0.0);
const vec3  color5 = vec3(0.0,0.8,0.1); 
const vec3  color6 = vec3(0.6,0.6,0.0);

void main() 
{ 
    float   real  = Position.x * Zoom + Xcenter; 
    float   imag  = Position.y * Zoom + Ycenter; 
 
    float r2 = 0.0; 
    float i; 
    for (i = 0.0; i < MaxIter && r2 < 400.0; ++i) 
    { 
        float tempreal = real; 
        real = (tempreal * tempreal) - (imag * imag) + C[0]; 
        imag = 2.0 * tempreal * imag + C[1]; 
        r2   = (real * real) + (imag * imag); 
    } 
   
	float t = i - log(log(sqrt(r2)))/log(2.0);
	vec3 color;
    if (r2 < 400.0) 
        color = color2 * r2; 
    else 
    	//playing around with mix, settled on just the first component
        color = mix( mix(color3, color4, fract(t / 0.5) ), mix(color5,color6, r2 / 100.0), 0.0 ); 
    color *= LightIntensity;
    
    gl_FragColor = vec4(color, 1.0); 
} 