package editor;

import editor.components.control.CameraControlComponent;
import editor.imgui.ImGUI;
import editor.input.EditorInputManager;
import editor.scenes.EditorScene;
import editor.ui.ViewportWindow;
import engine3.Application;
import engine3.Engine4;
import engine3.api.IApplication;
import engine3.asset.AssetManager;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;
import engine3.gfx.primitives.MeshFactory;
import engine3.gfx.primitives.Model;
import engine3.gfx.shader.ShaderProgram;
import engine3.gfx.shader.ShaderProgramFactory;
import engine3.gfx.texture.Texture2D;
import engine3.gfx.texture.TextureFactory;
import engine3.gfx.uniform.UniformBuffer;
import engine3.input.IInputManager;
import engine3.render.IRenderer;
import engine3.render.Renderer;
import engine3.render.entity.Camera3D;
import engine3.render.entity.ICamera;
import engine3.scene.IScene;
import org.joml.Vector4i;
import renderer.deferred.DeferredRenderer;

/**
 * Hooks another application onto its' Lifecycle.
 */
public final class EditorApplication extends Application {
  private final IApplication game;

  private IInputManager input;
  private IRenderer renderer;

  private final ViewportWindow viewport;

  private ICamera editorCamera;
  private boolean playing = false;

  @Override
  public void onInit() {
    final Renderer renderer = new Renderer();
    this.renderer = renderer;

    final AssetManager asm = Engine4.getAssetManager();
    asm.registerAssetType(ShaderProgram.class, new ShaderProgramFactory());
    asm.registerAssetType(Texture2D.class, new TextureFactory());
    asm.registerAssetType(VertexArray.class);
    asm.registerAssetType(UniformBuffer.class);
    asm.registerAssetType(Material.class);
    asm.registerAssetType(Mesh.class, new MeshFactory());
    asm.registerAssetType(Model.class);

    asm.addAssetManifest("editor/data");
    asm.loadManifestAssets("editor/data");
    asm.saveManifestAssets("editor/data");

    this.scene = new EditorScene<>(renderer);
    renderer.initialize(this.scene.getECS());

    try {
      this.scene.initialize();
    } catch (IScene.SceneLifecycleException e) {
      e.printStackTrace();
    }

    this.input = new EditorInputManager(this);
    this.input.initialize(Engine4.getDisplay());
    ImGUI.initialize(Engine4.getDisplay().getHandle());

    this.editorCamera = new Camera3D(90.0f, Engine4.getDisplay().getAspectRatio(), 0.1f, 1000.0f,
        new Vector4i(0, 0, this.viewport.size.x, this.viewport.size.y));
    this.editorCamera.addComponent(new CameraControlComponent());
    this.scene.getECS().add(this.editorCamera);

    this.scene.enter();
  }

  @Override
  public void onTick(float ft) {
    ImGUI.preFrame(ft);
    this.input.publishFrameInputEvents(Engine4.getEventBus());
    try {
      if (Engine4.getInputManager().isKeyPressed(IInputManager.KeyCode.Q)) {
        this.playing = true;
        this.scene.update(ft);
      } else {
        if (this.playing) {
          this.playing = false;
          this.scene.exit();
          this.scene.enter();
        }
        this.scene.getECS().update(ft, EditorTool.class);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onRender() {
    try {
      ImGUI.base();

      if (this.playing || this.getInput().isKeyPressed(IInputManager.KeyCode.Q)) {
        // render with whatever camera the scene should normally render
        this.scene.render();
      } else {
        // force rendering with editor camera
        this.editorCamera.getProjection().setAspectRatio((float) this.viewport.size.x / (float) this.viewport.size.y);
        this.editorCamera.setViewportDimensions(new Vector4i(0, 0, this.viewport.size.x, this.viewport.size.y));
        this.getRenderer().render(this.editorCamera);
      }

      this.viewport.render(this.renderer.getFramebuffer());

    } catch (IScene.SceneRenderException e) {
      e.printStackTrace();
    }
    ImGUI.postFrame();
  }

  @Override
  public IRenderer getRenderer() {
    return new DeferredRenderer();
  }

  @Override
  public IInputManager getInput() {
    return this.input;
  }

  public EditorApplication() {
    super("splash");
    this.viewport = new ViewportWindow();
    this.game = null;
  }

  public static void main(String[] args) {
    final Engine4<EditorApplication> engine = new Engine4<>();
    if (engine.initialize(args)) {
      engine.setApplication(new EditorApplication());
      engine.start();
    }
  }
}
