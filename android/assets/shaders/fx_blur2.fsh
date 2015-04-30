#version 100
///
// Blur fragment shader based on https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson5
///
precision mediump float;

// input texture
uniform sampler2D u_tex0;
// amount of blur
uniform float u_blur;
// blur direction
uniform vec2 u_dir;

varying vec2 v_texCoord;

void main()
{
    // this will be our RGBA sum
    vec4 sum = vec4(0.0);

    // our original texcoord for this fragment
    vec2 tc = v_texCoord;

    float hstep = u_dir.x;
    float vstep = u_dir.y;

    // apply blurring, using a 9-tap filter with predefined gaussian weights
    sum += texture2D(u_tex0, vec2(tc.x - 4.0 * u_blur * hstep, tc.y - 4.0 * u_blur * vstep)) * 0.0162162162;
    sum += texture2D(u_tex0, vec2(tc.x - 3.0 * u_blur * hstep, tc.y - 3.0 * u_blur * vstep)) * 0.0540540541;
    sum += texture2D(u_tex0, vec2(tc.x - 2.0 * u_blur * hstep, tc.y - 2.0 * u_blur * vstep)) * 0.1216216216;
    sum += texture2D(u_tex0, vec2(tc.x - 1.0 * u_blur * hstep, tc.y - 1.0 * u_blur * vstep)) * 0.1945945946;
    sum += texture2D(u_tex0, vec2(tc.x, tc.y)) * 0.2270270270;
    sum += texture2D(u_tex0, vec2(tc.x + 1.0 * u_blur * hstep, tc.y + 1.0 * u_blur * vstep)) * 0.1945945946;
    sum += texture2D(u_tex0, vec2(tc.x + 2.0 * u_blur * hstep, tc.y + 2.0 * u_blur * vstep)) * 0.1216216216;
    sum += texture2D(u_tex0, vec2(tc.x + 3.0 * u_blur * hstep, tc.y + 3.0 * u_blur * vstep)) * 0.0540540541;
    sum += texture2D(u_tex0, vec2(tc.x + 4.0 * u_blur * hstep, tc.y + 4.0 * u_blur * vstep)) * 0.0162162162;

    gl_FragColor = vec4(sum.rgb, 1.0);
}