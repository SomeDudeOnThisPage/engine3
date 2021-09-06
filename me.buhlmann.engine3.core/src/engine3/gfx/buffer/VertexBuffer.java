package engine3.gfx.buffer;

import engine3.gfx.OpenGL;
import engine3.util.ArrayUtils;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15C.glBufferData;

public class VertexBuffer extends DataBuffer<Float> {
  private VertexBufferLayout layout;

  public void layout(VertexBufferLayout layout) {
    this.layout = layout;
  }

  public VertexBufferLayout layout() {
    return this.layout;
  }

  public VertexBuffer(int target, int type, int usage, Float[] buffer) {
    super(target, type, usage);
    this.size = buffer.length;

    FloatBuffer fb = BufferUtils.createFloatBuffer(buffer.length);
    fb.put(ArrayUtils.toPrimitiveF(buffer));
    fb.flip();

    OpenGL.context().buffer(this);
    glBufferData(this.target, fb, this.usage);
    OpenGL.context().buffer(this, true);
  }
}
