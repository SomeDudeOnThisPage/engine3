package editor.components;

import engine3.entity.EntityComponent;
import org.joml.Vector3f;

public class RotationComponent extends EntityComponent {
  public Vector3f speed;

  public RotationComponent(Vector3f speed) {
    this.speed = new Vector3f(speed);
  }
}
