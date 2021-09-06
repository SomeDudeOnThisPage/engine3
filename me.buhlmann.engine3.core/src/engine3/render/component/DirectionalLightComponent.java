package engine3.render.component;

import engine3.entity.EntityComponent;
import org.joml.Vector3f;

public class DirectionalLightComponent extends EntityComponent {
  public Vector3f color;
  public Vector3f direction;

  public DirectionalLightComponent(Vector3f color, Vector3f direction) {
    this.color = color;
    this.direction = direction;
  }
}
