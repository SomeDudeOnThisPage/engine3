package engine3.scene;

import engine3.entity.Entity;
import engine3.entity.EntityComponentSystem;
import engine3.input.IInputManager;
import engine3.render.IRenderer;
import engine3.render.entity.ICamera;

public abstract class SceneEntity extends Entity {
  private IRenderer renderer;

  public abstract void onTick(IInputManager input);
  public abstract void onInit(EntityComponentSystem ecs);
  public abstract ICamera getViewportCamera();

  public void render() {
    // traverse children
    // render children (add to render queue?), (submit to renderer?)

    // render queue:
    // generate asynchronously
    // if finished, submit to renderer

    // render child scene (if child entity is a scene)
  }
}
