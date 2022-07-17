package engine3.render.deferred;

import engine3.Engine3;
import engine3.asset.AssetReference;
import engine3.asset.api.IAssetReference;
import engine3.asset.api.IDestructible;
import engine3.entity.EntityComponentSystem;
import engine3.gfx.OpenGL;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.gfx.uniform.BufferedUniform;
import engine3.gfx.uniform.UniformBuffer;
import engine3.render.IRenderer;
import engine3.render.entity.ICamera;
import engine3.scene.SceneTree;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL45C;

import static org.lwjgl.opengl.GL11C.*;

public final class DeferredRenderer implements IRenderer, IDestructible {
  private final UniformBuffer ubo;
  private final GBuffer gbuffer;

  public long qstart = 0;
  public long qstop = 0;

  private final Vector2i viewport;

  @Override
  public void destroy() {
    this.ubo.destroy();
  }

  @Override
  public void registerRenderSystems(EntityComponentSystem ecs) {
    ecs.add(new DeferredGeometryRendererSystem());
  }

  public void setViewport(Vector2i viewport) {
    this.viewport.set(viewport);
  }

  @Override
  public void render(SceneTree scene, Vector2i viewport, ICamera camera) {
    glEnable(GL_DEPTH_TEST);
    glCullFace(GL_BACK);

    int qstart = GL45C.glGenQueries();
    int qstop = GL45C.glGenQueries();
    GL45C.glQueryCounter(qstart, GL45C.GL_TIMESTAMP);

    // configure camera for rendering to screen
    camera.getProjection().setAspectRatio(viewport.x / (float) viewport.y);
    camera.setViewportDimensions(new Vector4i(0, 0, viewport.x, viewport.y));

    // resize gbuffer if needed
    if (this.gbuffer.getViewportSize().x != viewport.x || this.gbuffer.getViewportSize().y != viewport.y) {
      this.gbuffer.resizeViewport(viewport);
    }

    // configure uniform buffer
    this.ubo.set("u_mat4_projection", camera.getProjection().getProjectionMatrix());
    this.ubo.set("u_mat4_view", camera.getTransform().getTransformMatrix());

    OpenGL.context().buffer(this.ubo);

    OpenGL.context().framebuffer(this.gbuffer);
    this.gbuffer.drawBuffers();
    OpenGL.context().clear();

    scene.render(1, IRenderer.Stage.GEOMETRY, this);

    OpenGL.context().framebuffer(null);

    GL45C.glQueryCounter(qstop, GL45C.GL_TIMESTAMP);

    /*while (GL45C.glGetQueryObjecti(qstart, GL45C.GL_QUERY_RESULT_AVAILABLE) != 1) {
      continue;
    }

    this.qstart = GL45C.glGetQueryObjectui64(qstart, GL45C.GL_QUERY_RESULT);
    this.qstop = GL45C.glGetQueryObjectui64(qstop, GL45C.GL_QUERY_RESULT);*/

    /*GL45C.glBlitNamedFramebuffer(this.gbuffer.id(), GL45C.GL_NONE,
        0, 0,
        this.gbuffer.getViewportSize().x, this.gbuffer.getViewportSize().y,
        0, 0,
        Engine3.DISPLAY.getSize().x,
        Engine3.DISPLAY.getSize().y,
        GL45C.GL_COLOR_BUFFER_BIT,
        GL45C.GL_LINEAR
    );*/

    // bind gbuffer for writing
    // call ecs to render geometry stage

    // bind gbuffer for reading
    // bind light buffer for writing

    // call ecs to render light stage

    // bind gbuffer for reading (already set)
    // bind light buffer for reading

    // render filters to alternating filter framebuffer

    // render last filter operation to main framebuffer
    // if there are no filters, just blit it idk
  }

  public GBuffer getGBuffer() {
    return this.gbuffer;
  }

  public DeferredRenderer() {
    this.gbuffer = new GBuffer();
    this.gbuffer.initialize();
    this.viewport = new Vector2i(800, 600);

    this.ubo = new UniformBuffer(0,
        new BufferedUniform<>("u_mat4_projection", new Matrix4f()),
        new BufferedUniform<>("u_mat4_view", new Matrix4f())
    );

    IAssetReference<UniformBuffer> uboReference = AssetReference.wrap("ubo.deferred-pipeline-core", this.ubo, UniformBuffer.class);
    IAssetReference<FrameBuffer> fboReference = AssetReference.wrap("fbo.deferred-pipeline-gbuffer", this.gbuffer, FrameBuffer.class);

    Engine3.ASSET_MANAGER.load(uboReference);
    Engine3.ASSET_MANAGER.load(fboReference);
  }
}
