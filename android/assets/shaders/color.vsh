///
// Default passthrough vertex shader.
///
attribute vec4 a_position;
attribute vec4 a_color;

uniform mat4 u_worldView;

varying vec4 v_color;

void main()
{
    v_color = a_color;
    gl_Position = u_worldView * a_position;
}