package engine3.gfx.framebuffer;

import engine3.asset.AssetBindable;
import engine3.asset.api.ISyncedInitialization;
import engine3.core.IFlags;
import engine3.gfx.OpenGL;
import engine3.gfx.buffer.Buffer;
import engine3.gfx.texture.Texture2D;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL45C;

import java.util.*;

import static org.lwjgl.opengl.GL45C.*;

public class FrameBuffer extends AssetBindable implements Buffer, ISyncedInitialization, IFlags<FrameBuffer.Flags, FrameBuffer> {
  public enum Flags {
    RESIZABLE
  }

  private final Map<Integer, Texture2D> textures;
  private Texture2D depthAttachment;
  protected FrameBufferSpecification specification;

  private final Vector2i size;

  private final EnumSet<FrameBuffer.Flags> flags;

  public Collection<Texture2D> getColorTextures() {
    return this.textures.values();
  }

  public Texture2D getColorTexture(int texture) {
    return this.textures.get(texture);
  }

  public Texture2D getDepthTexture() {
    return this.depthAttachment;
  }

  private void addDepthTexture(FrameBufferSpecification.TextureSpecification specification) {
    final int attachment = 0;
    OpenGL.context().framebuffer(this);

    final Vector2i size = this.getViewportSize();
    final Texture2D texture = new Texture2D(size, specification.format, specification.filter, specification.wrap);
    texture.initialize();

    OpenGL.context().framebuffer(this).texture2d(texture, attachment);

    GL45C.glFramebufferTexture2D(
        this.getTarget(),
        GL_DEPTH_ATTACHMENT,
        texture.getTarget(),
        texture.id(),
        0
    );


    if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
      System.out.println("SUCCESS ATTACHING DEPTH TEXTURE");
    } else {
      System.err.println("FAILURE ATTACHING DEPTH TEXTURE " + glGetError());
    }

    this.depthAttachment = texture;

    OpenGL.context().framebuffer(null).texture2d(null, 0);
  }

  private void addColorTexture(FrameBufferSpecification.TextureSpecification specification, int attachment) {
    OpenGL.context().framebuffer(this);

    final Vector2i size = this.getViewportSize();
    final Texture2D texture = new Texture2D(size, specification.format, specification.filter, specification.wrap);
    texture.initialize();

    OpenGL.context().framebuffer(this).texture2d(texture, attachment);

    GL45C.glFramebufferTexture2D(
        this.getTarget(),
        GL_COLOR_ATTACHMENT0 + attachment,
        texture.getTarget(),
        texture.id(),
        0
    );

    if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
      System.out.println("SUCCESS ATTACHING TEXTURE");
    } else {
      System.err.println("FAILURE ATTACHING TEXTURE " + glGetError());
    }

    this.textures.put(attachment, texture);
    OpenGL.context().framebuffer(null).texture2d(null, attachment);
  }

  @Override
  public boolean isFlagged(Flags flag) {
    return this.flags.contains(flag);
  }

  @Override
  public FrameBuffer setFlag(Flags flag) {
    this.flags.add(flag);
    return this;
  }

  @Override
  public FrameBuffer unsetFlag(Flags flag) {
    this.flags.remove(flag);
    return this;
  }

  public Vector2i getViewportSize() {
    return this.size;
  }

  public void resizeViewport(Vector2ic size) {
    if (this.flags.contains(FrameBuffer.Flags.RESIZABLE)) {
      this.size.set(size);
      this.destroyAttachments();
      int i = 0;
      for (FrameBufferSpecification.TextureSpecification format : this.specification.getBufferSpecifications()) {
        if (format instanceof FrameBufferSpecification.DepthTextureSpecification) {
          System.out.println("IS DEPTH TEXTURE");
          this.addDepthTexture(format);
        } else {
          this.addColorTexture(format, i++);
        }
      }
    }
  }

  private void destroyAttachments() {
    for (Texture2D texture : this.textures.values()) {
      texture.destroy();
    }

    this.textures.clear();
  }

  @Override
  public void destroy() {
    glDeleteFramebuffers(this.id);
    this.destroyAttachments();
  }

  @Override
  public boolean initialize() {
    if (this.id == 0) {
      this.destroy();
    }

    this.id = glCreateFramebuffers();

    int i = 0;
    for (FrameBufferSpecification.TextureSpecification format : this.specification.getBufferSpecifications()) {
      if (format instanceof FrameBufferSpecification.DepthTextureSpecification) {
        System.out.println("IS DEPTH TEXTURE");
        this.addDepthTexture(format);
      } else {
        this.addColorTexture(format, i++);
      }
    }

    return true;
  }

  @Override
  public int size() {
    return this.specification.getBufferSpecifications().size();
  }

  @Override
  public int getTarget() {
    return GL45C.GL_FRAMEBUFFER;
  }

  public FrameBuffer(Vector2i size, FrameBufferSpecification specification, FrameBuffer.Flags... flags) {
    this.size = new Vector2i(size.x, size.y);
    this.textures = new HashMap<>();
    this.specification = specification;

    this.flags = EnumSet.noneOf(FrameBuffer.Flags.class);
    Collections.addAll(this.flags, flags);
  }
}
