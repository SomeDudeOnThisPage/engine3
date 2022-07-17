package engine3.scene;

import engine3.entity.EntityComponentSystem;
import engine3.render.IRenderer;

public abstract class AbstractScene<T extends IRenderer> implements IScene<T> {
  private final EntityComponentSystem ecs;
  private final T renderer;
  private boolean running = false;

  protected abstract void onInitialization();
  protected abstract void onEnter();
  protected abstract void onTick(final float ft);
  protected abstract void onDestroy();

  @Override
  public final void initialize() throws SceneLifecycleException {
    try {
      this.onInitialization();
      this.ecs.update(0.0f, null);
    } catch (Exception e) {
      e.printStackTrace();
      throw new SceneLifecycleException();
    }
  }

  @Override
  public final void enter() {
    this.onEnter();
  }

  @Override
  public void update(final float ft) throws SceneUpdateException {
    try {
      this.onTick(ft);
      this.ecs.update(ft, null);
    } catch (Exception e) {
      e.printStackTrace();
      throw new SceneUpdateException();
    }
  }

  @Override
  public IRenderer getRenderer() {
    return this.renderer;
  }

  @Override
  public final void render() throws SceneRenderException {
    try {
      this.renderer.render(this.getViewportCamera());
    } catch (Exception e) {
      e.printStackTrace();
      throw new SceneRenderException();
    }
  }

  @Override
  public final void destroy() throws SceneLifecycleException {
    try {
      this.onDestroy();
    } catch (Exception e) {

    }
  }

  @Override
  public EntityComponentSystem getECS() {
    return this.ecs;
  }

  public AbstractScene(T renderer) {
    this.ecs = new EntityComponentSystem();
    this.renderer = renderer;
  }
}
