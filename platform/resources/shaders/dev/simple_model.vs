#include <core.glsl>

layout (location = ATTRIBUTE_POSITION_LOCATION) in vec3 v_position;
layout (location = ATTRIBUTE_TEXTURE_LOCATION)  in vec2 v_texture;
layout (location = ATTRIBUTE_NORMAL_LOCATION)   in vec3 v_normal;

uniform mat4 u_model;

void main() {
    gl_Position = u_mat4_projection * u_mat4_view * u_model * vec4(v_position, 1.0f);
}