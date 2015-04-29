#version 100
///
// Textured fragment shader with transparency.
///
precision mediump float;

varying vec2 v_texCoord;
uniform sampler2D u_tex0;

void main()
{
    vec4 clr = texture2D(u_tex0, v_texCoord);;
    if (clr.a < 0.5)
        discard;
    gl_FragColor = clr;
}
