package engine3.render.entity;

import engine3.entity.api.IEntity;
import engine3.entity.component.TransformComponent;
import engine3.render.component.ProjectionComponent;
import org.joml.Vector4i;

public interface ICamera extends IEntity {
  TransformComponent getTransform();
  ProjectionComponent getProjection();
  void setViewportDimensions(Vector4i viewport);
}
