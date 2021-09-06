package engine3.render.renderer2;

import engine3.Engine3;
import engine3.api.IApplication;
import engine3.asset.AssetReference;
import engine3.asset.api.IAssetReference;
import engine3.entity.SceneLockedEntityCollection;
import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityComponent;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.OpenGL;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;
import engine3.gfx.primitives.Model;
import engine3.gfx.shader.ShaderProgram;
import engine3.gfx.uniform.BufferedUniform;
import engine3.gfx.uniform.UniformBuffer;
import engine3.render.IRenderer2;
import engine3.render.deferred.GBuffer;
import engine3.render.entity.ICamera;
import engine3.scene.SceneEntity;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL45C;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ForwardSceneRenderer implements IRenderer2 {
  private final SceneLockedEntityCollection collection = new SceneLockedEntityCollection() {
    @Override
    public <T extends IEntityComponent> Class<T>[] components() {
      return new Class[] {
          TransformComponent.class,
          GeometryComponent.class
      };
    }
  };

  private final UniformBuffer ubo;
  private final GBuffer gbuffer;

  @Override
  public void initialize(IApplication application) {
    // application.getEntityComponentSystem().add(this.collection);
  }

  private void addSubScenes(SceneEntity scene, List<SceneEntity> list) {
    for (IEntity entity : scene.getChildren()) {
      if (entity instanceof SceneEntity sceneEntity) {
        list.add(sceneEntity);
        this.addSubScenes(sceneEntity, list);
      }
    }
  }

  @Override
  public void render(SceneEntity root, ICamera camera, Vector2i viewport) {
    // get sub scenes
    final List<SceneEntity> scenes = new ArrayList<>();
    scenes.add(root);
    this.addSubScenes(root, scenes);

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

    // todo: construct draw queue instead of looping scenes and drawing instantly
    for (SceneEntity scene : scenes) {

      final Collection<IEntity> entities = this.collection.getEntities(scene.getIdentifier());
      if (entities == null) continue;

      for (IEntity entity : entities) {
        // render all attached meshes
        final Model model = entity.getComponent(GeometryComponent.class).model.get();

        for (IAssetReference<Mesh> mesh : model.meshes) {
          if (!mesh.isLoaded()) continue;

          final IAssetReference<Material> material = mesh.get().material;
          final IAssetReference<VertexArray> vao = mesh.get().vao;

          if (!material.isLoaded() || !vao.isLoaded()) continue;

          final ShaderProgram program = material.get().getProgram();
          if (program == null) continue;

          program.setUniform("u_model", entity.getComponent(TransformComponent.class).getTransformMatrix());

          OpenGL.context().material(material.get()).varray(vao.get());
          GL45C.glDrawElements(GL45C.GL_TRIANGLES, vao.get().count(), GL45C.GL_UNSIGNED_INT, GL45C.GL_NONE);
        }
      }
    }

    OpenGL.context().framebuffer(null);
  }

  public GBuffer getGBuffer() {
    return this.gbuffer;
  }

  public ForwardSceneRenderer() {
    this.gbuffer = new GBuffer();
    this.gbuffer.initialize();

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
