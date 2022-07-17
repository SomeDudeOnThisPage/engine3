package engine3.render.entity;

import engine3.entity.Entity;
import engine3.entity.component.TransformComponent;
import engine3.render.component.ProjectionComponent;
import org.joml.Matrix4f;
import org.joml.Vector4i;

public abstract class Camera extends Entity implements ICamera {
  private final TransformComponent transform;
  private final ProjectionComponent projection;

  protected final Vector4i viewport;

  @Override
  public Vector4i getViewportDimensions() {
    return this.viewport;
  }

  @Override
  public TransformComponent getTransform() {
    return this.transform;
  }

  @Override
  public ProjectionComponent getProjection() {
    return this.projection;
  }

  @Override
  public void setViewportDimensions(Vector4i viewport) {
    this.viewport.set(viewport);
  }

  public Camera(Matrix4f projection, Vector4i viewport) {
    this.transform = new TransformComponent();
    this.projection = new ProjectionComponent(projection);
    this.addComponent(this.transform);
    this.addComponent(this.projection);

    this.viewport = new Vector4i().set(viewport);
  }
}
