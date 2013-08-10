varying float diffuse;
varying float spec;

void main()
{
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);//*diffuse + vec4(1.0,1.0,1.0,1.0) * spec;
}