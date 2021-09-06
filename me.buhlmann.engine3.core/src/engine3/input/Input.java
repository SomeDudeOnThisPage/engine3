package engine3.input;

import engine3.Engine3;
import engine3.event.EventBus;
import engine3.events.CursorMovementInputEvent;
import engine3.platform.GLFWWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Input implements IInputManager {

  private final Map<Integer, Integer> state;

  private static final boolean ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT = false;

  private final Vector2i last;
  private final Vector2i now;

  @Override
  public void initialize(GLFWWindow window) {
    glfwSetCursorPosCallback(window.getHandle(), (handle, x, y) -> this.now.set((int) x, (int) y));

    glfwSetKeyCallback(window.getHandle(), (handle, key, scancode, action, mods) -> {
      if (this.state.getOrDefault(key, -1) != action) {
        this.state.put(key, action);
      }
    });
  }

  @Override
  public void publishFrameInputEvents(EventBus bus) {
    if (Input.ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT || !this.now.equals(this.last)) {
      bus.publish(new CursorMovementInputEvent(this.now.x, this.now.y, this.last.x, this.last.y));
    }

    this.last.set(this.now);
  }

  public final boolean isKeyPressed(IInputManager.KeyCode key) {
    int state = this.state.getOrDefault(key.getCode(), -1);
    return state != -1 && state != GLFW_RELEASE;
  }

  public final Vector2f getMousePosition() {
    double[] x = new double[1];
    double[] y = new double[1];
    glfwGetCursorPos(Engine3.DISPLAY.getHandle(), x, y);

    return new Vector2f((float) x[0], (float) y[0]);
  }

  public Input() {
    this.last = new Vector2i();
    this.now = new Vector2i();
    this.state = new HashMap<>();
  }
}
