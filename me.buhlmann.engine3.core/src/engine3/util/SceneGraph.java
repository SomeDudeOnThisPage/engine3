package engine3.util;

import engine3.entity.api.IEntity;
import engine3.entity.component.TransformComponent;
import org.joml.Matrix4f;

public final class SceneGraph {
  public static Matrix4f transform(IEntity entity) {
    if (!entity.hasComponent(TransformComponent.class)) {
      return new Matrix4f().identity();
    }

    if (entity.getParent() == null) {
      return entity.getComponent(TransformComponent.class).getTransformMatrix();
    }

    return new Matrix4f(entity.getComponent(TransformComponent.class).getTransformMatrix()).mul(SceneGraph.transform(entity.getParent()));
  }
}
