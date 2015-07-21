#version 100
///
// Hex Grid fragment shader.
///
precision mediump float;

varying vec2 v_texCoord;
varying vec4 v_color;
uniform sampler2D u_tex0;

void main()
{
    gl_FragColor = texture2D(u_tex0, v_texCoord) * v_color;
}