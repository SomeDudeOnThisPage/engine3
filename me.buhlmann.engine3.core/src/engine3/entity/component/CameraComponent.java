package engine3.entity.component;

import engine3.entity.EntityComponent;
import org.joml.Matrix4f;

public abstract class CameraComponent extends EntityComponent {
  public abstract Matrix4f getViewMatrix();
}
