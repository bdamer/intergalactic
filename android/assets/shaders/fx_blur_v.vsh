#version 100
///
// Vertical blur fragment shader based on http://xissburg.com/faster-gaussian-blur-in-glsl/.
///
attribute vec2 a_texCoord;

varying vec2 v_texCoord;
varying vec2 v_blurTexCoords[14];

void main()
{
    // Transform UV to [-1/-1..1/1] screen coordinates
    gl_Position = vec4(2.0 * a_texCoord.x - 1.0, 2.0 * a_texCoord.y - 1.0, 0, 1);

    // Precompute blur coordiantes
    v_texCoord = a_texCoord;
    v_blurTexCoords[ 0] = v_texCoord + vec2(0.0, -0.028);
    v_blurTexCoords[ 1] = v_texCoord + vec2(0.0, -0.024);
    v_blurTexCoords[ 2] = v_texCoord + vec2(0.0, -0.020);
    v_blurTexCoords[ 3] = v_texCoord + vec2(0.0, -0.016);
    v_blurTexCoords[ 4] = v_texCoord + vec2(0.0, -0.012);
    v_blurTexCoords[ 5] = v_texCoord + vec2(0.0, -0.008);
    v_blurTexCoords[ 6] = v_texCoord + vec2(0.0, -0.004);
    v_blurTexCoords[ 7] = v_texCoord + vec2(0.0,  0.004);
    v_blurTexCoords[ 8] = v_texCoord + vec2(0.0,  0.008);
    v_blurTexCoords[ 9] = v_texCoord + vec2(0.0,  0.012);
    v_blurTexCoords[10] = v_texCoord + vec2(0.0,  0.016);
    v_blurTexCoords[11] = v_texCoord + vec2(0.0,  0.020);
    v_blurTexCoords[12] = v_texCoord + vec2(0.0,  0.024);
    v_blurTexCoords[13] = v_texCoord + vec2(0.0,  0.028);
}