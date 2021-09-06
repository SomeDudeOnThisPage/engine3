package engine3.render.component;

import engine3.entity.EntityComponent;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class ProjectionComponent extends EntityComponent {
  private float fov;
  private float aspect;
  private float near;
  private float far;

  private Matrix4f projection;

  public Matrix4f getProjectionMatrix() {
    return this.projection;
  }

  private void reload() {
    this.projection = new Matrix4f().perspective((float) Math.toRadians(this.fov), this.aspect, this.near, this.far);
  }

  public void setFov(final float fov) {
    this.fov = fov;
    this.reload();
  }

  public void setAspectRatio(float aspect) {
    this.aspect = aspect;
    this.reload();
  }

  public void setNearClippingPlane(final float near) {
    this.near = near;
    this.reload();
  }

  public void setFarClippingPlane(final float far) {
    this.far = far;
    this.reload();
  }

  public ProjectionComponent(Matrix4f projection) {
    this.projection = projection;
  }

  public ProjectionComponent(final float fov, final float aspect, final Vector2f planes) {
    this.fov = fov;
    this.aspect = aspect;
    this.near = planes.x;
    this.far = planes.y;

    this.reload();
  }
}
