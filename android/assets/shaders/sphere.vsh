#version 100
///
// Textured vertex shader to map a spherical body.
///
attribute vec4 a_position;
uniform mat4 u_worldView;
uniform mat4 u_model;
varying vec4 v_texCoord;
 
void main()
{
    v_texCoord = a_position;
    gl_Position = u_worldView * u_model * a_position;
}