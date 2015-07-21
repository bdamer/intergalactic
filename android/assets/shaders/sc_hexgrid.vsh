#version 100
///
// Hex Grid vertex shader.
///
attribute vec4 a_position;
attribute vec2 a_texCoord;

// Matrices
uniform mat4 u_mvp;

varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord;
    gl_Position = u_mvp * a_position;
}