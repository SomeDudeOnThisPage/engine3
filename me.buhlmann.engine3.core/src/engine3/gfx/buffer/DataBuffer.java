package engine3.gfx.buffer;

import engine3.asset.AssetBindable;
import engine3.gfx.OpenGL;
import engine3.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL40C.GL_DRAW_INDIRECT_BUFFER;

public abstract class DataBuffer<T extends Number> extends AssetBindable implements Buffer {
  protected final int target;
  protected final int usage;
  protected int size;

  protected ByteBuffer data;

  @Override
  public void destroy() {
    OpenGL.context().buffer(this, true);
    System.out.println("Destroyed DataBuffer with ID '" + this.id + "'");
    glDeleteBuffers(this.id);
  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public int getTarget() {
    return this.target;
  }

  public void data(@NotNull T[] data) {
    final ByteBuffer buffer = BufferUtils.createByteBuffer(data.length * 4);
    if (data instanceof Integer[]) {
      buffer.asIntBuffer().put(ArrayUtils.toPrimitiveI((Integer[]) data));
      //buffer.asIntBuffer().flip();
      this.size = buffer.asIntBuffer().capacity();
    } else if (data instanceof Float[]) {
      FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
      fb.put(ArrayUtils.toPrimitiveF((Float[]) data));
      fb.flip();
      this.size = fb.capacity();
    } else {
      throw new UnsupportedOperationException("cannot create data buffer with data type '" + data.getClass() + "'");
    }

    this.data = buffer;

    OpenGL.context().buffer(this);
    glBufferData(this.target, this.data, this.usage);
    OpenGL.context().buffer(this, true);
  }

  public DataBuffer(int target, int type, int usage) {
    this.id = glGenBuffers();

    if (target != GL_ARRAY_BUFFER && target != GL_ELEMENT_ARRAY_BUFFER && target != GL_DRAW_INDIRECT_BUFFER && target != GL_UNIFORM_BUFFER) {
      throw new IllegalArgumentException("target must be either GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER or GL_DRAW_INDIRECT_BUFFER");
    }
    this.target = target;

    if (type != GL_FLOAT && type != GL_UNSIGNED_BYTE && type != GL_UNSIGNED_INT) {
      throw new IllegalArgumentException("type must be either GL_FLOAT, GL_UNSIGNED_BYTE or GL_UNSIGNED_INT");
    }

    if (usage != GL_STATIC_DRAW && usage != GL_DYNAMIC_DRAW && usage != GL_STREAM_DRAW) {
      throw new IllegalArgumentException("usage must be either GL_STATIC_DRAW, GL_DYNAMIC_DRAW or GL_STREAM_DRAW");
    }
    this.usage = usage;
  }
}
