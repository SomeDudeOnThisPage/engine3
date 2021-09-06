package engine3.api;

import engine3.entity.EntityComponentSystem;

public interface IApplication {
  void onInit();
  void onTick(float ft);
  void onRender();

  void destroy();
}
