package editor.components.control;

import engine3.Engine3;
import engine3.entity.EntityScript;
import engine3.entity.component.TransformComponent;
import engine3.input.IInputManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraControlComponent extends EntityScript {
  @Override
  public void update(float dt) {
    final TransformComponent transform = this.getEntity().getComponent(TransformComponent.class);
    final Matrix4f matrix = transform.getTransformMatrix();

    final Vector3f movement = new Vector3f(0.0f, 0.0f, 0.0f);
    if (Engine3.input().isKeyPressed(IInputManager.KeyCode.W)) {
      movement.add(0.0f, 0.0f, 1.0f);
    } else if (Engine3.input().isKeyPressed(IInputManager.KeyCode.S)) {
      movement.add(0.0f, 0.0f, -1.0f);
    }

    if (Engine3.input().isKeyPressed(IInputManager.KeyCode.A)) {
      movement.add(1.0f, 0.0f, 0.0f);
    } else if (Engine3.input().isKeyPressed(IInputManager.KeyCode.D)) {
      movement.add(-1.0f, 0.0f, 0.0f);
    }

    matrix.translateLocal(movement);
    transform.setTransformMatrix(matrix);
  }
}
