package engine3.input;

import engine3.event.EventBus;
import engine3.platform.GLFWWindow;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public interface IInputManager {

  @SuppressWarnings("unused")
  enum KeyCode {
    UNKNOWN(GLFW_KEY_UNKNOWN),
    SPACE(GLFW_KEY_SPACE),
    APOSTROPHE(GLFW_KEY_APOSTROPHE),
    COMMA(GLFW_KEY_COMMA),
    MINUS(GLFW_KEY_MINUS),
    PERIOD(GLFW_KEY_PERIOD),
    SLASH(GLFW_KEY_SLASH),
    KEY_0(GLFW_KEY_0),
    KEY_1(GLFW_KEY_1),
    KEY_2(GLFW_KEY_2),
    KEY_3(GLFW_KEY_3),
    KEY_4(GLFW_KEY_4),
    KEY_5(GLFW_KEY_5),
    KEY_6(GLFW_KEY_6),
    KEY_7(GLFW_KEY_7),
    KEY_8(GLFW_KEY_8),
    KEY_9(GLFW_KEY_9),
    EQUAL(GLFW_KEY_EQUAL),
    A(GLFW_KEY_A),
    B(GLFW_KEY_B),
    C(GLFW_KEY_C),
    D(GLFW_KEY_D),
    E(GLFW_KEY_E),
    F(GLFW_KEY_F),
    G(GLFW_KEY_G),
    H(GLFW_KEY_H),
    I(GLFW_KEY_I),
    J(GLFW_KEY_J),
    K(GLFW_KEY_K),
    L(GLFW_KEY_L),
    M(GLFW_KEY_M),
    N(GLFW_KEY_N),
    O(GLFW_KEY_O),
    P(GLFW_KEY_P),
    Q(GLFW_KEY_Q),
    R(GLFW_KEY_R),
    S(GLFW_KEY_S),
    T(GLFW_KEY_T),
    U(GLFW_KEY_U),
    V(GLFW_KEY_V),
    W(GLFW_KEY_W),
    X(GLFW_KEY_X),
    Y(GLFW_KEY_Y),
    Z(GLFW_KEY_Z);

    private final int code;

    public final int getCode() {
      return this.code;
    }

    KeyCode(int code) {
      this.code = code;
    }
  }

  @SuppressWarnings("unused")
  enum KeyAction {
    PRESS(GLFW_PRESS),
    RELEASE(GLFW_RELEASE),
    REPEAT(GLFW_REPEAT);

    private final int action;

    public int action() {
      return this.action;
    }

    KeyAction(int action) {
      this.action = action;
    }
  }

  void initialize(GLFWWindow window);
  void publishFrameInputEvents(EventBus bus);
  boolean isKeyPressed(IInputManager.KeyCode key);
  Vector2f getMousePosition();

  boolean isMouseButton1Pressed();
}
