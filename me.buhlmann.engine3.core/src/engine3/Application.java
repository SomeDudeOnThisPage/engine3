package engine3;

import engine3.api.IApplication;
import engine3.entity.EntityComponentSystem;
import engine3.gfx.OpenGL;
import engine3.input.IInputManager;
import engine3.render.IRenderer;
import engine3.scene.IScene;

public abstract class Application implements IApplication {
  protected final String root;
  protected boolean close;

  protected IScene<?> scene;
  protected EntityComponentSystem ecs;

  protected int fps;
  protected float ft;

  public abstract void onInit();
  public abstract void onTick(float ft);
  public abstract void onRender();

  public abstract IInputManager getInput();

  public final float getFrameTime() {
    return this.ft;
  }

  public final void run() {
    double last = System.currentTimeMillis();
    double lastFPS = System.currentTimeMillis();
    this.fps = 0;

    while (!Engine4.getDisplay().shouldClose() && !this.close) {
      try {
        double time = System.currentTimeMillis();
        double ft = time - last;
        this.fps++;

        if (ft <= 0) {
          ft = 0.000001;
        }

        Engine4.getEventBus().update();
        Engine4.getDisplay().poll();
        Engine4.getAssetManager().update();
        OpenGL.context().clear();
        this.onTick((float) ft);
        this.onRender();
        Engine4.getDisplay().swapBuffers();

        last = time;

        if (time - lastFPS >= 1000.0) {
          System.out.println(this.fps);
          this.ft = 1000.0f / this.fps;
          this.fps = 0;
          lastFPS = System.currentTimeMillis();
        }

        //Thread.sleep(1000 / 512);
      } catch(Exception e) {
        Engine4.getDisplay().close();
        e.printStackTrace();
      }
    }
  }

  public void onDestroy() {
  }

  public Application(String root) {
    this.root = root;
    this.close = false;

    this.ecs = new EntityComponentSystem();
  }
}
