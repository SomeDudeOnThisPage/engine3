package engine3.gfx.uniform;

import engine3.asset.AssetBindable;
import engine3.gfx.OpenGL;
import engine3.gfx.buffer.Buffer;
import engine3.util.Memory;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL42C.*;

public class UniformBuffer extends AssetBindable implements Buffer {
  /** String identifiers mapped to BufferedUniform instances. */
  private final HashMap<String, BufferedUniform<?>> uniforms;

  /** String identifiers mapped to the absolute locations in this buffer. */
  private final HashMap<String, Long> locations;

  private final int binding;
  private int size;

  @Override
  public int size()
  {
    throw new UnsupportedOperationException("cannot query size of uniform buffer");
  }

  @Override
  public int getTarget() {
    return GL_UNIFORM_BUFFER;
  }

  public int getBinding() {
    return this.binding;
  }

  @SuppressWarnings("unchecked")
  public <T> BufferedUniform<T> getUniform(String uniform) {
    if (!this.uniforms.containsKey(uniform)) {
      throw new UnsupportedOperationException("cannot retrieve non-existent uniform '" + uniform + "' from " +
          "UniformBuffer '" + this + "'");
    }

    return (BufferedUniform<T>) this.uniforms.get(uniform);
  }

  /**
   * Sets a uniform given by a string location, and updates the uniform buffer (writes to GPU memory).
   * @param uniform The uniform location.
   * @param data The data to be set.
   * @param <T> Data type.
   */
  public <T> void set(String uniform, T data) {
    final BufferedUniform<T> bu = this.getUniform(uniform);
    bu.set(data);
    this.update(uniform);
  }

  /**
   * Moves the data stored in a {@link BufferedUniform} into the actual {@link UniformBuffer Uniform-Buffer-Object}.
   * This can be used to flush updated data from the {@link BufferedUniform}s to the UBO to be used in shaders.
   * @param index The {@link String} name of the {@link BufferedUniform uniform} variable.
   */
  public void update(String index) {
    BufferedUniform<?> uniform = this.uniforms.getOrDefault(index, null);
    if (uniform == null) {
      throw new UnsupportedOperationException("cannot update non-existent uniform '" + index + "' in" +
          "UniformBuffer '" + this + "'");
    }

    OpenGL.context().buffer(this);
    Long location = this.locations.get(index);

    // this isn't scalable at all, there's gotta be a better way to do this...
    // maybe visitor pattern or make the uniforms set themselves?
    if (uniform.get() instanceof Float float1) {
      Memory.Buffer.buffer1f.put(0, float1);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1f);
    }
    else if (uniform.get() instanceof Integer int1) {
      Memory.Buffer.buffer1i.put(0, int1);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1i);
    }
    else if (uniform.get() instanceof Boolean bool1) {
      Memory.Buffer.buffer1i.put(0, bool1 ? 1 : 0);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1i);
    }
    else if (uniform.get() instanceof Matrix4f mat4) {
      mat4.get(Memory.Buffer.buffer16f);

      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer16f);
    }
    else if (uniform.get() instanceof Vector3f vec3) {
      vec3.get(Memory.Buffer.buffer4f);
      Memory.Buffer.buffer4f.put(3, 0.0f); // padding, this data is to be seen as invalid and should not be used
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer4f);
    }
    else if (uniform.get() instanceof Vector4f vec4) {
      vec4.get(Memory.Buffer.buffer4f);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer4f);
    }
    else {
      throw new UnsupportedOperationException("attempted to set uniform with invalid data type - how did you even" +
          "manage this?");
    }
  }

  @Override
  public void destroy() {
    System.out.println("Destroyed UniformBuffer with ID '" + this.id + "'");
    glDeleteBuffers(this.id);
  }

  /**
   * Recursively indexes buffered uniforms.
   * @param root The current root string.
   * @param uniform The current uniform.
   */
  public void index(String root, BufferedUniform<?> uniform) {
    if (uniform instanceof BufferedUniformStruct<?>) {
      BufferedUniformStruct<?> struct = (BufferedUniformStruct<?>) uniform;

      for (BufferedUniform<?> sUniform : struct.getUniforms().values()) {
        this.index(root + uniform.name() + "." /* append '.' to mirror glsl syntax */, sUniform);
        /*
          this.uniforms.put(root + uniform.name() + "." + sUniform.name(), sUniform);
          this.locations.put(root + uniform.name() + "." + sUniform.name(), (long) this.size);
          System.out.println(root + uniform.name() + "." + sUniform.name() + " " + this.size);
          this.size += sUniform.alignment;
        */
      }
    }
    else if (uniform instanceof BufferedUniformArray<?>) {
      int i = 0;
      for (BufferedUniform<?> aUniform : ((BufferedUniformArray<?>) uniform).getUniforms()) {
        this.index(root + uniform.name() + "[" + (i++) + "]", aUniform);
      }
    }
    else {
      this.uniforms.put(root + uniform.name(), uniform);
      this.locations.put(root + uniform.name(), (long) this.size);
      // System.out.println(root + uniform.name() + " " + this.size + " " + uniform.get());
      this.size += uniform.alignment();
    }
  }

  public UniformBuffer(int binding, BufferedUniform<?>... uniforms) {
    this.id = glGenBuffers();
    this.binding = binding;

    this.uniforms = new HashMap<>();
    this.locations = new HashMap<>();

    this.size = 0;

    for (BufferedUniform<?> uniform : uniforms) {
      this.index("" /* base root is empty */, uniform);
    }

    OpenGL.context().buffer(this);
    glBufferData(GL_UNIFORM_BUFFER, this.size, GL_STREAM_DRAW);
    glBindBufferBase(GL_UNIFORM_BUFFER, binding, this.id);
    OpenGL.context().buffer(this, true);

    // self-load to asset manager // todo
    // Engine3.ASSET_MANAGER.load(this, "ubo." + this, UniformBuffer.class);

    for (String uniform : this.uniforms.keySet()) {
      // System.out.println("Initializing uniform " + uniform);
      // System.out.println("Position: " + this.locations.get(uniform));
      // System.out.println("Alignment: " + this.uniforms.get(uniform).alignment);
      // System.out.println("Data: " + this.uniforms.get(uniform).get());
      this.update(uniform);
    }
  }
}
