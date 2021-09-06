#include <core.glsl>
#include <core/model.glsl>

layout (location = ATTRIBUTE_POSITION_LOCATION) in vec3 v_position;
layout (location = ATTRIBUTE_TEXTURE_LOCATION)  in vec2 v_texture;
layout (location = ATTRIBUTE_NORMAL_LOCATION)   in vec3 v_normal;

out VS_OUT {
    vec3 f_position;
    vec3 v_normal;
    vec2 f_texture;
} o;

void main() {
    vec4 position = u_mat4_projection * u_mat4_view * u_model * vec4(v_position, 1.0f);
    vec4 normal = u_model * vec4(v_normal, 1.0f);

    o.v_normal = normal.xyz;
    o.f_texture = v_texture;
    o.f_position = position.xyz;
    gl_Position = position;
}
