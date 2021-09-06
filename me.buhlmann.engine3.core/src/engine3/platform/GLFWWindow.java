package engine3.platform;

import engine3.Engine3;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class GLFWWindow {
  private final long handle;

  private VSyncState vsync;
  private WindowState state;
  private final Vector2i size;

  public float getAspectRatio() {
    return (float) this.size.x / (float) this.size.y;
  }

  public long getHandle() {
    return this.handle;
  }

  public void poll() {
    glfwPollEvents();
  }

  public void swapBuffers() {
    glfwSwapBuffers(this.handle);
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(this.handle);
  }

  public void close() {
    glfwSetWindowShouldClose(this.handle, true);
  }

  public Vector2i getSize() {
    return this.size;
  }

  public void terminate() {
    glfwTerminate();
  }

  public GLFWWindow(Vector2i size) {
    if (!glfwInit()) {
      System.err.println("could not initialize GLFW");
      System.exit(-1);
    }

    this.size = size;
    this.state = WindowState.WINDOWED;
    this.vsync = Engine3.CONFIGURATION.vsync ? VSyncState.ENABLED : VSyncState.DISABLED;

    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_SAMPLES, 8);
    this.handle = glfwCreateWindow(this.size.x, this.size.y, Engine3.CONFIGURATION.title, NULL, NULL);

    glfwSetWindowSizeCallback(this.handle, (handle, x, y) -> this.size.set(x, y));

    glfwMakeContextCurrent(this.handle);
    glfwSwapInterval(this.vsync.getValue());
  }
}

