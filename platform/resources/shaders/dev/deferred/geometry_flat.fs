#include <core.glsl>

out vec4 POSITION;
out vec4 NORMAL;
out vec4 ALBEDO;
out vec4 METALLIC_ROUGHNESS_AO_EMISSIVE;

struct Material_t {
    vec3 diffuse;
    float roughness;
    float metallic;
    float ao;
    float emissive;
};

in VS_OUT {
    vec3 f_position;
    vec3 v_normal;
    vec2 f_texture;
} i;

uniform Material_t u_material;

void main() {
    // gl_FragData[0] = vec4(1.0f);
    POSITION = vec4(i.f_position, 1.0f);
    NORMAL = vec4(i.v_normal, 1.0f);
    ALBEDO = vec4(u_material.diffuse, 1.0f);
    METALLIC_ROUGHNESS_AO_EMISSIVE = vec4(u_material.metallic, u_material.roughness, u_material.ao, u_material.emissive);
}
