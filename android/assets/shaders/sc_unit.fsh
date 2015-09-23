#version 100
///
// Textured unit fragment shader.
///
precision mediump float;

varying vec2 v_texCoord;
uniform vec4 u_color;
uniform sampler2D u_tex0;

void main()
{
    gl_FragColor = texture2D(u_tex0, v_texCoord) * u_color;
}