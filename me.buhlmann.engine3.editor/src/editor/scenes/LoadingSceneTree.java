package editor.scenes;

import editor.components.RotationComponent;
import editor.components.control.CameraControlComponent;
import engine3.Engine3;
import engine3.asset.AssetReference;
import engine3.asset.loading.Assimp;
import engine3.entity.Entity;
import engine3.entity.api.IEntity;
import engine3.entity.component.GeometryComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.material.FlatColorPBRMaterial;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;
import engine3.gfx.primitives.Model;
import engine3.gfx.shader.ShaderProgram;
import engine3.gfx.shader.ShaderProgramFactory;
import engine3.gfx.texture.Texture2D;
import engine3.gfx.texture.TextureFactory;
import engine3.gfx.uniform.UniformBuffer;
import engine3.render.IRenderer;
import engine3.render.entity.Camera3D;
import engine3.render.entity.ICamera;
import engine3.scene.SceneTree;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class LoadingSceneTree extends SceneTree {
  private ICamera camera;

  @Override
  public ICamera getViewportCamera() {
    return this.camera;
  }

  @Override
  public void onInit() {
    // gl assets
    Engine3.ASSET_MANAGER.registerAssetType(ShaderProgram.class, new ShaderProgramFactory());
    Engine3.ASSET_MANAGER.registerAssetType(Texture2D.class, new TextureFactory());
    Engine3.ASSET_MANAGER.registerAssetType(VertexArray.class);
    Engine3.ASSET_MANAGER.registerAssetType(UniformBuffer.class);

    // primitive rendering assets
    Engine3.ASSET_MANAGER.registerAssetType(Material.class);
    Engine3.ASSET_MANAGER.registerAssetType(Mesh.class);
    Engine3.ASSET_MANAGER.registerAssetType(Model.class);

    Engine3.ASSET_MANAGER.load("platform/splash/template.xml");

    VertexArray vao = Assimp.load("platform/resources/models/cube.obj");
    Engine3.ASSET_MANAGER.load(AssetReference.wrap("vao.cube", vao, VertexArray.class));

    Material material = new FlatColorPBRMaterial(new Vector3f(1.0f, 1.0f, 1.0f), 0.8f, 0.01f, 0.02f, 0.0f);
    Engine3.ASSET_MANAGER.load(AssetReference.wrap("material.white-plastic", material, Material.class));

    final Mesh cubeMesh = new Mesh("vao.cube", "material.white-plastic");
    Engine3.ASSET_MANAGER.load(AssetReference.wrap("mesh.cube", cubeMesh, Mesh.class));

    final Model cubeModel = new Model("mesh.cube");
    Engine3.ASSET_MANAGER.load(AssetReference.wrap("model.cube", cubeModel, Model.class));

    IEntity cube = new Entity("cube")
        .addComponent(new TransformComponent(new Vector3f(3.0f, 0.0f, 0.0f)))
        .addComponent(new GeometryComponent("model.cube"))
        .addComponent(new RotationComponent(new Vector3f(0.0f, 0.0f, 0.01f)));

    this.getEntityComponentSystem().add(cube);

    this.camera = new Camera3D(90.0f, 1, 0.1f, 1000.0f, new Vector4i(0, 0, 500, 500));
    this.camera.addComponent(new CameraControlComponent());
    this.camera.getTransform().position.z -= 4.0f;
    this.getEntityComponentSystem().add(this.camera);
  }

  @Override
  public void onRender(float dt) {

  }

  @Override
  public void onUpdate(float dt) {

  }

  public <T extends IRenderer> LoadingSceneTree(T renderer) {
    super(renderer);
  }
}