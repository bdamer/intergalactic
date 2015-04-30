#version 100
///
// Fullscreen glow composite fx pass.
///
precision mediump float;

varying vec2 v_texCoord;
// Base texture from frame buffer
uniform sampler2D u_tex0;
// Glow texture
uniform sampler2D u_tex1;

void main()
{
    vec4 base = texture2D(u_tex0, v_texCoord);  
    vec4 detail = texture2D(u_tex1, v_texCoord);
    //gl_FragColor = mix(base, detail, 0.8);
    gl_FragColor = 1.0 - (1.0 - base) * (1.0 - detail);
}