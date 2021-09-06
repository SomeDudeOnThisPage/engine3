#include <core.glsl>
#include <core/model.glsl>

layout (location = ATTRIBUTE_POSITION_LOCATION) in vec3 v_position;
layout (location = ATTRIBUTE_TEXTURE_LOCATION)  in vec2 v_texture;
layout (location = ATTRIBUTE_NORMAL_LOCATION)   in vec3 v_normal;

out VS_OUT {
    vec2 f_texture;
} o;

void main() {
    o.f_texture = v_texture;
    gl_Position = u_mat4_projection * u_mat4_view * u_model * vec4(v_position, 1.0f);
}