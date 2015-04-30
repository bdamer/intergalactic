#version 100
///
// Threshold fragment shader.
// http://www.curious-creature.com/2007/02/20/fast-image-processing-with-jogl/
///
precision mediump float;

uniform sampler2D u_tex0;
// luminance threshold - lower threshold will cause more bloom
uniform float u_threshold;
varying vec2 v_texCoord;

void main()
{
    vec3 luminanceVector = vec3(0.2125, 0.7154, 0.0721);
    vec4 sample = texture2D(u_tex0, v_texCoord);
    float luminance = dot(luminanceVector, sample.rgb);
    luminance = max(0.0, luminance - u_threshold);
    sample.rgb *= sign(luminance);
    sample.a = 1.0;
    gl_FragColor = sample;
}