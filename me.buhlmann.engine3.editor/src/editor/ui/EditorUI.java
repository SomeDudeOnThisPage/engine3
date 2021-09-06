package editor.ui;

import editor.imgui.ImGUI;
import imgui.ImGui;
import imgui.flag.ImGuiDir;
import imgui.type.ImInt;

public abstract class EditorUI {
  public enum ChildPosition {
    UP(ImGuiDir.Up, 0),
    DOWN(ImGuiDir.Down, 1),
    LEFT(ImGuiDir.Left, 2),
    RIGHT(ImGuiDir.Right, 3);

    public int index;
    public int position;

    ChildPosition(int position, int index) {
      this.position = position;
      this.index = index;
    }
  }

  protected boolean initialized;
  protected final EditorUI[] children;
  protected final int[] nodes;

  protected ChildPosition position;

  protected final String title;
  protected final int flags;

  public abstract void render();

  private void splitParent(EditorUI parent, ChildPosition position) {
    if (this.nodes[position.index] == 0) {
      ImInt i0 = new ImInt();
      ImInt i1 = new ImInt();

      int node = parent == null ? ImGUI.DOCKSPACE : ImGui.getID(parent.title);
      imgui.internal.ImGui.dockBuilderSplitNode(node, position.position, 0.5f, i0, i1);

      if (parent != null) {
        switch (position.index) {
          case 0:
            parent.nodes[0] = i0.get();
            parent.nodes[1] = i1.get();
          case 1:
            parent.nodes[1] = i0.get();
            parent.nodes[0] = i1.get();
          case 2:
            parent.nodes[2] = i0.get();
            parent.nodes[3] = i1.get();
          case 3:
            parent.nodes[3] = i0.get();
            parent.nodes[2] = i1.get();
        }
      }
    }
  }

  protected void split(int node, float ratio) {
    ImInt up = new ImInt();
    ImInt down = new ImInt();
    ImInt l = new ImInt();
    ImInt r = new ImInt();

    imgui.internal.ImGui.dockBuilderSplitNode(node, ImGuiDir.Up, ratio, up, down);
    // imgui.internal.ImGui.dockBuilderSplitNode(node, ImGuiDir.Left, ratio, l, r);

    this.nodes[ChildPosition.UP.index] = up.get();
    this.nodes[ChildPosition.DOWN.index] = down.get();
    // this.nodes[ChildPosition.LEFT.index] = left.get();
    // this.nodes[ChildPosition.RIGHT.index] = right.get();
  }

  private void initialize(EditorUI parent) {
    /*if (this.position != null) { // if we have a parent, we need to dock this window into the hierarchy
      this.splitParent(parent, this.position);
      int node = parent == null ? ImGUI.DOCKSPACE : ImGui.getID(parent.title);

      // System.out.println("NODE " + node + " DOCKSPACE " + ImGUI.DOCKSPACE);

      if (parent != null) {
        imgui.internal.ImGui.dockBuilderDockWindow(this.title, parent.nodes[this.position.index]);
      } else {
        imgui.internal.ImGui.dockBuilderDockWindow(this.title, ImGUI.DOCKSPACE);
      }
      imgui.internal.ImGui.dockBuilderFinish(node);
    }*/

    // this.initialized = true;
  }

  public final void addChild(final EditorUI child, final ChildPosition position) {
    this.children[position.index] = child;
    child.position = position;
  }

  public final void render(EditorUI parent) {
    if (!this.initialized) {
      this.initialize(parent);
    }

    this.render();

    for (EditorUI child : this.children) {
      if (child != null) {
        child.render(parent);
      }
    }
  }

  public EditorUI(String title, int flags, ChildPosition position) {
    this.children = new EditorUI[4];
    this.nodes = new int[4];
    this.title = title;
    this.flags = flags;
    this.position = position;
  }
}
