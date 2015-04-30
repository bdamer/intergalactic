#version 100
///
// Fragment shader for per-vertex lit body. 
///
precision mediump float;

uniform sampler2D u_tex0;
uniform float u_gradient;

varying vec4 v_texCoord;
varying float noise;

float random(vec3 scale, float seed)
{
    return fract(sin(dot(gl_FragCoord.xyz + seed, scale)) * 43758.5453 + seed);
}

void main()
{   
    // get a random offset
    float r = 0.01 * random(vec3(12.9898, 78.233, 151.7182), 0.0);
    // lookup vertically in the texture, using noise and offset to get the right color
    vec2 pos = vec2(u_gradient, 1.0 - 1.3 * noise + r);
    vec4 color = texture2D(u_tex0, pos);
    gl_FragColor = vec4(color.rgb, 1.0);
}