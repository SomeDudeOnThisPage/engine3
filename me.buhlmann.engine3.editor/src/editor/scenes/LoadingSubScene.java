package editor.scenes;

import editor.components.control.CameraControlComponent;
import engine3.Engine3;
import engine3.entity.Entity;
import engine3.entity.EntityComponentSystem;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.input.IInputManager;
import engine3.render.entity.Camera3D;
import engine3.render.entity.ICamera;
import engine3.scene.SceneEntity;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class LoadingSubScene extends SceneEntity {
  private ICamera camera;

  @Override
  public void onTick(IInputManager input) {

  }

  @Override
  public void onInit(EntityComponentSystem ecs) {
    for (int i = 0; i < 100; i++) {
      this.addChild(new Entity()
          .addComponent(new TransformComponent(new Vector3f((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50)))
          .addComponent(new GeometryComponent("model.cube"))
      );
    }

    this.camera = new Camera3D(90.0f, Engine3.DISPLAY.getAspectRatio(), 0.1f, 100.0f, new Vector4i());
    this.addChild(this.camera);
    this.camera.addComponent(new CameraControlComponent());
  }

  @Override
  public ICamera getViewportCamera() {
    return this.camera;
  }
}
