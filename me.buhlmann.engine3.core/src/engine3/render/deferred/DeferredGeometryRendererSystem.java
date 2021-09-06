package engine3.render.deferred;

import engine3.asset.api.IAssetReference;
import engine3.entity.EntityCollection;
import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityComponent;
import engine3.entity.api.IRenderSystem;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.OpenGL;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;
import engine3.gfx.primitives.Model;
import engine3.gfx.shader.ShaderProgram;
import engine3.render.IRenderer;
import org.lwjgl.opengl.GL45C;

public class DeferredGeometryRendererSystem extends EntityCollection implements IRenderSystem<DeferredRenderer> {
  @Override
  public IRenderer.Stage stage() {
    return IRenderer.Stage.GEOMETRY;
  }

  @Override
  public void render(DeferredRenderer renderer) {
    // gbuffer is bound for writing
    // render geometry information (position, material stuff) to gbuffer
    for (IEntity entity : this.entities) {
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

  @Override
  public <T extends IEntityComponent> Class<T>[] components() {
    return new Class[] {
        TransformComponent.class,
        GeometryComponent.class
    };
  }
}
