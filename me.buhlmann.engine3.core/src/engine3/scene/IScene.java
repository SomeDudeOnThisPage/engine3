package engine3.scene;

import engine3.entity.EntityComponentSystem;
import engine3.render.entity.ICamera;
import engine3.render.IRenderer;

public interface IScene<T extends IRenderer> {
  class SceneLifecycleException extends Exception {}
  class SceneUpdateException extends SceneLifecycleException {} // TODO: info about entity / system that caused the error
  class SceneRenderException extends SceneLifecycleException {} // TODO: info about rendering error

  void initialize() throws SceneLifecycleException;
  void enter();
  void exit();
  void update(final float ft) throws SceneUpdateException;
  void render() throws SceneRenderException;
  void destroy() throws SceneLifecycleException;

  ICamera getViewportCamera();
  IRenderer getRenderer();
  EntityComponentSystem getECS();
}
