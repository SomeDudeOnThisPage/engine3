package engine3.render;

import engine3.Engine4;
import engine3.entity.AbstractEventBinding;
import engine3.entity.EntityComponentSystem;
import engine3.gfx.OpenGL;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.gfx.framebuffer.FrameBufferSpecification;
import engine3.gfx.texture.TextureFormat;
import engine3.gfx.texture.TextureWrap;
import engine3.gfx.texture.filter.TextureFilterLinear;
import engine3.gfx.uniform.BufferedUniform;
import engine3.gfx.uniform.UniformBuffer;
import engine3.render.entity.ICamera;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL45C;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_BACK;

public class Renderer extends AbstractEventBinding implements IRenderer {
  private final UniformBuffer ubo;
  private final List<IRenderPass> passes;
  private FrameBuffer fbo;

  //@EventListener(DisplayEvent.class)
  public void onDisplayResize(Vector4i viewport) {
    this.fbo.destroy();
    final FrameBufferSpecification specification = new FrameBufferSpecification(
        new FrameBufferSpecification.TextureSpecification(
            new TextureFormat(GL_RGBA8, GL_RGBA, GL_UNSIGNED_INT),
            new TextureWrap(GL45C.GL_CLAMP_TO_EDGE),
            new TextureFilterLinear()
        ),
        new FrameBufferSpecification.DepthTextureSpecification(
            new TextureFormat(GL45C.GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT),
            new TextureWrap(GL45C.GL_CLAMP_TO_EDGE),
            new TextureFilterLinear()
        )
    );
    this.fbo = new FrameBuffer(new Vector2i(viewport.z, viewport.w), specification);
    this.fbo.initialize();
    System.out.println(viewport.z + " " + viewport.w);
  }

  @Override
  public void render(final ICamera camera) {
    final Vector4i viewport = camera.getViewportDimensions();
    if (viewport.z != this.fbo.getViewportSize().x || viewport.w != this.fbo.getViewportSize().y) {
      this.onDisplayResize(viewport);
    }

    OpenGL.context().framebuffer(this.fbo);
    glEnable(GL_DEPTH_TEST);
    glCullFace(GL_BACK);

    // configure uniform buffer
    this.ubo.set("u_mat4_projection", camera.getProjection().getProjectionMatrix());
    this.ubo.set("u_mat4_view", camera.getTransform().getTransformMatrix());

    OpenGL.context().buffer(this.ubo);
    OpenGL.context().clear();

    for (IRenderPass pass : this.passes) {
      pass.render(camera);
    }

    OpenGL.context().framebuffer(null);
  }

  @Override
  public void initialize(EntityComponentSystem ecs) {
    for (IRenderPass pass : this.passes) {
      pass.initialize(ecs);
    }
  }

  @Override
  public void destroy(EntityComponentSystem ecs) {
    for (IRenderPass pass : this.passes) {
      pass.destroy(ecs);
    }
    this.fbo.destroy();
    this.ubo.destroy();
  }

  @Override
  public FrameBuffer getFramebuffer() {
    return this.fbo;
  }

  public Renderer() {
    this.passes = new LinkedList<>();
    this.passes.add(new ForwardRenderPass());

    this.ubo = new UniformBuffer(0,
        new BufferedUniform<>("u_mat4_projection", new Matrix4f()),
        new BufferedUniform<>("u_mat4_view", new Matrix4f())
    );

    final FrameBufferSpecification specification = new FrameBufferSpecification(
        new FrameBufferSpecification.TextureSpecification(
            new TextureFormat(GL_RGBA8, GL_RGBA, GL_UNSIGNED_INT),
            new TextureWrap(GL45C.GL_CLAMP_TO_EDGE),
            new TextureFilterLinear()
        ),
        new FrameBufferSpecification.DepthTextureSpecification(
            new TextureFormat(GL45C.GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT),
            new TextureWrap(GL45C.GL_CLAMP_TO_EDGE),
            new TextureFilterLinear()
        )
    );
    this.fbo = new FrameBuffer(Engine4.getDisplay().getSize(), specification, FrameBuffer.Flags.RESIZABLE);
    this.fbo.initialize();
  }
}
