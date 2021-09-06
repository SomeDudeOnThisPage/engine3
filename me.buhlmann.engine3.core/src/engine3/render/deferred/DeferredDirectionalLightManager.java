package engine3.render.deferred;

import engine3.entity.EntityCollection;
import engine3.entity.api.IEntityComponent;
import engine3.entity.api.IEntitySystem;
import engine3.entity.api.IRenderSystem;
import engine3.render.IRenderer;

public class DeferredDirectionalLightManager extends EntityCollection implements IEntitySystem, IRenderSystem<DeferredRenderer> {
  @Override
  public <T extends IEntityComponent> Class<T>[] components() {
    return new Class[0];
  }

  @Override
  public void update(float dt) {

  }

  @Override
  public IRenderer.Stage stage() {
    return null;
  }

  @Override
  public void render(DeferredRenderer renderer) {

  }
}
