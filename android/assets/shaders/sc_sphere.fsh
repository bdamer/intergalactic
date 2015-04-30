#version 100
///
// Textured fragment shader to map a spherical body.
///
precision mediump float;

varying vec4 v_texCoord;
uniform sampler2D u_tex0;
uniform sampler2D u_tex1;

void main()
{
    vec2 longitudeLatitude = vec2((atan(v_texCoord.z, v_texCoord.x) / 3.1415926 + 1.0) * 0.5,
                                  (asin(v_texCoord.y) / 3.1415926 + 0.5));
    vec4 baseColor = texture2D(u_tex0, longitudeLatitude);
    vec4 detailColor = texture2D(u_tex1, longitudeLatitude);
    gl_FragColor = (baseColor + detailColor) / 2.0;
}