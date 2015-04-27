///
// Vertex shader for per-vertex lit body. 
///
attribute vec3 a_position;
attribute vec3 a_normal;

// Matrices
uniform mat4 u_mvp;
uniform mat4 u_model;

// Materials
uniform vec3 u_diffuse;

// Lights
uniform vec3 u_lightPos;        // directional light
uniform vec3 u_lightColor;
uniform float u_lightAmbient;    // ambient coefficient

varying vec4 v_color;

vec4 computeLight(vec4 normal)
{
    // Ambient
    vec3 ambient = u_lightAmbient * u_diffuse.rgb * u_lightColor;

    // Diffuse
    float diffuseCoefficient = max(dot(normal.xyz, -u_lightPos), 0.0);
    vec3 diffuse = diffuseCoefficient * u_diffuse.rgb * u_lightColor;

    // TODO: Specular
    float specularCoefficient = 0.0;
    //if (diffuseCoefficient > 0.0)
    //{
    //    specularCoefficient = 0.5
    //}
    vec3 specular = specularCoefficient * u_diffuse.rgb * u_lightColor;

    // Combine light components
    vec3 c = ambient + diffuse + specular;

    // perform gamma correction
    vec3 gamma = vec3(1.0 / 2.2);
    return vec4(pow(c, gamma), 1);
}

void main()
{
    // Convert vertex normal into camera space
    vec4 nv = normalize(u_model * vec4(a_normal, 0));
    v_color = computeLight(nv);

    // Transform the vertex
    gl_Position = u_mvp * vec4(a_position, 1);
}