package engine3.input;

import engine3.event.EventBus;
import engine3.events.CursorMovementInputEvent;
import engine3.platform.GLFWWindow;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class InputEventManager {

  private static final boolean ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT = false;

  private final Vector2i last;
  private final Vector2i now;

  public void initialize(GLFWWindow window) {
    GLFW.glfwSetCursorPosCallback(window.getHandle(), (handle, x, y) -> this.now.set((int) x, (int) y));
  }

  public void publishFrameInputEvents(EventBus bus) {
    if (InputEventManager.ALWAYS_PUBLISH_CURSOR_MOVEMENT_EVENT || !this.now.equals(this.last)) {
      bus.publish(new CursorMovementInputEvent(this.now.x, this.now.y, this.last.x, this.last.y));
    }

    this.last.set(this.now);
  }

  public InputEventManager() {
    this.last = new Vector2i();
    this.now = new Vector2i();
  }
}
