///
// Textured vertex shader.
///
attribute vec4 a_position;
attribute vec2 a_texCoord;
uniform mat4 u_worldView;
varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord;
    gl_Position = u_worldView * a_position;
}