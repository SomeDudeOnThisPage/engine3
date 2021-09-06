package engine3.scene;

import engine3.entity.Entity;
import engine3.entity.EntityComponentSystem;
import engine3.entity.api.IEntity;
import engine3.input.IInputManager;
import engine3.render.IRenderer;
import engine3.render.entity.ICamera;
import org.joml.Vector2i;

/**
 * A {@link Scene} represents a world filled with Entities and Physics Objects / Constraints.
 * Thus, each {@link Scene} possesses its' own EntityComponentSystem and PhysicsSystem. The
 * {@link engine3.asset.Asset Assets} and {@link engine3.asset.AssetManager} are shared between
 * {@link Scene Scenes} though. Furthermore, each {@link Scene} possesses a Renderer. This Renderer may be
 * specific to that {@link Scene}, but may also be shared between scenes.
 */
public abstract class Scene {
  protected IEntity root;

  public abstract void onTick(IInputManager input);
  public abstract void onInit(EntityComponentSystem ecs);
  public abstract void onRender(Vector2i viewport);

  public abstract IRenderer getViewportRenderer();
  public abstract ICamera getViewportCamera();

  public Scene() {
    this.root = new Entity();
  }
}
