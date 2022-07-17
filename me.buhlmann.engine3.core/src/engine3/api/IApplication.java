package engine3.api;

import engine3.input.IInputManager;
import engine3.render.IRenderer;

public interface IApplication {
  void onInit();
  void onTick(float ft);
  void onRender();

  IRenderer getRenderer();
  IInputManager getInput();
  void run();
  void onDestroy();
}
