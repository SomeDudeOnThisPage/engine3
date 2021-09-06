#include <core/material.glsl>

in VS_OUT {
  vec2 f_texture;
  vec3 color;
} i;

uniform sampler2D u_texture;

void main() {
    vec3 col = texture2D(u_texture, i.f_texture).rgb;

    gl_FragColor = vec4(col, 1.0f);
}