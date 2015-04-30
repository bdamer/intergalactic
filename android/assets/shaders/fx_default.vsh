#version 100
///
// Fullscreen fx pass vertex shader.
///
attribute vec2 a_texCoord;
// Scale factor (0 for no scaling)
uniform float u_scale;
varying vec2 v_texCoord;

void main()
{
    // Transform UV to [-1/-1..1/1] screen coordinates
    gl_Position = vec4(2.0 * a_texCoord.xy - 1.0, 0, 1);
    // Perform scaling
    v_texCoord = a_texCoord - gl_Position.xy * u_scale;
}