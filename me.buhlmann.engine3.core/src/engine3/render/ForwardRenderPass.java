package engine3.render;

import engine3.Engine4;
import engine3.asset.api.IAssetReference;
import engine3.entity.EntityCollection;
import engine3.entity.EntityComponentSystem;
import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityCollection;
import engine3.entity.api.IEntityComponent;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.OpenGL;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;
import engine3.gfx.primitives.Model;
import engine3.gfx.shader.ShaderProgram;
import engine3.render.entity.ICamera;
import engine3.util.SceneGraph;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.CallbackI;

import java.util.List;

public class ForwardRenderPass implements IRenderPass {
  private final IEntityCollection renderables;

  @Override
  public void render(final ICamera camera) {
    for (final IEntity entity : this.renderables.getEntities()) {
      final Model model = entity.getComponent(GeometryComponent.class).model.get();

      for (IAssetReference<Mesh> mesh : model.meshes) {
        // final List<IAssetReference<Material>> materials = mesh.get().material;
        final IAssetReference<Material> material = Engine4.getAssetManager().request(Material.class, "material.flat-color.red");
        final List<VertexArray> meshes = mesh.get().vao;

        int i = 0;
        for (VertexArray vao : meshes) {
          if (!material.isLoaded() || !mesh.isLoaded()) continue;

          final ShaderProgram program = material.get().getProgram();
          if (program == null) continue;

          program.setUniform("u_model", SceneGraph.transform(entity));

          OpenGL.context().material(material.get()).varray(vao);
          GL45C.glDrawElements(GL45C.GL_TRIANGLES, vao.count(), GL45C.GL_UNSIGNED_INT, GL45C.GL_NONE);
          i++;
        }
      }
    }
  }

  @Override
  public void initialize(final EntityComponentSystem ecs) {
    ecs.add(this.renderables);
  }

  @Override
  public void destroy(final EntityComponentSystem ecs) {
    ecs.remove(this.renderables);
  }

  public ForwardRenderPass() {
    this.renderables = new EntityCollection() {
      @Override
      public <T extends IEntityComponent> Class<T>[] components() {
        return new Class[] {
            GeometryComponent.class,
            TransformComponent.class
        };
      }
    };
  }
}
