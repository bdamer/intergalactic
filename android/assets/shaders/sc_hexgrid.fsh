#version 100
///
// Hex Grid fragment shader.
///
precision mediump float;

varying vec2 v_texCoord;
uniform sampler2D u_tex0;
uniform vec4 u_color;

void main()
{
    gl_FragColor = texture2D(u_tex0, v_texCoord) * u_color;
}
