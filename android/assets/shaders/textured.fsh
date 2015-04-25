///
// Textured fragment shader.
///
precision mediump float;

varying vec2 v_texCoord;
uniform sampler2D u_tex0;

void main()
{
    gl_FragColor = texture(u_tex0, v_texCoord);
}
