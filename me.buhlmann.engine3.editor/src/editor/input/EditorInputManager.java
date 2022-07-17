package editor.input;

import editor.EditorApplication;
import editor.imgui.ImGUI;
import engine3.Engine4;
import engine3.event.EventBus;
import engine3.events.CursorMovementInputEvent;
import engine3.input.IInputManager;
import engine3.platform.GLFWWindow;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class EditorInputManager implements IInputManager {
  private final EditorApplication application;
  private final Map<Integer, Integer> state;

  private static final boolean ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT = false;

  private final Vector2i last;
  private final Vector2i now;

  private boolean m1;

  @Override
  public void initialize(GLFWWindow window) {
    glfwSetCursorPosCallback(window.getHandle(), (handle, x, y) -> this.now.set((int) x, (int) y));

    glfwSetMouseButtonCallback(window.getHandle(), (handle, button, action, mods) -> {
      if (button == 1 && action == GLFW_PRESS) {
        this.m1 = true;
      } else if (button == 1 && action == GLFW_RELEASE) {
        this.m1 = false;
      }

      ImGUI.setMouse(action, button);
    });

    glfwSetKeyCallback(window.getHandle(), (handle, key, scancode, action, mods) -> {
      if (this.state.getOrDefault(key, -1) != action) {
        this.state.put(key, action);
      }

      ImGuiIO io = ImGui.getIO();
      if (action == GLFW_PRESS) {
        io.setKeysDown(key, true);
      } else if (action == GLFW_RELEASE) {
        io.setKeysDown(key, false);
      }

      io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
      io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
      io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
      io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
    });
  }

  @Override
  public void publishFrameInputEvents(EventBus bus) {
    // do not publish any events to the game if our scene view is not focused
    // if (!this.application.getMainSceneGUI().isFocused()) return;

    if (EditorInputManager.ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT || !this.now.equals(this.last)) {
      bus.publish(new CursorMovementInputEvent(this.now.x, this.now.y, this.last.x, this.last.y));
    }

    this.last.set(this.now);
  }

  public final boolean isKeyPressed(IInputManager.KeyCode key) {
    // do not forward any input key events to the game if our scene view is not focused
    // if (!this.application.getMainSceneGUI().isFocused()) return false;

    int state = this.state.getOrDefault(key.getCode(), -1);
    return state != -1 && state != GLFW_RELEASE;
  }

  public final Vector2f getMousePosition() {
    double[] x = new double[1];
    double[] y = new double[1];
    glfwGetCursorPos(Engine4.getDisplay().getHandle(), x, y);

    return new Vector2f((float) x[0], (float) y[0]);
  }

  @Override
  public boolean isMouseButton1Pressed() {
    return this.m1;
  }

  public EditorInputManager(EditorApplication application) {
    this.last = new Vector2i();
    this.now = new Vector2i();
    this.state = new HashMap<>();
    this.application = application;

    this.m1 = false;
  }
}
