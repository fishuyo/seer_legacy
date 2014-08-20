
// based on shader by iq at https://www.shadertoy.com/view/4df3Rn

#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_uv;

uniform float time;
uniform float zoom;
uniform vec2 mouse;

void main(){

    vec2 p = -1.0 + 2.0 * v_uv.xy;

    float zoo = zoom;
    zoo = pow( zoo,8.0);
    vec2 cc = p*zoo + mouse;

    vec2 z  = vec2(0.0);
    float m2 = 0.0;
    float co = 0.0;

    for( int i=0; i<256; i++ ){
        if( m2<1024.0 ){
            z = cc + vec2( z.x*z.x - z.y*z.y, 2.0*z.x*z.y );
            m2 = dot(z,z);
            co += 1.0;
        }
    }

    co = co + 1.0 - log2(.5*log2(m2));

    co = sqrt(co/256.0);
    gl_FragColor = vec4( .5+.5*cos(6.2831*co+0.0),
                         .5+.5*cos(6.2831*co+sin(time)),
                         .5+.5*cos(6.2831*co+cos(time)),
                         1.0 );

}

