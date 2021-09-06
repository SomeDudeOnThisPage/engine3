package engine3.render;

import engine3.entity.EntityComponentSystem;
import engine3.render.entity.ICamera;
import engine3.scene.Scene;
import engine3.scene.SceneTree;
import org.joml.Vector2i;

/**
 * A renderer is attached to a scene, and is responsible for rendering the scene using a {@code ViewMatrix} and
 * {@code ViewPort}.
 * <p>
 * A Renderer uses a {@code Pipeline} to render a scene. This Pipeline can set the API-State, and call different
 * {@code RenderSystems} of a {@link Scene Scene's} ECS.
 */
public interface IRenderer {
  enum Stage {
    BEFORE,
    GEOMETRY,
    LIGHTING,
    AFTER
  }

  void registerRenderSystems(EntityComponentSystem ecs);

  /**
   * @param scene The scene to render.
   */
  void render(SceneTree scene, Vector2i viewport, ICamera camera);
}
