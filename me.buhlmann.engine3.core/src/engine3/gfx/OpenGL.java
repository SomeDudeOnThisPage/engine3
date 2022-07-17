package engine3.gfx;

import engine3.Engine4;
import engine3.gfx.buffer.Buffer;
import engine3.gfx.buffer.VertexArray;
import engine3.events.GLDebugEvent;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.gfx.material.Material;
import engine3.gfx.shader.ShaderProgram;
import engine3.gfx.texture.Texture2D;
import engine3.gfx.uniform.UniformBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.opengl.*;

import java.util.*;

import static org.lwjgl.opengl.GL45C.*;

public class OpenGL {
  private static Context context;
  public static Context context() {
    return OpenGL.context;
  }

  public static void initialize() {
    GL.createCapabilities();
    OpenGL.context = new Context();
    glEnable(GL_MULTISAMPLE);
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_DEPTH_TEST);
  }

  public static final class Context {
    private int vao;
    private FrameBuffer framebuffer;
    private ShaderProgram program;

    private final Map<Integer, Integer> buffers;
    private final Set<Integer> attributes;

    private final Vector2i viewport;

    public Context clear() {
      Engine4.getEventBus().publish(new GLDebugEvent("glClear", new String[] {
          Integer.toString(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      }));
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      return this;
    }

    public Context texture2d(Texture2D texture, int slot) {
      // todo: check currently bound texture in <slot> to make sure we don't have any unneccessary api calls
      glActiveTexture(GL45C.GL_TEXTURE0 + slot);
      glBindTexture(GL45C.GL_TEXTURE_2D, (texture == null) ? GL45C.GL_NONE : texture.id());

      return this;
    }

    public Context material(Material material) {
      if (material != null) {
        this.shader(material.getProgram());
        material.setUniforms();
      } else {
        this.shader(null);
      }
      return this;
    }

    public Context viewport(Vector2i size) {
      if (size.x != this.viewport.x || size.y != this.viewport.y) {
        this.viewport.set(size);
        glViewport(0, 0, this.viewport.x, this.viewport.y);
      }
      return this;
    }

    public Context attribute(int index, boolean state) {
      if (!this.attributes.contains(index) && state) {
        Engine4.getEventBus().publish(new GLDebugEvent("glEnableVertexAttribArray", new String[] { Integer.toString(index) }));
        glEnableVertexAttribArray(index);
        this.attributes.add(index);
      }

      if (this.attributes.contains(index) && !state) {
        glDisableVertexAttribArray(index);
        this.attributes.remove(index);
      }

      return this;
    }

    public Context varray(@Nullable VertexArray vao) {
      if (vao == null) {
        this.vao = GL_NONE;
        glBindVertexArray(GL_NONE);
      } else if (this.vao != vao.id()) {
        this.vao = vao.id();
        glBindVertexArray(vao.id());

        for (Integer location : vao.getAttributes()) {
          this.attribute(location, true);
        }

        if (vao.indexed()) {
          this.buffer(vao.getIndexBuffer(), true);
          this.buffer(vao.getIndexBuffer());
        }
      }

      return this;
    }

    public Context buffer(@NotNull Buffer buffer, boolean unbind) {
      if (!unbind && buffer instanceof UniformBuffer) {
        // uniform buffers use glBindBufferBase!
        if (this.buffers.get(buffer.getTarget()) != buffer.id()) {
          glBindBufferBase(buffer.getTarget(), ((UniformBuffer) buffer).getBinding(), buffer.id());
        }
      } else {
        if (unbind) {
          if (this.buffers.get(buffer.getTarget()) == buffer.id()) {
            glBindBuffer(buffer.getTarget(), GL_NONE);
            this.buffers.put(buffer.getTarget(), GL_NONE);
          }
        } else {
          if (this.buffers.get(buffer.getTarget()) != buffer.id()) {
            glBindBuffer(buffer.getTarget(), buffer.id());
            this.buffers.put(buffer.getTarget(), buffer.id());
          }
        }
      }

      return this;
    }

    public Context buffer(@NotNull Buffer buffer) {
      return this.buffer(buffer, false);
    }

    public Context shader(ShaderProgram program) {
      if (program == null && this.program != null) {
        glUseProgram(GL_NONE);
        this.program = null;
      } else if (this.program != program) {
        System.out.println(program.shaders().get(1).getSource());
        glUseProgram(program.id());
        this.program = program;
      }

      return this;
    }

    public Context framebuffer(FrameBuffer buffer) {
      if (buffer == null) {
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
        GL45C.glViewport(0, 0, Engine4.getDisplay().getSize().x, Engine4.getDisplay().getSize().y);
        this.framebuffer = null;
      } else {
        if (this.framebuffer == null || this.framebuffer.id() != buffer.id()) {
          glBindFramebuffer(buffer.getTarget(), buffer.id());
          this.framebuffer = buffer;
          int i = 0;
          /*for (Texture2D texture : buffer.getColorTextures()) {
            this.texture2d(texture, i++);
          }*/
        }
        GL45C.glViewport(0, 0, buffer.getViewportSize().x, buffer.getViewportSize().y);
      }

      return this;
    }

    public Context() {
      this.program = null;
      this.framebuffer = null;

      this.buffers = new HashMap<>();
      this.attributes = new HashSet<>();

      this.viewport = new Vector2i(0, 0);

      this.buffers.put(GL_ARRAY_BUFFER, GL_NONE);
      this.buffers.put(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
      this.buffers.put(GL_DRAW_INDIRECT_BUFFER, GL_NONE);
      this.buffers.put(GL_UNIFORM_BUFFER, GL_NONE);
    }
  }
}
