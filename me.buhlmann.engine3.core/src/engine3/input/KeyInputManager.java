package engine3.input;

import engine3.platform.GLFWWindow;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class KeyInputManager {
  private final Map<Integer, Integer> state;

  public void initialize(GLFWWindow window) {
    glfwSetKeyCallback(window.getHandle(), (handle, key, scancode, action, mods) -> {
      if (this.state.getOrDefault(key, -1) != action) {
        this.state.put(key, action);
      }
    });
  }

  public int getKeyState(int glfwKeyCode) {
    return this.state.getOrDefault(glfwKeyCode, GLFW_RELEASE);
  }

  public boolean isKeyPressed(int glfwKeyCode) {
    int state = this.state.getOrDefault(glfwKeyCode, -1);
    return state != -1 && state != GLFW_RELEASE;
  }

  public KeyInputManager() {
    this.state = new HashMap<>();
  }
}
