package engine3.render.deferred;

import engine3.Engine3;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.gfx.framebuffer.FrameBufferSpecification;
import engine3.gfx.texture.TextureFormat;
import engine3.gfx.texture.TextureWrap;
import engine3.gfx.texture.filter.TextureFilter;
import engine3.gfx.texture.filter.TextureFilterLinear;
import org.lwjgl.opengl.GL45C;

public class GBuffer extends FrameBuffer {
  public void drawBuffers() {
    GL45C.glDrawBuffers(new int[] {
        GL45C.GL_COLOR_ATTACHMENT0,
        GL45C.GL_COLOR_ATTACHMENT1,
        GL45C.GL_COLOR_ATTACHMENT2,
        GL45C.GL_COLOR_ATTACHMENT3,
    });
  }

  public GBuffer() {
    super(Engine3.DISPLAY.getSize(), null, FrameBuffer.Flags.RESIZABLE);

    TextureFormat positionBuffer = new TextureFormat(GL45C.GL_RGB32F, GL45C.GL_RGB, GL45C.GL_FLOAT);
    TextureFormat normalBuffer = new TextureFormat(GL45C.GL_RGB16F, GL45C.GL_RGB, GL45C.GL_FLOAT);
    TextureFormat albedoBuffer = new TextureFormat(GL45C.GL_RGB8, GL45C.GL_RGB, GL45C.GL_UNSIGNED_BYTE);
    TextureFormat materialBuffer = new TextureFormat(GL45C.GL_RGBA16F, GL45C.GL_RGBA, GL45C.GL_FLOAT);

    TextureFormat depthBuffer = new TextureFormat(GL45C.GL_DEPTH_COMPONENT, GL45C.GL_DEPTH_COMPONENT, GL45C.GL_FLOAT);

    TextureWrap wrap = new TextureWrap(GL45C.GL_CLAMP_TO_BORDER);
    TextureFilter filter = new TextureFilterLinear();

    this.specification = new FrameBufferSpecification(
      new FrameBufferSpecification.TextureSpecification(positionBuffer, wrap, filter),
      new FrameBufferSpecification.TextureSpecification(normalBuffer, wrap, filter),
      new FrameBufferSpecification.TextureSpecification(albedoBuffer, wrap, filter),
      new FrameBufferSpecification.TextureSpecification(materialBuffer, wrap, filter),
      new FrameBufferSpecification.DepthTextureSpecification(depthBuffer, wrap, filter)
    );
  }
}
