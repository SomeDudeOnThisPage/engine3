#ifndef CORE
    #define CORE
    #define ATTRIBUTE_POSITION_LOCATION 0 // #define ATTRIBUTE_POSITION_LOCATION IMPORT("render.attribute_position_location")
    #define ATTRIBUTE_TEXTURE_LOCATION 1
    #define ATTRIBUTE_COLOR_LOCATION 1
    #define ATTRIBUTE_NORMAL_LOCATION 2
    #define ATTRIBUTE_TANGENT_POSITION 3
#endif

layout (std140, binding = 0) uniform ub_PIPELINE_CORE {
    mat4 u_mat4_projection;
    mat4 u_mat4_view;
};