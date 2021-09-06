package engine3.gfx.framebuffer;

import engine3.gfx.texture.TextureFormat;
import engine3.gfx.texture.TextureWrap;
import engine3.gfx.texture.filter.TextureFilter;
import org.lwjgl.opengl.GL45C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL45C.*;

public class FrameBufferSpecification {
  public static final class DepthTextureSpecification extends TextureSpecification {
    public DepthTextureSpecification(TextureFormat format, TextureWrap wrap, TextureFilter filter) {
      super(format, wrap, filter);
    }
  }

  public static class TextureSpecification {
    public TextureFormat format;
    public TextureWrap wrap;
    public TextureFilter filter;

    public TextureSpecification(TextureFormat format, TextureWrap wrap, TextureFilter filter) {
      this.format = format;
      this.wrap = wrap;
      this.filter = filter;
    }
  }

  private final List<TextureSpecification> buffers;

  public List<TextureSpecification> getBufferSpecifications() {
    return this.buffers;
  }

  public FrameBufferSpecification(TextureSpecification... buffers) {
    this.buffers = new ArrayList<>();
    this.buffers.addAll(Arrays.asList(buffers));
  }
}
