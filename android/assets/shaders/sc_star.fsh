#version 100
///
// Fragment shader for per-vertex lit body. 
///
precision mediump float;

varying vec4 v_color;

void main()
{
    gl_FragColor = v_color;
}