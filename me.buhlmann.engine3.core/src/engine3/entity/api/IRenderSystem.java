package engine3.entity.api;

import engine3.render.IRenderer;

public interface IRenderSystem<T extends IRenderer> extends IEntityCollection {
  IRenderer.Stage stage();

  void render(T renderer);
}
