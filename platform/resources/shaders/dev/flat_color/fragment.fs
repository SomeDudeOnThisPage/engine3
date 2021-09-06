#include <core.glsl>

struct Material_t {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

in VS_OUT {
    vec2 f_texture;
} i;

uniform Material_t u_material;

void main() {
    gl_FragColor = vec4(u_material.diffuse, 1.0f);
}