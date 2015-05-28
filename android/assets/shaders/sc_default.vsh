#version 100
///
// Default passthrough vertex shader.
///
attribute vec4 a_position;
uniform mat4 u_mvp;
uniform vec4 u_color;

void main()
{
    gl_Position = u_mvp * a_position;
}