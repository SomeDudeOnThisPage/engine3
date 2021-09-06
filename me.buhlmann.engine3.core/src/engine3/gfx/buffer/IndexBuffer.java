package engine3.gfx.buffer;

import engine3.gfx.OpenGL;
import engine3.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL42C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL42C.GL_UNSIGNED_INT;

public class IndexBuffer extends DataBuffer<Integer> {
  public IndexBuffer(int usage, @NotNull Integer[] data) {
    this(usage);
    this.size = data.length;

    IntBuffer ib = BufferUtils.createIntBuffer(data.length);
    ib.put(ArrayUtils.toPrimitiveI(data));
    ib.flip();

    OpenGL.context().buffer(this);
    glBufferData(this.target, ib, this.usage);
    OpenGL.context().buffer(this, true);
  }

  public IndexBuffer(int usage)
  {
    super(GL_ELEMENT_ARRAY_BUFFER, GL_UNSIGNED_INT, usage);
  }
}
