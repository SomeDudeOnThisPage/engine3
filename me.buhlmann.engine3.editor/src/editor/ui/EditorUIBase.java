package editor.ui;

import engine3.Engine3;
import imgui.ImGui;
import imgui.flag.*;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.Objects;

public class EditorUIBase extends EditorUI {
  private final float splitRatioUD;
  public EditorUIBase(float splitRatioUD) {
    super("Editor", ImGuiWindowFlags.NoCollapse
        | ImGuiWindowFlags.MenuBar
        | ImGuiWindowFlags.NoResize
        | ImGuiWindowFlags.NoMove
        | ImGuiWindowFlags.NoTitleBar
        | ImGuiWindowFlags.NoDocking
        | ImGuiWindowFlags.NoBringToFrontOnFocus
        | ImGuiWindowFlags.NoBackground, null);

    this.splitRatioUD = splitRatioUD;
  }

  public void construct() {
    int node = this.BASE;
    imgui.internal.ImGui.dockBuilderRemoveNode(node);
    imgui.internal.ImGui.dockBuilderAddNode(node, imgui.internal.flag.ImGuiDockNodeFlags.DockSpace);
    imgui.internal.ImGui.dockBuilderSetNodeSize(node, Engine3.DISPLAY.getSize().x, Engine3.DISPLAY.getSize().y);

    // split based on children
    if (Arrays.stream(this.children).anyMatch(Objects::nonNull)) {
      this.split(this.BASE, this.splitRatioUD);
    }

    for (int i = 0; i < 4; i++) {
      if (this.children[i] == null) continue;
      imgui.internal.ImGui.dockBuilderDockWindow(this.children[i].title, this.nodes[i]);
    }

    imgui.internal.ImGui.dockBuilderFinish(node);
  }

  public int BASE = 0;
  @Override
  public void render() {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

    Vector2i size = Engine3.DISPLAY.getSize();
    ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Always);
    ImGui.setNextWindowPos(0, 0, ImGuiCond.Once);
    ImGui.setNextWindowBgAlpha(1.0f);

    ImGui.begin(this.title, this.flags);
    ImGui.popStyleVar(3);

    this.BASE = ImGui.getID("BASE" + this);
    ImGui.dockSpace(this.BASE, 0.0f, 0.0f, ImGuiDockNodeFlags.PassthruCentralNode);

    if (!this.initialized) {
      this.initialized = true;
      this.construct();
    }

    ImGui.end();
  }
}
