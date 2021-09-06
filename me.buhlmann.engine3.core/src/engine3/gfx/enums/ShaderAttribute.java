package engine3.gfx.enums;

/**
 * The {@link ShaderAttribute} locations of this engine are set statically, in this file.
 * Doing it this way reduces the amount of needed glGetAttribLocation calls, and allows any {@link }ShaderProgram to be
 * used with any {@link }VertexArray. Note that any enum set here must mirror the values set in
 * {@code resources/shader/shared/core.glsl}, and vice versa.
 */
@SuppressWarnings("unused")
public enum ShaderAttribute {
  // these values should mirror the values set in VertexDataType.glsl
  V_POSITION("v_position", 0, VertexDataType.FLOAT3),
  V_NORMAL("v_normal", 1, VertexDataType.FLOAT3),
  V_TEXTURE("v_texture", 2, VertexDataType.FLOAT2),
  V_COLOR("v_color", 2, VertexDataType.FLOAT3),
  V_TANGENT("v_tangent", 3, VertexDataType.FLOAT3),
  V_BITANGENT("v_bitangent", 4, VertexDataType.FLOAT3);

  /**
   * The name of the attribute, as mirrored in shader sources.
   */
  private final String name;

  /**
   * The location of the attribute, as mirrored in shader sources.
   */
  private final int location;

  /**
   * The OpenGL data type of the data.
   */
  private final VertexDataType data;

  public String getName() {
    return this.name;
  }

  public int getLocation() {
    return this.location;
  }

  public VertexDataType getDataType() {
    return this.data;
  }

  ShaderAttribute(String name, int location, VertexDataType data) {
    this.name = name;
    this.location = location;
    this.data = data;
  }
}
