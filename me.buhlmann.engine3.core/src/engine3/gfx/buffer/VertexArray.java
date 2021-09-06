package engine3.gfx.buffer;

import engine3.asset.AssetBindable;
import engine3.gfx.OpenGL;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL42C.*;

public class VertexArray extends AssetBindable {
  private final Set<Integer> attributes;
  private final List<VertexBuffer> buffers;
  private IndexBuffer indices;

  /**
   * Returns true if this {@link VertexArray} uses indexing.
   * <p>
   * An indexed {@link VertexArray} should be rendered by using
   * {@link org.lwjgl.opengl.GL42C#glDrawElements(int, int, int, long)}.
   * </p>
   *
   * @return True if this {@link VertexArray} is indexed.
   */
  public boolean indexed() {
    return this.indices != null;
  }

  public Set<Integer> getAttributes() {
    return this.attributes;
  }

  public IndexBuffer getIndexBuffer() {
    return this.indices;
  }

  public List<VertexBuffer> getVertexBuffers() {
    return this.buffers;
  }

  /**
   * In case that this {@link VertexArray} uses indexing, this method returns the amount of indices present in the
   * {@link IndexBuffer} buffer. In other cases, it returns the amount of elements inside the {@link VertexBuffer} at
   * location {@code 0} ({@code v_position}).
   *
   * @return Count of elements / elements inside the {@link VertexBuffer} at location {@code 0}.
   */
  public int count() {
    if (this.indexed()) {
      return this.indices.size();
    }
    return this.buffers.get(0).size();
  }

  /**
   * Adds a {@link VertexBuffer} to this {@link VertexArray}, binding it to the locations set in the {@link VertexBuffer}s'
   * {@link VertexBufferLayout}.
   *
   * @param buffer The {@link VertexBuffer} to be added to this {@link VertexArray}.
   */
  public void addVertexBuffer(@NotNull VertexBuffer buffer) {
    OpenGL.context().varray(this);
    OpenGL.context().buffer(buffer);

    VertexBufferLayout layout = buffer.layout();

    for (VertexBufferLayout.BufferElement element : layout.elements()) {
      OpenGL.context().attribute(element.location(), true);
      glVertexAttribPointer(
          element.location(),
          element.type().getComponentAmount(),
          element.type().getOpenGLType(),
          false, // todo: this should probably be stored in the BufferElement...
          layout.stride(),
          element.offset()
      );
      OpenGL.context().attribute(element.location(), false);

      /*if (this.attributes.contains(element.location())) {
        throw new GLException(this, "glVertexAttribPointer", "attribute at location '" + element.location() + "' is already defined");
      }*/

      this.attributes.add(element.location());
    }

    this.buffers.add(buffer);

    OpenGL.context().varray(null);
    OpenGL.context().buffer(buffer, true);
  }

  public void setIndexBuffer(@NotNull IndexBuffer buffer) {
    OpenGL.context().varray(this);
    OpenGL.context().buffer(buffer);

    // and that's it, binding an element buffer to a vertex array is weird...
    this.indices = buffer;

    OpenGL.context().buffer(buffer, true);
    OpenGL.context().varray(null);
  }

  @Override
  public void destroy() {
    for (VertexBuffer buffer : this.buffers) {
      buffer.destroy();
    }

    for (Integer location : this.attributes) {
      OpenGL.context().attribute(location, false);
    }

    if (this.indexed()) {
      this.indices.destroy();
    }

    OpenGL.context().varray(null);
    System.out.println("Destroyed VertexArray with ID '" + this.id + "'");
    glDeleteVertexArrays(this.id);
  }

  public VertexArray() {
    this.id = glGenVertexArrays();
    this.buffers = new ArrayList<>();
    this.attributes = new HashSet<>();
  }
}
