package editor.scenes;

import editor.components.RotationComponent;
import editor.components.control.FPSCameraController;
import engine3.Engine4;
import engine3.asset.AssetManager;
import engine3.asset.AssetReference;
import engine3.entity.Entity;
import engine3.entity.EntityComponentSystem;
import engine3.entity.api.IEntity;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.material.FlatColorPBRMaterial;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Model;
import engine3.render.entity.Camera3D;
import engine3.render.entity.ICamera;
import engine3.render.IRenderer;
import engine3.scene.AbstractScene;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class EditorScene<T extends IRenderer> extends AbstractScene<T> {
  private Camera3D camera;

  @Override
  public void exit() {
    this.onExit();
  }

  @Override
  public ICamera getViewportCamera() {
    this.camera.getProjection().setAspectRatio(Engine4.getDisplay().getAspectRatio());
    this.camera.setViewportDimensions(new Vector4i(0, 0, Engine4.getDisplay().getSize().x, Engine4.getDisplay().getSize().y));
    return this.camera;
  }

  @Override
  protected void onInitialization() {
    final AssetManager asm = Engine4.getAssetManager();

    // VertexArray vao = Assimp.load("platform/resources/models/cube.obj");
    // asm.load(AssetReference.wrap("vao.cube", vao, VertexArray.class));

    final Material material_flatColorRed = new FlatColorPBRMaterial(
        new Vector3f(1.0f, 0.0f, 0.0f),
        1.0f, 0.0f, 0.01f, 0.0f
    );
    asm.load(AssetReference.wrap("material.flat-color.red", material_flatColorRed, Material.class));

    // final Mesh cubeMesh = new Mesh("vao.cube", "material.flat-color.red");
    // asm.load(AssetReference.wrap("mesh.cube", cubeMesh, Mesh.class));

    final Model cubeModel = new Model("mesh.cube");
    asm.load(AssetReference.wrap("model.cube", cubeModel, Model.class));
  }

  @Override
  protected void onEnter() {
    final EntityComponentSystem ecs = this.getECS();
    final IEntity test = new Entity("test")
        .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -10.0f)))
        .addComponent(new GeometryComponent("model.cube"))
        .addComponent(new RotationComponent(new Vector3f(0.0f, 0.0f, 0.001f)));

    final IEntity test2 = new Entity("test-child")
        .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -10.0f)))
        .addComponent(new GeometryComponent("model.cube"))
        .addComponent(new RotationComponent(new Vector3f(0.1f, 0.0f, 0.001f)));

    test.addChild(test2);
    ecs.add(test);

    this.camera = new Camera3D(90.0f, Engine4.getDisplay().getAspectRatio(), 0.1f, 1000.0f, new Vector4i(0, 0, Engine4.getDisplay().getSize().x, Engine4.getDisplay().getSize().y));
    this.camera.addComponent(new FPSCameraController());
    this.getECS().add(this.camera);
  }

  protected void onExit() {
    this.getECS().clear();
  }

  @Override
  protected void onTick(final float ft) {
    final IEntity test = this.getECS().get("test");
    test.getComponent(TransformComponent.class).rotation.rotateLocalZ(
        test.getComponent(RotationComponent.class).speed.z * ft
    );
  }

  @Override
  protected void onDestroy() {

  }

  public EditorScene(T renderer) {
    super(renderer);
  }
}
