package editor;

import editor.imgui.EditorConsole;
import editor.imgui.ImGUI;
import editor.imgui.TestGUI;
import editor.input.EditorInputManager;
import editor.scenes.LoadingSceneTree;
import editor.ui.ConsoleWindow;
import editor.ui.EditorUI;
import editor.ui.EditorUIBase;
import editor.ui.ViewportWindow;
import engine3.Application;
import engine3.Engine3;
import engine3.input.IInputManager;
import engine3.render.deferred.DeferredRenderer;
import imgui.flag.ImGuiWindowFlags;

public final class EditorApplication extends Application {
  private IInputManager input;
  private boolean first = true;

  private EditorUIBase base;

  private ViewportWindow viewport;
  private ConsoleWindow console;

  @Override
  public void onInit() {
    this.renderer = new DeferredRenderer();

    this.scene = new LoadingSceneTree(this.renderer);
    this.input = new EditorInputManager(this);
    this.input.initialize(Engine3.DISPLAY);

    ImGUI.initialize(Engine3.DISPLAY.getHandle());
    this.scene.onInit();

    this.base = new EditorUIBase(0.85f);
    this.viewport = new ViewportWindow(this.scene, this.renderer, EditorUI.ChildPosition.UP, this.scene.getEntityComponentSystem());
    this.console = new ConsoleWindow("Console", ImGuiWindowFlags.NoMove, EditorUI.ChildPosition.DOWN);

    this.base.addChild(this.viewport, EditorUI.ChildPosition.UP);
    this.base.addChild(this.console, EditorUI.ChildPosition.DOWN);
  }

  @Override
  public void onTick(float ft) {
    ImGUI.preFrame(ft);

    /*if (this.first) {
      this.first = false;
      imgui.internal.ImGui.dockBuilderRemoveNode(ImGUI.DOCKSPACE);
      imgui.internal.ImGui.dockBuilderAddNode(ImGUI.DOCKSPACE, ImGuiDockNodeFlags.DockSpace);
      imgui.internal.ImGui.dockBuilderSetNodeSize(ImGUI.DOCKSPACE, Engine3.DISPLAY.getSize().x, Engine3.DISPLAY.getSize().y);

      ImInt top = new ImInt();
      ImInt bottom = new ImInt();

      imgui.internal.ImGui.dockBuilderSplitNode(ImGUI.DOCKSPACE, ImGuiDir.Up, 0.75f, top, bottom);
      imgui.internal.ImGui.dockBuilderDockWindow("Scene " + this.scene, top.get());
      imgui.internal.ImGui.dockBuilderDockWindow("Console", bottom.get());
      imgui.internal.ImGui.dockBuilderFinish(ImGUI.DOCKSPACE);
    }*/

    this.input.publishFrameInputEvents(Engine3.EVENT_BUS);
    this.scene.update(ft);
  }

  @Override
  public void onRender() {
    // render full scene
    this.renderer.render(this.scene, this.viewport.getSize(), this.scene.getViewportCamera());
    //this.sceneViewport.render(this.scene, this.renderer);

    // render the GUIS (including the scene viewport gui)
    ImGUI.renderGUIS(this.scene, this.renderer);

    this.base.render(null);

    // this.viewport.render(null);
    // this.console.render(null);

    // render GUIS to screen
    ImGUI.postFrame();
  }

  @Override
  public IInputManager getInput() {
    return this.input;
  }

  public EditorApplication() {
    super("editor");
  }

  public static void main(String[] args) {
    Engine3.run(EditorApplication.class, args);
  }
}
