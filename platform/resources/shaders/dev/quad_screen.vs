out VS_OUT {
    vec3 color;
} o;

void main() {
    float x = float(((uint(gl_VertexID) + 2u) / 3u) % 2u);
    float y = float(((uint(gl_VertexID) + 1u) / 3u) % 2u);

    o.color = vec3(1.0f, 0.0f, 0.0f);

    gl_Position = vec4(-1.0f + x*2.0f, -1.0f+y*2.0f, 0.0f, 1.0f);
}