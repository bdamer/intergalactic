#version 100
///
// Textured vertex shader.
///
attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec2 a_texCoord;

// Matrices
uniform mat4 u_mvp;

// Out
varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord;
    gl_Position = u_mvp * a_position;
}