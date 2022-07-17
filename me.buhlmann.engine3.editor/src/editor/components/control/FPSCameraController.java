package editor.components.control;

import engine3.Engine4;
import engine3.entity.EntityScript;
import engine3.entity.component.TransformComponent;
import engine3.input.IInputManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FPSCameraController extends EntityScript {
  private static final float SPEED = 0.05f;
  private Vector2f mouseLast = new Vector2f(0.0f);

  private float yaw = 0.0f;
  private float pitch = 0.0f;

  @Override
  public void update(float dt) {
    final TransformComponent transform = this.getEntity().getComponent(TransformComponent.class);
    final Matrix4f matrix = transform.getTransformMatrix();

    Vector3f forward = transform.rotation.positiveZ(new Vector3f()).normalize().mul(SPEED * dt);
    Vector3f left = transform.rotation.positiveX(new Vector3f()).normalize().mul(SPEED * dt);
    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f).mul(SPEED * dt);

    final Vector3f movement = new Vector3f(0.0f, 0.0f, 0.0f);
    if (Engine4.getInputManager().isKeyPressed(IInputManager.KeyCode.W)) {
      transform.position.add(forward);
    } else if (Engine4.getInputManager().isKeyPressed(IInputManager.KeyCode.S)) {
      transform.position.add(forward.negate());
    }

    if (Engine4.getInputManager().isKeyPressed(IInputManager.KeyCode.A)) {
      transform.position.add(left);
    } else if (Engine4.getInputManager().isKeyPressed(IInputManager.KeyCode.D)) {
      transform.position.add(left.negate());
    }

    // matrix.translateLocal(movement);
    // transform.setTransformMatrix(matrix);
    // transform.position.add(transform);

    if (Engine4.getInputManager().isMouseButton1Pressed()) {
      Vector2f mouseNew = Engine4.getInputManager().getMousePosition();
      Vector2f drag = mouseLast.sub(mouseNew);
      this.mouseLast = mouseNew;

      this.yaw   -= drag.x * 0.5f;
      this.pitch -= drag.y * 0.5f;

      transform.rotation.identity()
          .rotateX((float) Math.toRadians(this.pitch))
          .rotateY((float) Math.toRadians(this.yaw));
    } else {
      mouseLast = Engine4.getInputManager().getMousePosition();
    }
  }
}
