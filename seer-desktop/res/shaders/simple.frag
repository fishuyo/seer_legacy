varying float diffuse;
varying float spec;
varying vec3 ecPosition;

void main()
{
    gl_FragColor = vec4(ecPosition,1.0);//vec4(1.0, 0.5, 0.3, 1.0);//*diffuse + vec4(1.0,1.0,1.0,1.0) * spec;
}