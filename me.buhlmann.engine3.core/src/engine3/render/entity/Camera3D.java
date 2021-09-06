package engine3.render.entity;

import org.joml.Matrix4f;
import org.joml.Vector4i;

public class Camera3D extends Camera {
  public Camera3D(float fov, float aspect, float near, float far, Vector4i viewport) {
    super(new Matrix4f().perspective(fov, aspect, near, far), viewport);

    this.getProjection().setFov(fov);
    this.getProjection().setAspectRatio(aspect);
    this.getProjection().setNearClippingPlane(near);
    this.getProjection().setFarClippingPlane(far);
  }
}
