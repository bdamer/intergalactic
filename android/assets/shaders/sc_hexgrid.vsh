#version 100
///
// Hex Grid vertex shader.
///
attribute vec4 a_position;
attribute vec2 a_texCoord;
attribute vec4 a_color;

// Matrices
uniform mat4 u_mvp;

varying vec2 v_texCoord;
varying vec4 v_color;

void main()
{
    v_texCoord = a_texCoord;
    v_color = a_color;
    gl_Position = u_mvp * a_position;
}