package engine3.gfx.shader;

import engine3.asset.AssetBindable;
import engine3.asset.api.ISyncedInitialization;
import engine3.core.IFlags;
import engine3.gfx.OpenGL;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.MemoryStack;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL45C.*;

public class ShaderProgram extends AssetBindable implements ISyncedInitialization, IFlags<ShaderProgram.Flags, ShaderProgram> {

  public enum Flags {
    TIMED
  }

  /**
   * This {@link HashMap} caches all previously queried uniform locations to reduce the number of API calls required
   * each frame.
   */
  protected final HashMap<String, Integer> uniforms;

  private final List<Shader> shaders;

  private final EnumSet<ShaderProgram.Flags> flags;

  private final ShaderIO[] io;

  /**
   * Defines whether a shader in this program includes /shared/time.glsl.
   */
  private boolean timed;

  private int getCachedUniformLocation(String key) {
    if (!this.uniforms.containsKey(key)) {
      int location = glGetUniformLocation(this.id, key);
      if (location == -1) {
        return -1;
      }
      this.uniforms.put(key, location);
    }

    return this.uniforms.get(key);
  }

  public List<Shader> shaders() {
    return this.shaders;
  }

  public boolean hasUniform(String... uniform) {
    for (String u : uniform) {
      if (!this.uniforms.containsKey(u)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isFlagged(Flags flag) {
    return this.flags.contains(flag);
  }

  @Override
  public ShaderProgram setFlag(Flags flag) {
    this.flags.add(flag);
    return this;
  }

  @Override
  public ShaderProgram unsetFlag(Flags flag) {
    this.flags.remove(flag);
    return this;
  }

  /**
   * Binds a boolean uniform to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, boolean data) {
    OpenGL.context().shader(this);
    glUniform1i(this.getCachedUniformLocation(uniform), data ? GL_TRUE : GL_FALSE);
  }

  /**
   * Binds an int uniform to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, int data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform1i(this.getCachedUniformLocation(uniform), data);
    }
  }

  /**
   * Binds a two-component int {@link Vector2i} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Two-component int {@link Vector2i} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector2ic data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform2iv(this.getCachedUniformLocation(uniform), data.get(stack.mallocInt(2)));
    }
  }

  /**
   * Binds a three-component int {@link Vector3i} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Three-component int {@link Vector3i} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector3ic data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform3iv(this.getCachedUniformLocation(uniform), data.get(stack.mallocInt(3)));
    }
  }

  /**
   * Binds a four-component int {@link Vector4i} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Four-component int {@link Vector4i} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector4ic data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform4iv(this.getCachedUniformLocation(uniform), data.get(stack.mallocInt(4)));
    }
  }

  /**
   * Binds a float uniform to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, float data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform1f(this.getCachedUniformLocation(uniform), data);
    }
  }

  /**
   * Binds a two-component float {@link Vector2f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Two-component float {@link Vector2f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector2fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform2fv(this.getCachedUniformLocation(uniform), data.get(stack.mallocFloat(2)));
    }
  }

  /**
   * Binds a three-component float {@link Vector3f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Three-component float {@link Vector3f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector3fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform3fv(this.getCachedUniformLocation(uniform), data.get(stack.mallocFloat(3)));
    }
  }

  /**
   * Binds a four-component float {@link Vector4f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    Four-component float {@link Vector4f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Vector4fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniform4fv(this.getCachedUniformLocation(uniform), data.get(stack.mallocFloat(4)));
    }
  }

  /**
   * Binds a 2x2 float {@link Matrix2f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    2x2 {@link Matrix2f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Matrix2fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniformMatrix2fv(this.getCachedUniformLocation(uniform), false, data.get(stack.mallocFloat(4)));
    }
  }

  /**
   * Binds a 3x3 float {@link Matrix3f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    3x3 {@link Matrix3f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Matrix3fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniformMatrix3fv(this.getCachedUniformLocation(uniform), false, data.get(stack.mallocFloat(9)));
    }
  }

  /**
   * Binds a 4x4 float {@link Matrix4f} to this {@link ShaderProgram}.
   *
   * @param uniform {@link String} uniform location.
   * @param data    4x4 {@link Matrix4f} uniform data.
   */
  @SuppressWarnings("unused")
  public final void setUniform(@NotNull String uniform, @NotNull Matrix4fc data) {
    OpenGL.context().shader(this);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniformMatrix4fv(this.getCachedUniformLocation(uniform), false, data.get(stack.mallocFloat(16)));
    }
  }

  @Override
  public void destroy() {
    System.out.println("Destroyed ShaderProgram with ID '" + this.id + "'");
    GL45C.glDeleteProgram(this.id);
  }

  @Override
  public boolean initialize() {
    this.id = glCreateProgram();

    // initialize and attach shader programs
    for (Shader shader : this.shaders) {
      if (shader.initialize()) {
        glAttachShader(this.id, shader.id());
      } else {
        System.err.println("failed to attach shader");
        return false;
      }
    }

    // pre-link io binding
    for (ShaderIO io : this.io) {
      if (io instanceof FragmentShaderOutput out) {
        GL45C.glBindFragDataLocation(this.id, out.buffer, out.binding);
      }

      // todo: VertexShaderInput
    }

    // link program
    glLinkProgram(this.id);
    if (glGetProgrami(this.id, GL_LINK_STATUS) == GL_FALSE) {
      System.out.println("Failed to link shader program:\n\t" + glGetProgramInfoLog(this.id));
      return false;
    }

    // detach and dispose shaders
    for (Shader shader : this.shaders) {
      glDetachShader(this.id, shader.id());
      shader.destroy();
    }

    return true;
  }

  public interface ShaderIO {}
  public record FragmentShaderOutput(int buffer, String binding) implements ShaderIO {}
  public record VertexShaderInput(int buffer, String binding) implements ShaderIO {}

  /**
   * Called by loader asynchronously.
   *
   * @param shaders Shader programs.
   */
  public ShaderProgram(List<Shader> shaders, ShaderIO... io) {
    this.uniforms = new HashMap<>();
    this.timed = false;

    this.shaders = shaders;
    this.io = io;
    this.flags = EnumSet.noneOf(ShaderProgram.Flags.class);
  }
}
