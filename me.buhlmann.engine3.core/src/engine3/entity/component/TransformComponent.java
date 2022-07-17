package engine3.entity.component;

import engine3.entity.EntityComponent;
import org.joml.*;

@SuppressWarnings("unused")
public class TransformComponent extends EntityComponent {
  public Vector3f position;
  // public Vector3f rotation;
  public Quaternionf rotation;
  public Vector3fc scale;

  private final Matrix4f transform;

  public void setTransformMatrix(Matrix4f matrix) {
    this.transform.set(matrix);
    this.position = matrix.getTranslation(new Vector3f());
    this.rotation = matrix.getRotation(new AxisAngle4f()).get(new Quaternionf());
    this.scale = matrix.getScale(new Vector3f());
  }

  public Matrix4f getTransformMatrix() {
    return this.transform.identity()
        .rotate(this.rotation)
        .translate(this.position)
        .scale(this.scale);
  }

  public TransformComponent() {
    this(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
  }

  public TransformComponent(Vector3f position) {
    this(position, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
  }

  public TransformComponent(Vector3f position, Vector3f rotation) {
    this(position, rotation, new Vector3f(1.0f, 1.0f, 1.0f));
  }

  public TransformComponent(Vector3f position, Vector3f rotation, Vector3f scale) {
    this.transform = new Matrix4f();

    this.position = position;
    this.rotation = new Quaternionf().rotateXYZ(rotation.x, rotation.y, rotation.z);
    this.scale = scale;
  }
}
