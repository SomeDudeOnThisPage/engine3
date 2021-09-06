package engine3.gfx.enums;

import static org.lwjgl.opengl.GL45C.GL_FLOAT;
import static org.lwjgl.opengl.GL45C.GL_INT;

@SuppressWarnings("unused")
public enum VertexDataType {
  INT1(4, 1, GL_INT),
  INT2(8, 2, GL_INT),
  INT3(12, 3, GL_INT),
  INT4(16, 4, GL_INT),

  FLOAT1(4, 1, GL_FLOAT),
  FLOAT2(8, 2, GL_FLOAT),
  FLOAT3(12, 3, GL_FLOAT),
  FLOAT4(16, 4, GL_FLOAT),

  MATRIX2F(16, 4, GL_FLOAT),
  MATRIX3F(36, 9, GL_FLOAT),
  MATRIX4F(64, 16, GL_FLOAT),

  MATRIX2I(16, 4, GL_INT),
  MATRIX3I(36, 9, GL_INT),
  MATRIX4I(64, 16, GL_INT),

  BOOLEAN(1, 1, GL_INT);

  /**
   * Size in bytes.
   */
  private final int size;

  /**
   * Amount of components - e.g. a 3-Component Vector.
   */
  private final int components;

  /**
   * Internal OpenGL data type.
   */
  private final int gl;

  public int getSize() {
    return this.size;
  }

  public int getComponentAmount() {
    return this.components;
  }

  public int getOpenGLType() {
    return this.gl;
  }

  VertexDataType(int size, int components, int gl) {
    this.size = size;
    this.components = components;
    this.gl = gl;
  }
}
