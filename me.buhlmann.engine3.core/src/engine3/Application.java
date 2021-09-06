package engine3;

import engine3.api.IApplication;
import engine3.entity.EntityComponentSystem;
import engine3.gfx.OpenGL;
import engine3.input.IInputManager;
import engine3.render.IRenderer;
import engine3.render.IRenderer2;
import engine3.scene.Scene;
import engine3.scene.SceneEntity;
import engine3.scene.SceneTree;
import org.lwjgl.glfw.GLFW;

public abstract class Application implements IApplication {
  protected final String root;
  protected boolean close;

  protected IRenderer renderer;

  protected SceneTree scene;

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

    while (!Engine3.DISPLAY.shouldClose() && !this.close) {
      try {
        double time = System.currentTimeMillis();
        double ft = time - last;
        this.fps++;

        if (ft <= 0) {
          ft = 0.000001;
        }

        Engine3.EVENT_BUS.update();
        Engine3.DISPLAY.poll();
        Engine3.ASSET_MANAGER.update();
        OpenGL.context().clear();
        this.onTick((float) ft);
        this.onRender();
        Engine3.DISPLAY.swapBuffers();

        last = time;

        if (time - lastFPS >= 1000.0) {
          System.out.println(this.fps);
          this.ft = 1000.0f / this.fps;
          this.fps = 0;
          lastFPS = System.currentTimeMillis();
        }

        Thread.sleep(1000 / 144);
      } catch(Exception e) {
        Engine3.DISPLAY.close();
        e.printStackTrace();
      }
    }
  }

  public final void destroy() {
  }

  public Application(String root) {
    this.root = root;
    this.close = false;
  }
}
